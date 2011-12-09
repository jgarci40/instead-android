package com.silentlexx.instead;

import android.content.Context;


public class LastGame {

	private String title;
	private String name;
	private String title_def;
	private int filtr;
	private int list;
	private String lang;	    
    private MyPrefs pr;
	
	LastGame(Context p){
		pr = new MyPrefs(p, Globals.ApplicationName); 
		title_def =  p.getString(R.string.tutorial);
 		filtr = pr.get("filtr", GameList.ALL);
 		list = pr.get("list", Globals.BASIC);		
 		lang = pr.get("lang", Globals.Lang.ALL);
 		name = pr.get("name", Globals.TutorialGame);
 		title = pr.get("title", title_def);
 		
	}
	
	public void clearGame(){
 		name = Globals.TutorialGame;
 		title = title_def;		
 		Commit();
	}

	public void clearAll(){

		filtr = GameList.ALL;
 		list =  Globals.BASIC;		
 		lang = Globals.Lang.ALL;
 		name = Globals.TutorialGame;
 		title = title_def;		
 		Commit();
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
		Commit();
	}

	public void setTitle(String t){
		title = t;
		Commit();
	}

	public void setLang(String l){
		if(l.equals("null")){ 
			l = Globals.Lang.ALL;
		}
		lang = l;
		Commit();
	}
	
	
	public void setFiltr(int f){
		filtr = f;
		Commit();
	}

	public void setListNo(int l){	
		list = l;
		Commit();
	}


	private void Commit() {
 		pr.set("filtr", filtr);
 		pr.set("list", list);		
 		pr.set("lang", lang);
 		pr.set("name", name);
 		pr.set("title", title);		
		pr.commit();
	}
	
}
