package com.silentlexx.instead;

import javax.microedition.khronos.egl.EGLConfig;

import javax.microedition.khronos.egl.*;

import android.app.*;
import android.content.*;
import android.view.*;
import android.os.*;
import android.util.Log;
import android.graphics.*;
import android.media.*;

/**
 * SDL Activity
 */
public class SDLActivity extends Activity {

	// Main components
	private static SDLActivity mSingleton;
	private static SDLSurface mSurface;
	private static Display display;
	
	// Audio
	private static Thread mAudioThread;
	private static AudioTrack mAudioTrack;
	
	private static String game = null;
	
	// Load the .so
	static {
		System.loadLibrary("SDL");
		System.loadLibrary("SDL_image");
		System.loadLibrary("SDL_mixer");
		System.loadLibrary("SDL_ttf");
		System.loadLibrary("main");
	}

	// Setup
	protected void onCreate(Bundle savedInstanceState) {
		// Log.v("SDL", "onCreate()");
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
				Globals.ApplicationName);
				
		Bundle b = getIntent().getExtras();
		if(b!=null){
			game = b.getString("game");
		}
		
		display = getWindowManager().getDefaultDisplay();
		
		// So we can call stuff from static callbacks
		mSingleton = this;

		// Set up the surface
		mSurface = new SDLSurface(getApplication());
		setContentView(mSurface);
		SurfaceHolder holder = mSurface.getHolder();
		holder.setType(SurfaceHolder.SURFACE_TYPE_GPU);

	}

	// Events
	@Override
	protected void onPause() {
		// Log.v("SDL", "onPause()");
		nativeSave();
		wakeLock.release();
		super.onPause();
	}

	@Override
	protected void onResume() {
		// Log.v("SDL", "onResume()");
		super.onResume();
		wakeLock.acquire();

	}

	// Messages from the SDLMain thread
	static int COMMAND_CHANGE_TITLE = 1;

	// Handler for the messages
	Handler commandHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (msg.arg1 == COMMAND_CHANGE_TITLE) {
				setTitle((String) msg.obj);
			}
		}
	};
	
	
	
	

	// Send a message from the SDLMain thread
	void sendCommand(int command, Object data) {
		Message msg = commandHandler.obtainMessage();
		msg.arg1 = command;
		msg.obj = data;
		commandHandler.sendMessage(msg);
	}

	// C functions we call
	public static native void nativeInit(String jres, String jgame);

	public static native void nativeQuit();

	public static native void onNativeResize(int x, int y, int format);

	public static native void onNativeKeyDown(int keycode);

	public static native void onNativeKeyUp(int keycode);

	public static native void onNativeTouch(int action, float x, float y,
			float p);

	public static native void onNativeAccel(float x, float y, float z);

	public static native void nativeRunAudioThread();

	public static native void nativeSave();

	public static native void nativeStop();
	

	private PowerManager.WakeLock wakeLock = null;

	// Java functions called from C

	public static void createGLContext() {
		mSurface.initEGL();
	}

	public static void flipBuffers() {
		mSurface.flipEGL();
	}


	public static String getRes() {
		int x = display.getWidth();
		int y = display.getHeight();
		return x + "x" + y;
	}

	public static String getGame() {
		return game;
	}
	
	
	public static void setActivityTitle(String title) {
		// Called from SDLMain() thread and can't directly affect the view
		mSingleton.sendCommand(COMMAND_CHANGE_TITLE, title);
	}

	// Audio
	private static Object buf;

	public static Object audioInit(int sampleRate, boolean is16Bit,
			boolean isStereo, int desiredFrames) {
		int channelConfig = isStereo ? AudioFormat.CHANNEL_CONFIGURATION_STEREO
				: AudioFormat.CHANNEL_CONFIGURATION_MONO;
		int audioFormat = is16Bit ? AudioFormat.ENCODING_PCM_16BIT
				: AudioFormat.ENCODING_PCM_8BIT;
		int frameSize = (isStereo ? 2 : 1) * (is16Bit ? 2 : 1);

		Log.v("SDL", "SDL audio: wanted " + (isStereo ? "stereo" : "mono")
				+ " " + (is16Bit ? "16-bit" : "8-bit") + " "
				+ ((float) sampleRate / 1000f) + "kHz, " + desiredFrames
				+ " frames buffer");

		// Let the user pick a larger buffer if they really want -- but ye
		// gods they probably shouldn't, the minimums are horrifyingly high
		// latency already
		desiredFrames = Math.max(
				desiredFrames,
				(AudioTrack.getMinBufferSize(sampleRate, channelConfig,
						audioFormat) + frameSize - 1)
						/ frameSize);

		mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate,
				channelConfig, audioFormat, desiredFrames * frameSize,
				AudioTrack.MODE_STREAM);

		audioStartThread();

		Log.v("SDL",
				"SDL audio: got "
						+ ((mAudioTrack.getChannelCount() >= 2) ? "stereo"
								: "mono")
						+ " "
						+ ((mAudioTrack.getAudioFormat() == AudioFormat.ENCODING_PCM_16BIT) ? "16-bit"
								: "8-bit") + " "
						+ ((float) mAudioTrack.getSampleRate() / 1000f)
						+ "kHz, " + desiredFrames + " frames buffer");

		if (is16Bit) {
			buf = new short[desiredFrames * (isStereo ? 2 : 1)];
		} else {
			buf = new byte[desiredFrames * (isStereo ? 2 : 1)];
		}
		return buf;
	}

	public static void audioStartThread() {
		mAudioThread = new Thread(new Runnable() {
			public void run() {
				mAudioTrack.play();
				nativeRunAudioThread();
			}
		});

		// I'd take REALTIME if I could get it!
		mAudioThread.setPriority(Thread.MAX_PRIORITY);
		mAudioThread.start();
	}

	public static void audioWriteShortBuffer(short[] buffer) {
		for (int i = 0; i < buffer.length;) {
			int result = mAudioTrack.write(buffer, i, buffer.length - i);
			if (result > 0) {
				i += result;
			} else if (result == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// Nom nom
				}
			} else {
				Log.w("SDL", "SDL audio: error return from write(short)");
				return;
			}
		}
	}

	public static void audioWriteByteBuffer(byte[] buffer) {
		for (int i = 0; i < buffer.length;) {
			int result = mAudioTrack.write(buffer, i, buffer.length - i);
			if (result > 0) {
				i += result;
			} else if (result == 0) {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					// Nom nom
				}
			} else {
				Log.w("SDL", "SDL audio: error return from write(short)");
				return;
			}
		}
	}

	public static void audioQuit() {
		if (mAudioThread != null) {
			try {
				mAudioThread.join();
			} catch (Exception e) {
				Log.v("SDL", "Problem stopping audio thread: " + e);
			}
			mAudioThread = null;

			// Log.v("SDL", "Finished waiting for audio thread");
		}

		if (mAudioTrack != null) {
			mAudioTrack.stop();
			mAudioTrack = null;
		}
	}
	
	
}

