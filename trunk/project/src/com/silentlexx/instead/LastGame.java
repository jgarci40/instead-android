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
	private String cfg;
	private String title;
	private String name;
	private String title_def;
	
	LastGame(Context p){
		 cfg = p.getFilesDir()+"/"+Globals.LastGameOpt;
		 title_def = p.getString(R.string.tutorial);
			File f = new File(cfg);
			if(!f.exists()){
				createNew();
			}
			readFile();
	}
	
	public void removeLast(){
		(new File(cfg)).delete();
		createNew();
		readFile();				
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
			String str = title+"\n"+name;
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
	
	public String getName(){
		return name;
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
	

}
