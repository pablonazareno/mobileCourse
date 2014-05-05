package com.example.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.adapters.ItemListAdapter;
import com.example.classtwo.R;
import com.example.domains.Item;

public class ListActivity extends Activity {

	public static String SEARCH_KEY = "com.example.classtwo.searchKey";
	public static String ITEMS_KEY = "com.example.classtwo.itemsKey";
	private ArrayList<Item> itemsList;
	private boolean fetchingItems = false;
	private int offset = 0;
	private String searchKey;

	@SuppressWarnings("unchecked")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);

		Intent intent = getIntent();
		searchKey = intent.getStringExtra(ListActivity.SEARCH_KEY);

		// Create the text view
		TextView textView = (TextView) findViewById(R.id.searching_key);
		textView.setText(searchKey);
		ListView listView = (ListView) findViewById(R.id.items_list);
		listView.setOnItemClickListener(new MyItemClickListener());
		listView.setOnScrollListener(new MyScrollListener());
		ItemListAdapter adapter = new ItemListAdapter(this,
				android.R.layout.simple_list_item_1, new ArrayList<Item>());
		listView.setAdapter(adapter);

		if (savedInstanceState != null) {
			Serializable serialItems = savedInstanceState
					.getSerializable(ITEMS_KEY);
			if (serialItems != null) {
				itemsList = (ArrayList<Item>) serialItems;
				offset = itemsList.size();
			}
		}
		if (itemsList == null) {
			searchContent(searchKey);
		} else {
			loadResult(itemsList);
			hideLoading();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ITEMS_KEY, itemsList);
	}

	private void searchContent(String message) {
		if (!fetchingItems) {
			fetchingItems=true;
			ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
			if (activeInfo == null || !activeInfo.isConnected()) {
				TextView textView = (TextView) findViewById(R.id.searching_key);
				textView.setText("No Network connection available.");
				hideItemList();
				hideLoading();
			} else {
				new SearchAsyncTask().execute(message);
			}
		}
	}

	private void loadResult(List<Item> items) {
		ListView listView = (ListView) findViewById(R.id.items_list);
		((ItemListAdapter) listView.getAdapter()).addAll(items);
		offset+=items.size();
		listView.setVisibility(View.VISIBLE);
	}

	private void hideItemList() {
		ListView listView = (ListView) findViewById(R.id.items_list);
		listView.setVisibility(View.GONE);
	}

	private void showLoading() {
		ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
		bar.setVisibility(View.VISIBLE);
	}

	private void hideLoading() {
		ProgressBar bar = (ProgressBar) findViewById(R.id.progressBar);
		bar.setVisibility(View.GONE);
	}
	
	private void openVip(Item item){
		Intent intent = new Intent(this, ItemActivity.class);
	    intent.putExtra(ItemActivity.ITEM_ID, item.getItemId());
	    intent.putExtra(ItemActivity.ITEM,item);
	    startActivity(intent);
	}

	/**********************************************************************/

	private class MyItemClickListener implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Log.d("ClickListener","click " + position + " - " + id);
			Item item= (Item)parent.getAdapter().getItem(position);
			openVip(item);
		}

	}

	private class MyScrollListener implements OnScrollListener {

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (firstVisibleItem + visibleItemCount == totalItemCount
					&& totalItemCount != 0) {
				if (fetchingItems == false) {
					showLoading();
					searchContent(searchKey);
				}
			}
		}

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}

	}

	private class SearchAsyncTask extends AsyncTask<String, Void, String> {
		private String SEARCH_URL = "https://api.mercadolibre.com/sites/MLA/search?limit=15&q=";
		private String OFFSET_PARAMETER = "&offset=";

		public SearchAsyncTask() {
			super();
		}

		@Override
		protected void onPreExecute() {
		};

		@Override
		protected String doInBackground(String... params) {
			StringBuilder sb = new StringBuilder(SEARCH_URL);
			sb.append(URLEncoder.encode(params[0]));
			sb.append(OFFSET_PARAMETER);
			sb.append(offset);
			String data = null;
			try {
				data = downloadUrl(sb.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				JSONObject json = new JSONObject(result);
				ArrayList<Item> items = new ArrayList<Item>();
				JSONArray itemsJSON = (JSONArray) json.get("results");
				for (int i = 0; i < itemsJSON.length(); i++) {
					JSONObject row = itemsJSON.getJSONObject(i);
					Item item = new Item();
					item.setCurrency(row.getString("currency_id"));
					item.setItemId(row.getString("id"));
					item.setPrice(row.getDouble("price"));
					item.setTitle(row.getString("title"));
					item.setThumbnailURL(row.getString("thumbnail"));
					items.add(item);
				}
				itemsList = items;
				loadResult(items);
				fetchingItems=false;
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			super.onProgressUpdate(values);
		}

		private String downloadUrl(String urlString) throws IOException {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			// conn.setDoInput(true);
			// Starts the query
			conn.connect();
			InputStream stream = conn.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(stream);
			BufferedReader bufferedReader = new BufferedReader(
					inputStreamReader);
			StringBuilder sb = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				sb.append(line);
				line = bufferedReader.readLine();
			}
			return sb.toString();
		}
	}
}
