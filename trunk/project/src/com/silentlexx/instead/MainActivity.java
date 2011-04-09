package com.silentlexx.instead;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
	private Button run;
	private Button exit;
	private Button gmlist;
	private Button options;
	// private PowerManager.WakeLock wakeLock = null;
	private ProgressDialog dialog;
	private TextView email;
	protected boolean dwn = false;
	protected boolean onpause = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// PowerManager pm = (PowerManager)
		// getSystemService(Context.POWER_SERVICE);
		// wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
		// Globals.ApplicationName);

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setContentView(R.layout.main);
		} else {
			setContentView(R.layout.main_land);
		}

		// //Log.d(Globals.TAG,"Orent:" + this.get);

		dialog = new ProgressDialog(this);
		dialog.setTitle(getString(R.string.wait));
		dialog.setMessage(getString(R.string.init));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		email = (TextView) findViewById(R.id.email);
		email.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sendEmail();
			}
		});

		run = (Button) findViewById(R.id.btn_run);
		exit = (Button) findViewById(R.id.btn_exit);
		gmlist = (Button) findViewById(R.id.btn_gmlist);
		options = (Button) findViewById(R.id.btn_options);

		gmlist.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				// .setBackgroundColor(Color.rgb(33, 77, 187));
				startGM();
			}
		});

		options.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				startOpt();
			}
		});

		exit.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		run.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startApp();
			}
		});

		if (!dwn)
			checkRC();
	}

	private void sendEmail() {
		String url = "mailto:" + getString(R.string.email);
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	void showRun() {
		// run.setVisibility(View.VISIBLE);
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		dwn = false;
		checkRC();
		// wakeLock.release();
	}

	private void startApp() {
		/*
		 * Globals.isRun = 0; Bundle b = new Bundle(); b.putInt("isRun",
		 * Globals.isRun); this.getIntent().putExtras(b);
		 */
		if (checkInstall()) {
			Intent myIntent = new Intent(this, SDLActivity.class);
			startActivity(myIntent);
		} else {
			checkRC();
		}
	}

	private void startOpt() {
		/*
		 * Globals.isRun = 0; Bundle b = new Bundle(); b.putInt("isRun",
		 * Globals.isRun); this.getIntent().putExtras(b);
		 */
		if (checkInstall()) {
			Intent myIntent = new Intent(this, Options.class);
			startActivity(myIntent);
		} else {
			checkRC();
		}
	}

	private void startGM() {
		if (checkInstall()) {
			Intent myIntent = new Intent(this, GameMananger.class);
			startActivity(myIntent);
		} else {
			checkRC();
		}
	}

	/*
	 * @Override protected Dialog onCreateDialog(int id) {
	 * super.onCreateDialog(id); return dialog; }
	 */

	@Override
	protected void onPause() {
		// Log.d(Globals.TAG, "Main: Pause");
		onpause = true;
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		// Log.d(Globals.TAG, "Main: Pause");
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!dwn) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			checkRC();
		} else {
			if (onpause && !dialog.isShowing()) {
				dialog.show();
			}
		}
		onpause = false;
		// Log.d(Globals.TAG, "Main: Resume");

	}

	public void setDownGood() {
		dwn = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Log.d(Globals.TAG, "Main: Resume");
	}

	@Override
	protected void onDestroy() {
		// Log.d(Globals.TAG, "Main: Destroy");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mmenu1, menu);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("onpause", onpause);
		savedInstanceState.putBoolean("dwn", dwn);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		dwn = savedInstanceState.getBoolean("dwn");
		onpause = savedInstanceState.getBoolean("onpause");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.dwn_menu_btn:
			reLoadData();
			deleteRC();
			break;
	/*		
		case R.id.del_menu_btn:
			deleteRC();
			break;
	*/
		case R.id.about_menu_btn:
			showAboutInstead();
			break;
		}
		return true;
	}

	public void wakeLockA() {
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		// wakeLock.acquire();
	}

	public void wakeLockR() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
		// wakeLock.release();
	}

	public void onError(String s) {
		wakeLockR();
		dialog.setCancelable(true);
		// if(dialog.isShowing()){dia//Log.dismiss();}
		dwn = false;
		// Log.d(Globals.TAG, s);
		Log.e("Instead ERORR: ", s);
		// Toast.makeText(this, s, Toast.LENGTH_LONG).show();
	}

	private void reLoadData() {
		ShowDialog();
		dwn = true;
		dialog.setMessage(getString(R.string.init));
		new DataDownloader(this, dialog);
	}

	private void loadData() {
		if (!checkInstall()) {
			dwn = true;
			ShowDialog();
			dialog.setMessage(getString(R.string.init));
			new DataDownloader(this, dialog);
		}
	}

	public boolean checkInstall() {
		String path = Globals.getOutFilePath(Globals.DataFlag);

		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return false;
		} catch (FileNotFoundException e) {
			return false;
		} catch (SecurityException e) {
			return false;
		}
		;

		String line;

		try {

			line = input.readLine();
			try {
				if (line.toLowerCase().matches(
						"(.*)" + Globals.AppVer.toLowerCase() + "(.*)")) {
					input.close();
					return true;
				}
			} catch (NullPointerException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			;

		} catch (IOException e) {
			return false;
		}
		try {
			input.close();
		} catch (IOException e) {
			return false;
		}

		return false;
	}

	public void ShowDialog() {
		dialog.setCancelable(false);
		dialog.show();
	}

	private void deleteRC() {

		(new File(Globals.getOutFilePath(Globals.Options))).delete();
		checkRC();
	//	Toast.makeText(this, getString(R.string.delrc), Toast.LENGTH_LONG).show();
	}



	private void checkRC() {
		if (checkInstall()) {
			String path = Globals.getOutFilePath(Globals.Options);
			InputStream checkFile = null;
			try {
				checkFile = new FileInputStream(path);
			} catch (FileNotFoundException e) {
			} catch (SecurityException e) {
			}
			;

			if (checkFile == null) {
				CreateRC();
			}

		} else {
			(new File(Globals.getOutFilePath(Globals.Options))).delete();
			loadData();
		}

	}

	private String getConf() {
		String locale = null;
		if (Locale.getDefault().toString().equals("ru_RU")
				|| Locale.getDefault().toString().equals("ru")
				|| Locale.getDefault().toString().equals("ua_UA")
				|| Locale.getDefault().toString().equals("ua")) {
			locale = "lang = ru\ngame = tutorial2\n";
		} else {
			locale = "lang = en\ngame = tutorial2-en\n";
		}

		String res = getRes();
		
		/*
		if (res.equals("-1x-1")){
			res = "320x240";
			Toast.makeText(this, getString(R.string.conferror), Toast.LENGTH_LONG)
			.show();			
		}
		*/
	
		String theme;
		// Log.d(Globals.TAG, "Screen " + res);

		if (res.toLowerCase().matches("(.*)320x240(.*)")) {
			theme = "theme = android-xVGA\n";
		} else

		if (res.toLowerCase().matches("(.*)480x320(.*)")) {
			theme = "theme = android-HVGA\n";
		} else

		if (res.toLowerCase().matches("(.*)640x480(.*)")) {
			theme = "theme = android-xVGA\n";
		} else

		if (res.toLowerCase().matches("(.*)800x600(.*)")) {
			theme = "theme = android-xVGA\n";
		} else

		{
			theme = "theme = android-WxVGA\n";
		}

		// Log.d(Globals.TAG, "Res choosen: " + s);

		String s = "kbd = 2\nautosave = 1\nowntheme = 0\nhl = 1\nclick = 1\nmusic = 1\n"
				+ locale
				+ theme
				+ "mode = " + res + "\n";

		return s;
	}

	private String getRes() {
		Display display = getWindowManager().getDefaultDisplay();
		int x = display.getWidth();
		int y = display.getHeight();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return y + "x" + x;
		} else {
			return x + "x" + y;
		}
	}

	private void CreateRC() {

		String path = Globals.getOutFilePath(Globals.Options);
		InputStream checkFile = null;
		OutputStream out = null;
		try {
			checkFile = new FileInputStream(path);
		} catch (FileNotFoundException e) {
		} catch (SecurityException e) {
		}
		;

		if (checkFile == null) {
			byte buf[] = getConf().getBytes();
			try {
				out = new FileOutputStream(path);
				out.write(buf);
				out.close();
			} catch (FileNotFoundException e) {
			} catch (SecurityException e) {
			} catch (java.io.IOException e) {
				Log.e("Instead ERROR", "Error writing file " + path);
				return;
			}
			;
		}

	}

	private void showAboutInstead() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					break;

				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon);
		builder.setTitle(getString(R.string.app_name) + " - " + Globals.AppVer);
		builder.setMessage(getString(R.string.about_instead))
				.setPositiveButton(getString(R.string.ok), dialogClickListener)
				.show();
	}

}