/**
 * Simple nativeInit() runnable
 */
class SDLMain implements Runnable {
	public void run() {
		// Runs SDL_main()
		
		// Log.v("SDL", "SDL res "+SDLActivity.res);
		
		SDLActivity.nativeInit(SDLActivity.getRes(),SDLActivity.getGame());
		
		
	}
	
}

/**
 * SDLSurface. This is what we draw on, so we need to know when it's created in
 * order to do anything useful.
 * 
 * Because of this, that's where we set up the SDL thread
 */
class SDLSurface extends SurfaceView implements SurfaceHolder.Callback,
		View.OnKeyListener, View.OnTouchListener {

	// This is what SDL runs in. It invokes SDL_main(), eventually
	private Thread mSDLThread;

	// EGL private objects
	private EGLContext mEGLContext;
	private EGLSurface mEGLSurface;
	private EGLDisplay mEGLDisplay;

	// Startup
	public SDLSurface(Context context) {
		super(context);
		getHolder().addCallback(this);

		setFocusable(true);
		setFocusableInTouchMode(true);
		requestFocus();
		setOnKeyListener(this);
		setOnTouchListener(this);

	}

	// Called when we have a valid drawing surface
	public void surfaceCreated(SurfaceHolder holder) {
		// Log.v("SDL", "surfaceCreated()");
		
		// enableSensor(Sensor.TYPE_ACCELEROMETER, true);
	}

	// Called when we lose the surface
	public void surfaceDestroyed(SurfaceHolder holder) {
		// Log.v("SDL", "surfaceDestroyed()");

		// Send a quit message to the application
		SDLActivity.nativeQuit();

		// Now wait for the SDL thread to quit
		if (mSDLThread != null) {
			try {
				mSDLThread.join();
			} catch (Exception e) {
				Log.v("SDL", "Problem stopping thread: " + e);
			}
			mSDLThread = null;

			// Log.v("SDL", "Finished waiting for SDL thread");
		}

		// enableSensor(Sensor.TYPE_ACCELEROMETER, false);
	}

	// Called when the surface is resized
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// Log.v("SDL", "surfaceChanged()");

		int sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565 by default
		switch (format) {
		case PixelFormat.A_8:
			Log.v("SDL", "pixel format A_8");
			break;
		case PixelFormat.LA_88:
			Log.v("SDL", "pixel format LA_88");
			break;
		case PixelFormat.L_8:
			Log.v("SDL", "pixel format L_8");
			break;
		case PixelFormat.RGBA_4444:
			Log.v("SDL", "pixel format RGBA_4444");
			sdlFormat = 0x85421002; // SDL_PIXELFORMAT_RGBA4444
			break;
		case PixelFormat.RGBA_5551:
			Log.v("SDL", "pixel format RGBA_5551");
			sdlFormat = 0x85441002; // SDL_PIXELFORMAT_RGBA5551
			break;
		case PixelFormat.RGBA_8888:
			Log.v("SDL", "pixel format RGBA_8888");
			sdlFormat = 0x86462004; // SDL_PIXELFORMAT_RGBA8888
			break;
		case PixelFormat.RGBX_8888:
			Log.v("SDL", "pixel format RGBX_8888");
			sdlFormat = 0x86262004; // SDL_PIXELFORMAT_RGBX8888
			break;
		case PixelFormat.RGB_332:
			Log.v("SDL", "pixel format RGB_332");
			sdlFormat = 0x84110801; // SDL_PIXELFORMAT_RGB332
			break;
		case PixelFormat.RGB_565:
			Log.v("SDL", "pixel format RGB_565");
			sdlFormat = 0x85151002; // SDL_PIXELFORMAT_RGB565
			break;
		case PixelFormat.RGB_888:
			Log.v("SDL", "pixel format RGB_888");
			// Not sure this is right, maybe SDL_PIXELFORMAT_RGB24 instead?
			sdlFormat = 0x86161804; // SDL_PIXELFORMAT_RGB888
			break;
		default:
			Log.v("SDL", "pixel format unknown " + format);
			break;
		}
		SDLActivity.onNativeResize(width, height, sdlFormat);

		// Now start up the C app thread
		if (mSDLThread == null) {
			mSDLThread = new Thread(new SDLMain(), "SDLThread");
			mSDLThread.start();
		}
	}

	// unused
	public void onDraw(Canvas canvas) {
	}

	// EGL functions
	public boolean initEGL() {
		Log.v("SDL", "Starting up");

		try {

			EGL10 egl = (EGL10) EGLContext.getEGL();

			EGLDisplay dpy = egl.eglGetDisplay(EGL10.EGL_DEFAULT_DISPLAY);

			int[] version = new int[2];
			egl.eglInitialize(dpy, version);

			int[] configSpec = {
			// EGL10.EGL_DEPTH_SIZE, 16,
			EGL10.EGL_NONE };
			EGLConfig[] configs = new EGLConfig[1];
			int[] num_config = new int[1];
			egl.eglChooseConfig(dpy, configSpec, configs, 1, num_config);
			EGLConfig config = configs[0];

			EGLContext ctx = egl.eglCreateContext(dpy, config,
					EGL10.EGL_NO_CONTEXT, null);

			EGLSurface surface = egl.eglCreateWindowSurface(dpy, config, this,
					null);

			egl.eglMakeCurrent(dpy, surface, surface, ctx);

			mEGLContext = ctx;
			mEGLDisplay = dpy;
			mEGLSurface = surface;

		} catch (Exception e) {
			Log.v("SDL", e + "");
			for (StackTraceElement s : e.getStackTrace()) {
				Log.v("SDL", s.toString());
			}
		}

		return true;
	}

	// EGL buffer flip
	public void flipEGL() {
		try {
			EGL10 egl = (EGL10) EGLContext.getEGL();

			egl.eglWaitNative(EGL10.EGL_NATIVE_RENDERABLE, null);

			// drawing here

			egl.eglWaitGL();

			egl.eglSwapBuffers(mEGLDisplay, mEGLSurface);

		} catch (Exception e) {
			Log.v("SDL", "flipEGL(): " + e);
			for (StackTraceElement s : e.getStackTrace()) {
				Log.v("SDL", s.toString());
			}
		}
	}

	// Key events
	public boolean onKey(View v, int keyCode, KeyEvent event) {

		int key;
		switch (keyCode) {
		// case KeyEvent.KEYCODE_BACK: key = KeyEvent.KEYCODE_0; break;
		// case KeyEvent.KEYCODE_MENU: key = KeyEvent.KEYCODE_M; break;
		case KeyEvent.KEYCODE_VOLUME_UP:
			key = KeyEvent.KEYCODE_DPAD_UP;
			break;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			key = KeyEvent.KEYCODE_DPAD_DOWN;
			break;
		default:
			key = keyCode;
		}

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			// Log.v("SDL", "key down: " + keyCode);

			SDLActivity.onNativeKeyDown(key);
			return true;
		} else if (event.getAction() == KeyEvent.ACTION_UP) {
			// Log.v("SDL", "key up: " + keyCode);

			SDLActivity.onNativeKeyUp(key);
			return true;
		}

		return false;
	}

	// Touch events
	public boolean onTouch(View v, MotionEvent event) {

		int action = event.getAction();
		float x = event.getX();
		float y = event.getY();
		float p = event.getPressure();

		// TODO: Anything else we need to pass?
		SDLActivity.onNativeTouch(action, x, y, p);
		return true;
	}

}
