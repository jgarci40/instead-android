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
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
//import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
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
	private  LastGame lastGame;
	private Button sfont;
	private Button reset;
	private CheckBox music;
	private CheckBox click;
	private CheckBox ourtheme;
	private CheckBox scroff;
	private CheckBox portrait;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		lastGame = new LastGame(this);
		
		setContentView(R.layout.options);

		reset = (Button) findViewById(R.id.reset);

		reset.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				resetCfgDialog();
			}
		});

		sfont = (Button) findViewById(R.id.sfont);

		sfont.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				openContextMenu(arg0);
			}
		});

		this.registerForContextMenu(sfont);
		

		music = (CheckBox) findViewById(R.id.music);
		click = (CheckBox) findViewById(R.id.click);
		ourtheme = (CheckBox) findViewById(R.id.ourtheme);
		scroff = (CheckBox) findViewById(R.id.scroff);
		portrait = (CheckBox) findViewById(R.id.portrait);
		
		if(lastGame.getOreintation()==Globals.PORTRAIT){
			portrait.setChecked(true);
		}
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.opmenu1, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
	case R.id.saveopt:
		rewriteRC();
		finish();
		break;
	case R.id.cancelopt:
		finish();
		break;
	}		
		return true;
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
				
				menu.setHeaderTitle(getString(R.string.fontsize));
		
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
	
	
	private void resetCfgDialog(){
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
						deleteCfg();
					break;

				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.warning);
		builder.setTitle(getString(R.string.atention));
		builder.setMessage(getString(R.string.resetsd))
				.setPositiveButton(getString(R.string.yes), dialogClickListener)
				.setNegativeButton(getString(R.string.no), dialogClickListener)
				.show();

	}
	
	private void deleteCfg(){
		(new File(Globals.getOutFilePath(Globals.DataFlag))).delete();
		(new File(Globals.getOutFilePath(Globals.Options))).delete();
		finish();
	}
	

	private void readRC() {
		File sf = new File(getFilesDir() + Globals.ScreenOffFlag); 
		
		if(sf.exists()){
			scroff.setChecked(false);
		} else {
			scroff.setChecked(true);
		}
		
		
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
		
	    if(portrait.isChecked()){
	    	lastGame.setOreintetion(Globals.PORTRAIT);
	    } else {
	    	lastGame.setOreintetion(Globals.LANDSCAPE);	
	    }
		
		File sf = new File(getFilesDir() + Globals.ScreenOffFlag);
		
		if(scroff.isChecked()){
			sf.delete();			
		} else {
			try {
   	    		sf.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		
		}
		
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

	   @Override
	   public boolean onKeyDown(int keyCode, KeyEvent event)  {
	       if (keyCode == KeyEvent.KEYCODE_BACK) {
	    		rewriteRC();   
	       }
	       return super.onKeyDown(keyCode, event);
	   }
	
}
