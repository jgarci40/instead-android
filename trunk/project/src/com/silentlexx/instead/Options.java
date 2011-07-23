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
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.Button;
import android.widget.CheckBox;

public class Options extends Activity {
	final static int VSMALL = 0;
	final static int SMALL = 8;
	final static int NORMAL = 12;
	final static int LAGE = 15;
	final static int VLAGE = 20;
	private int fsize;
	private boolean is_f = false;
	private Button cancel;
	private Button save;
	private Button sfont;
	private CheckBox music;
	private CheckBox click;
	private CheckBox ourtheme;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.options);
		
		

		sfont = (Button) findViewById(R.id.sfont);

		sfont.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openContextMenu(arg0);
			}
		});

		this.registerForContextMenu(sfont);
		
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
		ourtheme = (CheckBox) findViewById(R.id.ourtheme);

	}

	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);

		menu.add(0, v.getId(), 0, getString(R.string.vsmall));
		
				menu.add(0, v.getId(), 0, getString(R.string.fsmall));

				menu.add(0, v.getId(), 0, getString(R.string.fnormal));

		    	menu.add(0, v.getId(), 0, getString(R.string.flage));
		    	
		    	menu.add(0, v.getId(), 0, getString(R.string.fvlage));
		    	
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		if (item.getTitle() == getString(R.string.vsmall)) {
			fsize = VSMALL;
			is_f = true;
			
		} else if (item.getTitle() == getString(R.string.fsmall)) {
			fsize = SMALL;
			is_f = true;
			
		} else if (item.getTitle() == getString(R.string.fnormal)) {
			fsize = NORMAL;
			is_f = true;
						
		} else if (item.getTitle() == getString(R.string.flage)) {
			fsize = LAGE;
			is_f = true;
						
		} else if (item.getTitle() == getString(R.string.fvlage)) {
			fsize = VLAGE;
			is_f = true;
			
		} else {
			is_f = false;
			return false;
		}

		return true;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		readRC();
		// Log.d(Globals.TAG, "Option: Resume");
	}

	private void readRC() {
		String path = Globals.getOutFilePath(Globals.Options);
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
					if (line.toLowerCase().matches(
							"(.*)owntheme(\\ *)=(\\ *)1(.*)")) {
						ourtheme.setChecked(true);
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
		String path = Globals.getOutFilePath(Globals.Options);
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
					} else if (line.toLowerCase().matches(
							"(.*)owntheme(\\ *)=(\\ *)(.*)")) {
						rc = rc + "owntheme = " + getOpt(ourtheme.isChecked())	+ "\n";
					} else if (is_f && line.toLowerCase().matches(
					"(.*)fscale(\\ *)=(\\ *)(.*)")) {
			        	rc = rc + "fscale = " + Integer.toString(fsize) + "\n";
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
			// Log.e("Instead ERROR", "Error writing file " + path);
			return;
		}
		;

	}

}
