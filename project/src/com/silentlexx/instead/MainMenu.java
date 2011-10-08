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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.SimpleAdapter.ViewBinder;

public class MainMenu extends ListActivity implements ViewBinder {
	private ProgressDialog dialog;
	protected boolean dwn = false;
	protected boolean onpause = false;
	private ListView listView;
	private static final String LIST_TEXT = "list_text";
	private static final String BR = "<br>";

	private class ListItem {
		public String text;
		public int icon;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Globals.lastGame = new LastGame(this);
		
		dialog = new ProgressDialog(this);
		dialog.setTitle(getString(R.string.wait));
		dialog.setMessage(getString(R.string.init));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		View header = getLayoutInflater().inflate(R.layout.mnhead, null);
		// View button = getLayoutInflater().inflate(R.layout.gmbtn1, null);
		listView = getListView();
		listView.addHeaderView(header);
		// listView.addFooterView(footer);
		listView.setBackgroundColor(Color.BLACK);
		listView.setBackgroundDrawable(this.getResources().getDrawable(
				R.drawable.wallpaper));
		registerForContextMenu(listView);
		showMenu();
		if (!dwn)
			checkRC();

	}

	private void showMenu(){
		List<Map<String, ListItem>> listData = new ArrayList<Map<String, ListItem>>();

		listData.add(addListItem(getHtmlTagForName(getString(R.string.run))+ BR
				+ getHtmlTagForSmall(Globals.lastGame.getTitle()),
				R.drawable.start));
		listData.add(addListItem(getHtmlTagForName(getString(R.string.gmlist))+
				BR+
				getHtmlTagForSmall(getString(R.string.gmwhat)),
				R.drawable.gamelist));
		listData.add(addListItem(
				getHtmlTagForName(getString(R.string.options))+
				BR+
				getHtmlTagForSmall(getString(R.string.optwhat)),
				R.drawable.options));
		listData.add(addListItem(getHtmlTagForName(getString(R.string.app_name))
				+ BR
				+ getHtmlTagForSmall(getString(R.string.ver) + " "
						+ Globals.AppVer), R.drawable.info));
//		listData.add(addListItem(getHtmlTagForName(getString(R.string.mailme)),
//				R.drawable.email_go));
		listData.add(addListItem(getHtmlTagForName(getString(R.string.exit)),
				R.drawable.stop));

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listData,
				R.layout.list_item, new String[] { LIST_TEXT },
				new int[] { R.id.list_text });
		simpleAdapter.setViewBinder(this);
		setListAdapter(simpleAdapter);
	}
	
	private String getHtmlTagForName(String s) {
		return "<b>" + s + "</b>";
	}

	private String getHtmlTagForSmall(String s) {
		return "<small><i>" + s + "</i></small>";
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (position > 0) {

			switch (position) {
			case 1:
				startApp();
				break;
			case 2:
				startGM();
				break;
			case 3:
				startOpt();
				break;
			case 4:
				showAboutInstead();
				break;
			case 5:
				finish();
				break;
			}

		}

	}

	private Map<String, ListItem> addListItem(String s, int i) {
		Map<String, ListItem> iD = new HashMap<String, ListItem>();
		ListItem l = new ListItem();
		l.text = s;
		l.icon = i;
		iD.put(LIST_TEXT, l);
		return iD;
	}

	@Override
	public boolean setViewValue(View view, Object data,
			String stringRepresetation) {
		ListItem listItem = (ListItem) data;

		TextView menuItemView = (TextView) view;
		menuItemView.setText(Html.fromHtml(listItem.text));
		menuItemView.setCompoundDrawablesWithIntrinsicBounds(this
				.getResources().getDrawable(listItem.icon), null, null, null);
		return true;
	}

	private void sendEmail() {
		String url = "mailto:silentlexx@gmail.com";
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setData(Uri.parse(url));
		startActivity(i);
	}

	void showRun() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		dwn = false;
		checkRC();
	}

	private void startApp() {
		if (checkInstall()) {
			Intent myIntent = new Intent(this, SDLActivity.class);
			Bundle b = new Bundle();
			b.putString("game", Globals.lastGame.getName());
			myIntent.putExtras(b);
			startActivity(myIntent);
		} else {
			checkRC();
		}
	}

	private void startOpt() {
		if (checkInstall()) {
			Intent myIntent = new Intent(this, Options.class);
			startActivity(myIntent);
		} else {
			checkRC();
		}
	}

	private void startGM() {
		if (checkInstall()) {
			Intent myIntent = new Intent(this, GameMananger.class);
			startActivity(myIntent);
		} else {
			checkRC();
		}
	}

	@Override
	protected void onPause() {
		onpause = true;
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!dwn) {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			checkRC();
		} else {
			if (onpause && !dialog.isShowing()) {
				dialog.show();
			}
		}
		onpause = false;
	}

	public void setDownGood() {
		dwn = false;
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Log.d(Globals.TAG, "Main: Resume");
	}

	@Override
	protected void onDestroy() {
		// Log.d(Globals.TAG, "Main: Destroy");
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.mmenu1, menu);
		return true;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("onpause", onpause);
		savedInstanceState.putBoolean("dwn", dwn);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		dwn = savedInstanceState.getBoolean("dwn");
		onpause = savedInstanceState.getBoolean("onpause");
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.dwn_menu_btn:
			Globals.lastGame.removeLast();
			reLoadData();
			deleteRC();
			showMenu();
			break;
	case R.id.mailme:
		sendEmail();
		break;
	case R.id.about:
		showAboutInstead();
		break;
	}		
		return true;
	}

	public void onError(String s) {
		dialog.setCancelable(true);
		dwn = false;
		Log.e("Instead ERORR: ", s);
	}

	private void reLoadData() {
		ShowDialog();
		dwn = true;
		dialog.setMessage(getString(R.string.init));
		new DataDownloader(this, dialog);
	}

	private void loadData() {
		if (!checkInstall()) {
			dwn = true;
			ShowDialog();
			dialog.setMessage(getString(R.string.init));
			new DataDownloader(this, dialog);
		}
	}

	public boolean checkInstall() {
		String path = Globals.getOutFilePath(Globals.DataFlag);

		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(
					new FileInputStream(new File(path)), "UTF-8"));
		} catch (UnsupportedEncodingException e) {
			return false;
		} catch (FileNotFoundException e) {
			return false;
		} catch (SecurityException e) {
			return false;
		}
		;

		String line;

		try {

			line = input.readLine();
			try {
				if (line.toLowerCase().matches(
						"(.*)" + Globals.AppVer.toLowerCase() + "(.*)")) {
					input.close();
					return true;
				}
			} catch (NullPointerException e) {
				return false;
			} catch (IOException e) {
				return false;
			}
			;

		} catch (IOException e) {
			return false;
		}
		try {
			input.close();
		} catch (IOException e) {
			return false;
		}

		return false;
	}

	public void ShowDialog() {
		dialog.setCancelable(false);
		dialog.show();
	}

	private void deleteRC() {
		(new File(Globals.getOutFilePath(Globals.Options))).delete();
		checkRC();
	}

	private void checkRC() {
		if (checkInstall()) {
			if (!(new File(Globals.getOutFilePath(Globals.Options))).exists()) {
				CreateRC();
				;
			}
		} else {
			(new File(Globals.getOutFilePath(Globals.Options))).delete();
			loadData();
		}

	}

	private String getConf() {
		String locale = null;
		if (Locale.getDefault().toString().equals("ru_RU")
				|| Locale.getDefault().toString().equals("ru")) {
			locale = "lang = ru\n";
		} else if (Locale.getDefault().toString().equals("uk_UA")
				|| Locale.getDefault().toString().equals("uk")) {
			locale = "lang = ua\n";
		} else if (Locale.getDefault().toString().equals("it_IT")
				|| Locale.getDefault().toString().equals("it")
				|| Locale.getDefault().toString().equals("it_CH")) {
			locale = "lang = it\n";
		} else if (Locale.getDefault().toString().equals("es_ES")
				|| Locale.getDefault().toString().equals("es")) {
			locale = "lang = es\n";
		} else if (Locale.getDefault().toString().equals("be_BE")
				|| Locale.getDefault().toString().equals("be")) {
			locale = "lang = ru\n";
		} else {
			locale = "lang = en\n";
		}

		String res = getRes();
		String theme;
		if (res.toLowerCase().matches("(.*)320x240(.*)")) {
			theme = "theme = android-xVGA\n";
		} else

		if (res.toLowerCase().matches("(.*)480x320(.*)")) {
			theme = "theme = android-HVGA\n";
		} else

		if (res.toLowerCase().matches("(.*)640x480(.*)")) {
			theme = "theme = android-xVGA\n";
		} else

		if (res.toLowerCase().matches("(.*)800x600(.*)")) {
			theme = "theme = android-xVGA\n";
		} else
		{
			if (Build.VERSION.SDK_INT > 10) {
				theme = "theme = android-Honeycomb\n";
			} else {
				theme = "theme = android-WxVGA\n";
			}

		}
		String s = "game = "+Globals.TutorialGame+"\nkbd = 2\nautosave = 1\nowntheme = 0\nhl = 0\nclick = 1\nmusic = 1\nfscale = 12\n"
				+ locale + theme + "mode = " + res + "\n";
		return s;
	}

	private String getRes() {
		Display display = getWindowManager().getDefaultDisplay();
		int x = display.getWidth();
		int y = display.getHeight();
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			return y + "x" + x;
		} else {
			return x + "x" + y;
		}
	}

	private void CreateRC() {
		String path = Globals.getOutFilePath(Globals.Options);
		if (!(new File(path)).exists()) {
			OutputStream out = null;
			byte buf[] = getConf().getBytes();
			try {
				out = new FileOutputStream(path);
				out.write(buf);
				out.close();
			} catch (FileNotFoundException e) {
			} catch (SecurityException e) {
			} catch (java.io.IOException e) {
				Log.e("Instead ERROR", "Error writing file " + path);
				return;
			};
		}

	}

	private void showAboutInstead() {
		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					break;

				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.icon);
		builder.setTitle(getString(R.string.app_name) + " - " + Globals.AppVer);
		builder.setMessage(getString(R.string.about_instead))
				.setPositiveButton(getString(R.string.ok), dialogClickListener)
				.show();
	}

}