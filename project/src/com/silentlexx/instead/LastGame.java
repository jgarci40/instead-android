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

import android.content.Context;


public class LastGame {
	private final String BR = "\n";
	private String cfg;
	private String title;
	private String name;
	private String title_def;
	private int filtr;
	private int list;
	private int or;
	private String lang;
	
	LastGame(Context p){
		 cfg = p.getFilesDir()+"/"+Globals.LastGameOpt;
		 title_def = p.getString(R.string.tutorial);
		 initClass();
	}
	
	private void initClass(){
		 filtr = GameList.ALL;
		 list = Globals.BASIC;
		 lang = Globals.Lang.ALL;
		 or = Globals.LANDSCAPE; 
			File f = new File(cfg);
			if(!f.exists()){
				createNew();
			}
			readFile();
	}
	
	public void removeLast(){
		(new File(cfg)).delete();
		initClass();				
	}
	
	private void createNew(){
		title = title_def;
		name = Globals.TutorialGame;
		writeFile();
	}
	
	private void writeFile(){
		OutputStream out = null;
		try {
			out = new FileOutputStream(cfg);
			String str = 
					title+BR+
					name+BR+
					lang+BR+
					Integer.toString(filtr)+BR+
					Integer.toString(list)+BR+
					Integer.toString(or)+BR;
			
			out.write(str.getBytes());
			out.close();
		} catch (FileNotFoundException e) {
		} catch (SecurityException e) {
		} catch (java.io.IOException e) {
		};
		
	}
	
	
	private void readFile(){
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(cfg)), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		} catch (FileNotFoundException e) {
		}

		try {
			
			  title	= input.readLine();
			  name	= input.readLine();
			  lang = input.readLine();
			  filtr = Integer.parseInt(input.readLine());
			  list = Integer.parseInt(input.readLine());
			  or = Integer.parseInt(input.readLine());
			  
				} catch (NullPointerException e) {
					
				} catch (IOException e) {
				} catch (NumberFormatException e){
					(new File(cfg)).delete();
					createNew();
					return;
				}
				try {
					input.close();
				} catch (IOException e) {
				}
			
	}

	public String getTitle(){
		return title;
	}

	public String getLang(){
		if(lang.equals("null")){ 
			lang = Globals.Lang.ALL;
		}
		return lang;
	}
	
	public String getName(){
		return name;
	}

	public int getFiltr(){
		return filtr;
	}
	
	public int getListNo(){
		return list;
	}
	
	
	public void setLast(String t, String n){
		title = t;
		name = n;
		writeFile();
	}

	public void setTitle(String t){
		title = t;
		writeFile();
	}

	public void setLang(String l){
		if(l.equals("null")){ 
			l = Globals.Lang.ALL;
		}
		lang = l;
		writeFile();
	}
	
	
	public void setFiltr(int f){
		filtr = f;
		writeFile();
	}

	public void setListNo(int l){	
		list = l;
		writeFile();
	}
	
	public int getOreintation(){
	  return or;	
	}
	
	public void setOreintetion(int o){
	    or = o;
	    writeFile();
	}
	
	
}
