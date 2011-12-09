package com.silentlexx.instead;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.util.Log;

//import android.util.Log;

public class GameList {
	public static final int INSTALLED = 0;
	public static final int ALL = 1;
	public static final int UPDATE = 2;
	public static final int NEW = 3;

	public static final int FLAG = 0;
	public static final int NAME = 1;
	public static final int URL = 2;
	public static final int VERSION = 3;
	public static final int TITLE = 4;
	public static final int DESCURL = 5;
	public static final int LANG = 6;
	public static final int SIZE = 7;

	protected static final String GAME_LIST = "game_list";
	protected static final String GAME = "game";
	protected static final String ITEM_NAME = "name";
	protected static final String ITEM_URL = "url";
	protected static final String ITEM_VERSION = "version";
	protected static final String ITEM_TITLE = "title";
	protected static final String ITEM_DESCURL = "descurl";
	protected static final String ITEM_LANG = "lang";
	protected static final String ITEM_SIZE = "size";
	private String na;
	private boolean ok = false;

//	protected static final int MAX = 99;

	private String xml;

	private int length = 0;

	/*
	private String[] name = new String[MAX];
	private String[] url = new String[MAX];
	private String[] version = new String[MAX];
	private String[] title = new String[MAX];
	private String[] descurl = new String[MAX];
	private String[] lang = new String[MAX];
	private String[] size = new String[MAX];
	private int[] flag = new int[MAX];
	*/
	private List<Integer> flag;
	private List<Integer> bytesize;
	private List<String> name;
	private List<String> url;	
	private List<String> version;
	private List<String> title;
	private List<String> descurl;
	private List<String> lang;
	private List<String> size;
	
	private Document document;
	private GameMananger Parent;
	private String fflags;

	
	GameList(GameMananger _parent, String f, boolean fscan) {
		Parent = _parent;
		na=Parent.getString(R.string.na);
		flag = new ArrayList<Integer>();
		bytesize = new ArrayList<Integer>();
		name = new ArrayList<String>();
		url = new ArrayList<String>();	
		version = new ArrayList<String>();
		title = new ArrayList<String>();
		descurl = new ArrayList<String>();
		lang = new ArrayList<String>();
		size = new ArrayList<String>();
				
	
		
		fflags = Parent.getFilesDir()+"/"+f.substring(0, f.length()-3)+"db";

		xml = Parent.getFilesDir()+"/"+f;		
		
		ok = readXML();
		if (isOK()) {
			parseXML();
			if (!(new File(fflags)).exists() || fscan){
				   getFlags();
				   
			}
			setFlags();
		}		
	}


	public boolean isOK() {
		return ok;
	}

