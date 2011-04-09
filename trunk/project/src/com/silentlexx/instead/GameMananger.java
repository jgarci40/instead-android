package com.silentlexx.instead;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
//import android.os.PowerManager;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class GameMananger extends ListActivity implements ViewBinder {

	public final int BASIC = 1;
	public final int ALTER = 2;
	private int item_index = -1;
	private String g;
	// private PowerManager.WakeLock wakeLock = null;
	private int filter = GameList.ALL;
	private GameList gl;
	private int[] index = new int[GameList.MAX];
	private ProgressDialog dialog;
	private static final String LIST_TEXT = "list_text";
	private int listNo = Globals.BASIC;
	private boolean lwhack = false;
	private String lang_filter = Globals.Lang.ALL;
	protected boolean onpause = false;
	private boolean dwn = false;
	private boolean isdwn = false;
	private GameDownloader downloader = null;
	private Button basic_btn;
	private Button alter_btn;
	private Button btn_sync;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// PowerManager pm = (PowerManager)
		// getSystemService(Context.POWER_SERVICE);
		// wakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
		// Globals.ApplicationName);

		dialog = new ProgressDialog(this);
		dialog.setTitle(getString(R.string.wait));
		dialog.setMessage(getString(R.string.init));
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);

		View header = getLayoutInflater().inflate(R.layout.gmhead, null);
		// View button = getLayoutInflater().inflate(R.layout.gmbtn1, null);
		ListView listView = getListView();
		listView.addHeaderView(header);
		// listView.addFooterView(footer);

		registerForContextMenu(listView);

		basic_btn = (Button) findViewById(R.id.basic_btn);

		basic_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listNo = Globals.BASIC;
				setTabs();
				checkXml();
			}
		});

		
		btn_sync = (Button) findViewById(R.id.btn_sync);

		btn_sync.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {

				listDownload();
			}
		});
		
		
		alter_btn = (Button) findViewById(R.id.alter_btn);

		alter_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				listNo = Globals.ALTER;
				setTabs();
				checkXml();
			}
		});

	}

	private void setTabsG() {
		switch (listNo) {
		case Globals.BASIC:
			basic_btn.setTextColor(Color.rgb(0, 0, 0));
			alter_btn.setTextColor(Color.rgb(200, 200, 200));

			basic_btn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.tab_a));
			alter_btn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.tab_g));

			break;
		case Globals.ALTER:
			basic_btn.setTextColor(Color.rgb(200, 200, 200));
			alter_btn.setTextColor(Color.rgb(0, 0, 0));

			basic_btn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.tab_g));
			alter_btn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.tab_a));
			break;
		}

	}

	private void setTabs() {

		switch (listNo) {
		case Globals.BASIC:
			basic_btn.setTextColor(Color.rgb(0, 0, 0));
			alter_btn.setTextColor(Color.rgb(200, 200, 200));

			basic_btn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.tab_c));
			alter_btn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.tab));

			break;
		case Globals.ALTER:
			basic_btn.setTextColor(Color.rgb(200, 200, 200));
			alter_btn.setTextColor(Color.rgb(0, 0, 0));

			basic_btn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.tab));
			alter_btn.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.tab_c));
			break;
		}

	}

	public void ShowDialog() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setTitle(getString(R.string.wait));
		dialog.setMessage(getString(R.string.init));
		dialog.setCancelable(false);
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	public void ShowDialogCancel() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		dialog = new ProgressDialog(this);
		dialog.setIndeterminate(true);
		dialog.setTitle(getString(R.string.waitdwn) + " \"" + g + "\"...");
		dialog.setMessage(getString(R.string.init));
		dialog.setCancelable(false);
		dialog.setButton(getString(R.string.cancel),
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						dialog.dismiss();
					}
				});
		if (!dialog.isShowing()) {
			dialog.show();
		}
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if (position > 0) {
			item_index = position - 1;
			// Log.d(Globals.TAG, "Position item: " + item_index);
			g = gl.getInf(GameList.TITLE, index[item_index]);
			lwhack = true;
			openContextMenu(v);
		}
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		if (lwhack) {
			menu.setHeaderTitle(g);

			if (gl.getFlag(index[item_index]) == GameList.INSTALLED) {
				menu.add(0, v.getId(), 0, getString(R.string.menustart));

				menu.add(0, v.getId(), 0, getString(R.string.menudel));
			}

			if (gl.getFlag(index[item_index]) == GameList.NEW) {
				menu.add(0, v.getId(), 0, getString(R.string.menudown));
			}

			if (gl.getFlag(index[item_index]) == GameList.UPDATE) {
				menu.add(0, v.getId(), 0, getString(R.string.menustart));
				menu.add(0, v.getId(), 0, getString(R.string.menuupd));
				menu.add(0, v.getId(), 0, getString(R.string.menudel));
			}

			menu.add(0, v.getId(), 0, getString(R.string.agame));

		}
		lwhack = false;
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getTitle() == getString(R.string.menustart)) {
			startApp();
		} else if (item.getTitle() == getString(R.string.menudel)) {
			gameDelete();
		} else if (item.getTitle() == getString(R.string.menuupd)) {
			gameUpdate();
		} else if (item.getTitle() == getString(R.string.menudown)) {
			gameDownload();
		} else if (item.getTitle() == getString(R.string.agame)) {
			startAbout();
		} else {
			return false;
		}
		/*
		 * AdapterContextMenuInfo info = (AdapterContextMenuInfo)
		 * item.getMenuInfo(); switch (item.getItemId()) { case R.id.edit:
		 * editNote(info.id); return true; case R.id.delete:
		 * deleteNote(info.id); return true; default: return
		 * super.onContextItemSelected(item);
		 */
		return true;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// FIXME
		MenuInflater inflater = getMenuInflater();
		// FIXME !!!!
		// if(lang_filter){
		inflater.inflate(R.menu.gmmenu1, menu);
		// } else {
		// inflater.inflate(R.menu.gmmenu2, menu);
		// }
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		item_index = -1;
		switch (item.getItemId()) {
		case R.id.upd_menu_btn:
			listDownload();
			return true;
		case R.id.inst_menu_btn:
			filter = GameList.INSTALLED;
			listUpdate();
			return true;
		case R.id.all_menu_btn:
			filter = GameList.ALL;
			listUpdate();
			return true;
		case R.id.isupd_menu_btn:
			filter = GameList.UPDATE;
			listUpdate();
			return true;
		case R.id.isnew_menu_btn:
			filter = GameList.NEW;
			listUpdate();
			return true;

		case R.id.langru_menu_btn:
			lang_filter = Globals.Lang.RU;
			listUpdate();
			return true;
		case R.id.langen_menu_btn:
			lang_filter = Globals.Lang.EN;
			listUpdate();
			return true;
		case R.id.langall_menu_btn:
			lang_filter = Globals.Lang.ALL;
			listUpdate();
			return true;
			/*
			 * case R.id.basic_menu_btn: listNo = Globals.BASIC; checkXml();
			 * return true; case R.id.alter_menu_btn: listNo = Globals.ALTER;
			 * checkXml(); return true;
			 */
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void wakeLockA() {

		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		} else {
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		}
		// wakeLock.acquire();
	}

	public void wakeLockR() {

		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

		// wakeLock.release();

	}

	private int getFlagId(int i) {

		switch (gl.getFlag(i)) {
		case GameList.NEW:
			return R.drawable.newinstall;// jog_tab_target_gray

		case GameList.INSTALLED:
			return R.drawable.installed; // jog_tab_target_green

		case GameList.UPDATE:
			return R.drawable.update; // jog_tab_target_yellow

		default:
			return R.drawable.newinstall;
		}
	}

	public void onError(String s) {
		wakeLockR();
		dialog.setCancelable(true);
		dwn = false;
		downloader = null;
		// Log.d(Globals.TAG, s);
		Log.e("Instead ERORR: ", s);
		// listUpdate();
		// Toast.makeText(this, s, Toast.LENGTH_LONG).show();

	}

	private Map<String, ListItem> addListItem(String s, int i) {
		Map<String, ListItem> iD = new HashMap<String, ListItem>();
		ListItem l = new ListItem();
		l.text = s;
		l.icon = i;
		iD.put(LIST_TEXT, l);
		return iD;
	}

	private String getGameListName(int n) {
		switch (n) {
		case Globals.BASIC:
			return Globals.GameListFileName;
		case Globals.ALTER:
			return Globals.GameListAltFileName;
		default:
			return Globals.GameListFileName;
		}
	}

	public void listUpdate() {

		gl = new GameList(this, getGameListName(listNo));

		List<String> names = new ArrayList<String>();
		List<Map<String, ListItem>> listData = new ArrayList<Map<String, ListItem>>();

		int j = 0;

		for (int i = 0; i < gl.getLength(); i++) {

			if (lang_filter.equals(gl.getInf(GameList.LANG, i))
					|| lang_filter.equals("")) {
				if (filter == GameList.ALL) {

					listData.add(addListItem(gl.getInf(GameList.TITLE, i),
							getFlagId(i)));

					names.add(gl.getInf(GameList.TITLE, i));

					index[j] = i;
					j++;
				}
				if (filter == GameList.INSTALLED) {
					if (gl.getFlag(i) == GameList.INSTALLED) {
						listData.add(addListItem(gl.getInf(GameList.TITLE, i),
								getFlagId(i)));

						names.add(gl.getInf(GameList.TITLE, i));

						index[j] = i;
						j++;
					}
				}

				if (filter == GameList.UPDATE) {
					if (gl.getFlag(i) == GameList.UPDATE) {
						listData.add(addListItem(gl.getInf(GameList.TITLE, i),
								getFlagId(i)));

						names.add(gl.getInf(GameList.TITLE, i));

						index[j] = i;
						j++;
					}
				}

				if (filter == GameList.NEW) {
					if (gl.getFlag(i) == GameList.NEW) {
						listData.add(addListItem(gl.getInf(GameList.TITLE, i),
								getFlagId(i)));

						names.add(gl.getInf(GameList.TITLE, i));
						// images.add(this.getResources().getDrawable(getFlagId(i)));
						index[j] = i;
						j++;
					}
				}

				// //Log.d(Globals.TAG,"Title: "+gl.getInf(GameList.TITLE, i));
			}
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, listData,
				R.layout.list_item, new String[] { LIST_TEXT },
				new int[] { R.id.list_text });
		simpleAdapter.setViewBinder(this);
		setListAdapter(simpleAdapter);

		// getListView().setSelection(2);

		// FIXME android 1.6 refresh bug workaround
		if (Build.VERSION.SDK_INT == Build.VERSION_CODES.DONUT) {
			setTabsG();
		} else {
			setTabs();
		}

	}

	@Override
	public boolean setViewValue(View view, Object data,
			String stringRepresetation) {
		ListItem listItem = (ListItem) data;

		TextView menuItemView = (TextView) view;
		menuItemView.setText(listItem.text);
		menuItemView.setCompoundDrawablesWithIntrinsicBounds(this
				.getResources().getDrawable(listItem.icon), null, null, null);

		return true;
	}

	private void listDownload() {
		dialog.setMessage(getString(R.string.init));

		ShowDialog();

		new XmlDownloader(this, dialog, listNo);
	}

	private void Download() {

		dialog.setMessage(getString(R.string.init));

		ShowDialogCancel();

		dialog.setCancelable(true);

		dwn = true;
		downloader = new GameDownloader(this, gl.getInf(GameList.URL,
				index[item_index]),
				gl.getInf(GameList.NAME, index[item_index]), dialog);

		// Toast.makeText(this, gl.getInf(GameList.TITLE, index[item_index] ),
		// Toast.LENGTH_LONG).show();
	}

	public void listIsDownload() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
	}

	public void gameIsDownload() {
		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		dwn = false;
		isdwn = false;
		// listUpdate();
		downloader = null;
		try {
			Toast.makeText(
					this,
					getString(R.string.gdwncompl) + ": "
							+ gl.getInf(GameList.TITLE, index[item_index]),
					Toast.LENGTH_LONG).show();
		} catch (ArrayIndexOutOfBoundsException e) {
		}

	}

	private void gameDelete() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Delete();
					break;

				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}
		};

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.warning);
		builder.setTitle(g);
		builder.setMessage(getString(R.string.yesno))
				.setPositiveButton(getString(R.string.yes), dialogClickListener)
				.setNegativeButton(getString(R.string.no), dialogClickListener)
				.show();

	}

	void Delete() {
		Globals.delete(new File(Globals.getOutFilePath(Globals.GameDir
				+ gl.getInf(GameList.NAME, index[item_index]))));
		listUpdate();
		Toast.makeText(
				this,
				getString(R.string.delgame) + ": "
						+ gl.getInf(GameList.TITLE, index[item_index]),
				Toast.LENGTH_LONG).show();

	}

	private void gameDownload() {

		DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					Download();
					break;

				case DialogInterface.BUTTON_NEGATIVE:

					break;
				}
			}
		};

		String s = gl.getInf(GameList.SIZE, index[item_index]);
		if (s == null)
			s = getString(R.string.na);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setIcon(R.drawable.question);
		builder.setTitle(g);
		builder.setMessage(
				getString(R.string.dwnsize) + " " + s + " " + ".\n"
						+ getString(R.string.sizeyesno))
				.setPositiveButton(getString(R.string.yes), dialogClickListener)
				.setNegativeButton(getString(R.string.no), dialogClickListener)
				.show();

	}

	private void gameUpdate() {
		// gameDelete();
		gameDownload();
	}

	private void startAbout() {
		Intent myIntent = new Intent(this, AboutGame.class);
		Bundle b = new Bundle();
		b.putString("name", gl.getInf(GameList.NAME, index[item_index]));
		b.putString("title", gl.getInf(GameList.TITLE, index[item_index]));
		b.putString("lang", gl.getInf(GameList.LANG, index[item_index]));
		b.putString("ver", gl.getInf(GameList.VERSION, index[item_index]));
		b.putString("file", gl.getInf(GameList.URL, index[item_index]));
		b.putString("url", gl.getInf(GameList.DESCURL, index[item_index]));
		b.putString("size", gl.getInf(GameList.SIZE, index[item_index]));
		b.putInt("flag", gl.getFlag(index[item_index]));
		b.putInt("INSTALLED", GameList.INSTALLED);
		b.putInt("UPDATE", GameList.UPDATE);
		b.putInt("NEW", GameList.NEW);
		myIntent.putExtras(b);
		startActivity(myIntent);

	}

	private void startApp() {

		if (checkInstall()) {
			// rewriteRC(gl.getInf(GameList.NAME, index[item_index]));
			Intent myIntent = new Intent(this, SDLActivity.class);
			Bundle b = new Bundle();
			b.putString("game", gl.getInf(GameList.NAME, index[item_index]));
			myIntent.putExtras(b);
			startActivity(myIntent);
		}
	}

	@Override
	protected void onPause() {
		// Log.d(Globals.TAG, "GM: Pause");

		if (dialog.isShowing()) {
			dialog.dismiss();
		}
		onpause = true;
		super.onPause();
	}

	@Override
	protected void onStop() {
		// Log.d(Globals.TAG, "GM: Pause");
		super.onStop();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (downloader != null) {
			if (downloader.DownloadComplete) {
				if (dialog.isShowing()) {
					dialog.dismiss();
				}
				wakeLockR();
				gameIsDownload();

			} else {
				if (onpause && !dialog.isShowing()) {
					dialog.show();
				}
			}
		} else {
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			wakeLockR();
			// FIXME
			if (isdwn) {
				gameIsDownload();
			}
		}

		checkXml();
		onpause = false;
		// Log.d(Globals.TAG, "GM: Resume");
	}

	public boolean getStopDwn() {
		if (!onpause) {
			if (!dialog.isShowing() && dwn) {
				dwn = false;
				return true;
			}
		}
		return false;
	}

	public void sayCancel() {
		downloader = null;
		// listUpdate();
		Toast.makeText(
				this,
				getString(R.string.dwncancel) + ": "
						+ gl.getInf(GameList.TITLE, index[item_index]),
				Toast.LENGTH_LONG).show();
	}

	protected void checkXml() {
		if (!isFile(getGameListName(listNo))) {
			listDownload();
		} else {
			listUpdate();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		// Log.d(Globals.TAG, "GM: Resume");
	}

	public void setDownGood() {
		dwn = false;
		isdwn = true;
	}

	public void setXmlGood() {
		dwn = false;
	}

	private boolean isFile(String s) {
		String path = Globals.getOutFilePath(s);
		InputStream checkFile = null;
		try {
			checkFile = new FileInputStream(path);
		} catch (FileNotFoundException e) {
		} catch (SecurityException e) {
		}
		;
		if (checkFile != null) {
			checkFile = null;
			return true;
		}
		checkFile = null;
		return false;
	}

	public boolean checkInstall() {
		String path = Globals.getOutFilePath(Globals.DataFlag);
		@SuppressWarnings("unused")
		InputStream checkFile = null;
		try {
			checkFile = new FileInputStream(path);
		} catch (FileNotFoundException e) {
		} catch (SecurityException e) {
		}
		;
		checkFile = null;
		return true;
	}

	private class ListItem {
		public String text;

		public int icon;
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		savedInstanceState.putBoolean("onpause", onpause);
		savedInstanceState.putBoolean("dwn", dwn);
		savedInstanceState.putBoolean("isdwn", isdwn);
		savedInstanceState.putString("lang_filter", lang_filter);
		savedInstanceState.putInt("listNo", listNo);
		savedInstanceState.putInt("filter", filter);
		savedInstanceState.putInt("pose", item_index);
		super.onSaveInstanceState(savedInstanceState);
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
		dwn = savedInstanceState.getBoolean("dwn");
		isdwn = savedInstanceState.getBoolean("isdwn");
		onpause = savedInstanceState.getBoolean("onpause");
		lang_filter = savedInstanceState.getString("lang_filter");
		listNo = savedInstanceState.getInt("listNo");
		filter = savedInstanceState.getInt("filter");
		item_index = savedInstanceState.getInt("pose");
	}

}
