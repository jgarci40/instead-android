package com.silentlexx.instead;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class ZipInstaller extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String zip = getIntent().getData().getEncodedPath();
			Globals.zip = zip;
			Intent myIntent = new Intent(this, MainMenu.class);
			startActivity(myIntent);

		finish();
		
	}
}