	private void setFlags(){
		
		//int i = 0;
		
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(fflags)), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
		} catch (FileNotFoundException e) {
		}

		try {
			String line = null;
			while ((line = input.readLine()) != null) {
			  
				flag.add(Integer.parseInt(line.trim()));

				
			}
			} catch (NullPointerException e) {
			} catch (IOException e) {
			}


	try {
		input.close();
	} catch (IOException e) {
	}
		
	}
	
	public void flagsRescan(){
		getFlags();
	}
	
	private void getFlags() {
		//Log.d("LEXX", "RESCAN");
		
		(new File(fflags)).delete();
		
		String path;
		int ff;
		
		String buf = "";
		
		for (int i = 0; i < getLength(); i++) {
			path = Globals.getOutFilePath(Globals.GameDir + name.get(i)
					+ "/main.lua");

			if (checkFile(path)) {

				if (getVerFromFile(path, i)) {
					ff = INSTALLED;
				} else {
					ff = UPDATE;
				}

			} else {
				ff = NEW;
			}
			
			buf = buf + Integer.toString(ff)+"\n";
		}

		OutputStream out = null;
		byte buff[] = buf.getBytes();
		try {
			out = new FileOutputStream(fflags);
			out.write(buff);
			out.close();
		} catch (FileNotFoundException e) {
		} catch (SecurityException e) {
		} catch (java.io.IOException e) {
			Log.e("Instead ERROR", "Error writing file " + fflags);
			return;
		}
		;
		
		
	}

	private boolean getVerFromFile(String path, int n) {
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return true;
		} catch (FileNotFoundException e) {
			return true;
		}

		try {
			String line = null;
			while ((line = input.readLine()) != null) {
				// //Log.d(Globals.TAG,"Lines: "+line);
				try {

					if (line.toLowerCase()
							.matches("(.*)\\$version:(.*)\\$(.*)")) {

						if (line.toLowerCase().matches(
								"(.*)\\$version:(\\ *)"
										+ version.get(n).toLowerCase() + "\\$(.*)")) {
							input.close();
							return true;
						} else {
							input.close();
							return false;
						}

					}

				} catch (NullPointerException e) {

					// FIXME Вылит с тех игр, в которых явно не указана
					// версия...
					input.close();
					return true;
				}
			}

		} catch (IOException e) {
			return true;
		}
		try {
			input.close();
		} catch (IOException e) {
			return true;
		}
		return true;
	}

	private boolean checkFile(String path) {
		InputStream file = null;
		try {
			file = new FileInputStream(path);
		} catch (FileNotFoundException e) {
		} catch (SecurityException e) {
		}
		;
		if (file != null) {
			return true;
		}
		return false;
	}

	private boolean readXML() {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			return false;
		}
		InputStream stream;
		try {
			stream = new FileInputStream(xml);
		} catch (FileNotFoundException e) {
			//Log.d(Globals.TAG, "File not found: " + xml);
			return false;
		}
		// //Log.d(Globals.TAG,stream.read());
		try {
			document = builder.parse(stream);
		} catch (SAXException e) {
			//Log.d(Globals.TAG, "SAXEx: document = builder.parse(stream)");
			return false;
		} catch (IOException e) {
			//Log.d(Globals.TAG, "IOEx: document = builder.parse(stream)");
			return false;
		}
		try {
			stream.close();
		} catch (IOException e) {
			//Log.d(Globals.TAG, "IOEx: stream.close();");
			return false;
		}
		return true;
	}

	private void parseXML() {

		NodeList nodes = document.getElementsByTagName(GAME);
		length = nodes.getLength();
		// //Log.d(Globals.TAG,"Nodes: "+length);
		for (int i = 0; i < length; i++) {
	
			name.add(na);
			url.add(na);	
			version.add(na);
			title.add(na);
			descurl.add(na);
			lang.add(na);
			size.add(na);			
			processItemNode(nodes.item(i),i);
		}

	}

	private void processItemNode(Node itemNode,int n) {
		for (int i = 0; i < itemNode.getChildNodes().getLength(); i++) {
			Node subnode = ((NodeList) itemNode.getChildNodes()).item(i);

			if (subnode.getNodeName().toLowerCase().equals(ITEM_NAME)) {
				name.add(n,subnode.getFirstChild().getNodeValue());
			}

			if (subnode.getNodeName().toLowerCase().equals(ITEM_TITLE)) {
				title.add(n,subnode.getFirstChild().getNodeValue());
			}

			if (subnode.getNodeName().toLowerCase().equals(ITEM_URL)) {
				url.add(n,subnode.getFirstChild().getNodeValue());
			}

			if (subnode.getNodeName().toLowerCase().equals(ITEM_VERSION)) {
				version.add(n,subnode.getFirstChild().getNodeValue());
			}

			if (subnode.getNodeName().toLowerCase().equals(ITEM_DESCURL)) {
				descurl.add(n,subnode.getFirstChild().getNodeValue());
			}

			if (subnode.getNodeName().toLowerCase().equals(ITEM_LANG)) {
				lang.add(n,subnode.getFirstChild().getNodeValue());
			}

			if (subnode.getNodeName().toLowerCase().equals(ITEM_SIZE)) {
				bytesize.add(Integer.parseInt(subnode.getFirstChild()
						.getNodeValue()));
				float b = Float.parseFloat(subnode.getFirstChild()
						.getNodeValue());
				BigDecimal bigDecimal = new BigDecimal(
						Float.toString((b / 1024) / 1024));
				bigDecimal = bigDecimal.setScale(2, BigDecimal.ROUND_UP);

				size.add(n,bigDecimal.toString() + " "
						+ Parent.getString(R.string.mb));

			}

		}
	}


	public String getInf(int type, int n) {
		if (type >= 0 || type <= length) {
			switch (type) {
			case NAME:
				return name.get(n);
			case TITLE:
				return title.get(n);
			case URL:
				return url.get(n);
			case DESCURL:
				return descurl.get(n);
			case LANG:
				return lang.get(n);
			case VERSION:
				return version.get(n);
			case SIZE:
				return size.get(n);
			default:
				return null;
			}
		}
		return null;
	}

	public int getFlag(int i) {
		if (i >= 0 || i <= length) {
			return flag.get(i);
		}
		return -1;
	}

	public int getLength() {
		return length;
	}

	public int getByteSize(int i){
		return bytesize.get(i);
	}

	public int getIndexOfURQ(){
		for(int i=0; i < getLength(); i++){
			if(name.get(i).equals(Globals.DirURQ)) return i;
		}
		return -1;
	}

}
