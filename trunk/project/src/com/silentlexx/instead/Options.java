package com.silentlexx.instead;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import android.app.Activity;
import android.os.Bundle;
//import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

public class Options extends Activity {
	private Button cancel;
	private Button save;
	private CheckBox music;
	private CheckBox click;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.options);

		save = (Button) findViewById(R.id.save);

		save.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				rewriteRC();
				finish();
			}
		});

		cancel = (Button) findViewById(R.id.cancel);

		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				finish();
			}
		});

		music = (CheckBox) findViewById(R.id.music);
		click = (CheckBox) findViewById(R.id.click);

	}

	@Override
	protected void onResume() {
		super.onResume();
		readRC();
		//Log.d(Globals.TAG, "Option: Resume");
	}

	private void readRC() {
		String path = getOutFilePath(Globals.Options);
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		} catch (FileNotFoundException e) {
		}

		try {
			String line = null;
			while ((line = input.readLine()) != null) {

				try {
					if (line.toLowerCase().matches(
							"(.*)music(\\ *)=(\\ *)1(.*)")) {
						music.setChecked(true);
					}
					if (line.toLowerCase().matches(
							"(.*)click(\\ *)=(\\ *)1(.*)")) {
						click.setChecked(true);
					}
				} catch (NullPointerException e) {
				}
			}

		} catch (IOException e) {
		} catch (NullPointerException e) {
		}

		try {
			input.close();
		} catch (IOException e) {
		}

	}

	private String getOpt(boolean b) {
		if (b) {
			return "1";
		} else {
			return "0";
		}
	}

	private void rewriteRC() {
		String path = getOutFilePath(Globals.Options);
		String rc = "";
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			String line = null;
			while ((line = input.readLine()) != null) {
				try {
					if (line.toLowerCase()
							.matches("(.*)music(\\ *)=(\\ *)(.*)")) {
						rc = rc + "music = " + getOpt(music.isChecked()) + "\n";
					} else if (line.toLowerCase().matches(
							"(.*)click(\\ *)=(\\ *)(.*)")) {
						rc = rc + "click = " + getOpt(click.isChecked()) + "\n";
					} else {
						rc = rc + line + "\n";
					}

				} catch (NullPointerException e) {
				}
				;
			}

		} catch (IOException e) {
		}
		try {
			input.close();
		} catch (IOException e) {
		}

		(new File(path)).delete();

		OutputStream out = null;
		byte buf[] = rc.getBytes();
		try {
			out = new FileOutputStream(path);
			out.write(buf);
			out.close();
		} catch (FileNotFoundException e) {
		} catch (SecurityException e) {
		} catch (java.io.IOException e) {
		//	Log.e("Instead ERROR", "Error writing file " + path);
			return;
		}
		;

	}

	public String getOutFilePath(final String filename) {
		return "/sdcard/" + Globals.ApplicationName + "/" + filename;
	};

}
