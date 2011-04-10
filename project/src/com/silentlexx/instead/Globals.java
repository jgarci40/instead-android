package com.silentlexx.instead;

import java.io.File;

class Globals {

	public static final String ApplicationName = "Instead";

	public static final String AppVer = "1.3.4.3";

	//public static final String TAG = "LEXX_Activity";

	public static final int EGL_ver = 1;
	public static final String ZipName = "data.zip";
	public static final String GameListFileName = "game_list.xml";
	public static final String GameListAltFileName = "game_list_alt.xml";
	public static final String GameListDownloadUrl = "http://instead-launcher.googlecode.com/svn/pool/game_list.xml";
	public static final String GameListAltDownloadUrl = "http://instead-launcher.googlecode.com/svn/pool/game_list_alt.xml";
	public static final String GameDir = "appdata/games/";
	public static final String Options = "appdata/insteadrc";

	public static final String DataFlag = ".version";
	public static final int BASIC = 1;
	public static final int ALTER = 2;

	class Lang {
		public static final String RU = "ru";
		public static final String EN = "en";
		public static final String ALL = "";
	}

	public static String getOutFilePath(final String filename) {
		return "/sdcard/" + Globals.ApplicationName + "/" + filename;
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
					file.delete();

				}
			}

		} else {
			file.delete();
		}
	}
}


