package com.silentlexx.instead;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class IdfLauncher extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String idf = getIntent().getData().getEncodedPath();

			Globals.idf = idf;
			Intent myIntent = new Intent(this, MainMenu.class);
			startActivity(myIntent);

		finish();
	}
}
