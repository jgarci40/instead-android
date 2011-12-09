package com.silentlexx.instead;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class IdfLauncher extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		String idf = getIntent().getData().getEncodedPath();
		if(idf!=null){
			Globals.idf = idf;
			Intent myIntent = new Intent(this, MainMenu.class);
			Bundle b = new Bundle();
			//b.putString("idf", idf);
			myIntent.putExtras(b);
			startActivity(myIntent);
		}
		finish();
	}
}
