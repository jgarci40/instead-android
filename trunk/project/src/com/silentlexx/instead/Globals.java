package com.silentlexx.instead;

import java.io.File;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

class Globals {

	public static final String ApplicationName = "Instead";
	
	public static String AppVer(Context c) {
		PackageInfo pi;
		try {
			pi = c.getPackageManager().getPackageInfo(c.getPackageName(), 0);
		} catch (NameNotFoundException e) {
				e.printStackTrace();
				return "Not Found";
		}
		return pi.versionName;
	}

	//public static final String TAG = "LEXX_Activity";

	public static final int EGL_ver = 1;
	public static final String ZipName = "data.zip";
	public static final String GameListFileName = "game_list.xml";
	public static final String GameListAltFileName = "game_list_alt.xml";
	public static final String GameListDownloadUrl = "http://instead-launcher.googlecode.com/svn/pool/game_list.xml";
	public static final String GameListAltDownloadUrl = "http://instead-launcher.googlecode.com/svn/pool/game_list_alt.xml";
	public static final String GameDir = "appdata/games/";
	public static final String SaveDir = "appdata/saves/";
	public static final String Options = "appdata/insteadrc";
	public static final String GameFlags = ".gameflags";
	public static final String DataFlag = ".version";
	public static final String LastGameOpt = "lastgame.dat";
	public static final String ScreenOffFlag = ".screenoff";
	public static final String TutorialGame = "tutorial3";
	public static final String DirURQ = "urq";
	public static final String StringURQ = "\\[URQ\\]";
	public static final int BASIC = 1;
	public static final int ALTER = 2;
	public static final int AUTO = 0;
	public static final int PORTRAIT = 1;
	public static final int LANDSCAPE = 2;

	
	
	class Lang {
		public static final String RU = "ru";
		public static final String EN = "en";
		public static final String ALL = "";
	}

	public static String getStorage(){
		return android.os.Environment.getExternalStorageDirectory().toString()+"/";
	}

	public static String getGamePath(String f){
		return getOutFilePath(GameDir+f);
	}
	
	public static String getAutoSavePath(String f){
		return getOutFilePath(SaveDir+f+"/autosave");
	}
	
	public static String getOutFilePath(final String filename) {
		return getStorage() + Globals.ApplicationName + "/" + filename;
	};
	
	public static void delete(File file) {

		if (file.isDirectory()) {

			if (file.list().length == 0) {

				file.delete();

			} else {

				String files[] = file.list();

				for (String temp : files) {
					File fileDelete = new File(file, temp);
					delete(fileDelete);
				}
				if (file.list().length == 0) {
					
					try{
					file.delete();
					} catch(NullPointerException e){
						
					}
				}
			}

		} else {
			
			try{
			file.delete();
			} catch(NullPointerException e){
				
			}
		}
	}
}


