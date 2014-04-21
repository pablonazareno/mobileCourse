package com.example.classtwo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.example.domains.Item;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ListActivity extends Activity {
	
	public static String SEARCH_KEY= "com.example.classtwo.searchKey";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list);
		
	    Intent intent = getIntent();
	    String message = intent.getStringExtra(ListActivity.SEARCH_KEY);
	    
	    // Create the text view
	    TextView textView = (TextView) findViewById(R.id.searching_key);
	    
	    ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
    	    textView.setText(message);
        	new SearchAsyncTask(this).execute(message);
        }else{
    	    textView.setText("No Network connection available.");
    	    ProgressBar bar= (ProgressBar) findViewById(R.id.progressBar);
    	    bar.setVisibility(View.GONE);
        }
	}
	
	private class SearchAsyncTask extends AsyncTask<String, Void, String>{
		private String SEARCH_URL= "https://api.mercadolibre.com/sites/MLA/search?limit=100&q=";
       
		Context context;
		
		public SearchAsyncTask(Context context) {
			super();
			this.context=context;
		}
		@Override
        protected void onPreExecute() {
        };
        
		@Override
		protected String doInBackground(String... params) {
			StringBuilder sb= new StringBuilder(SEARCH_URL);
			sb.append(URLEncoder.encode(params[0]));
			String data= null;
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
        	ProgressBar bar= (ProgressBar) findViewById(R.id.progressBar);
    	    bar.setVisibility(View.GONE);
    	    try {
				JSONObject json=new JSONObject(result);
				List<Item> items=new ArrayList<Item>();
				JSONArray itemsJSON= (JSONArray) json.get("results");
				for (int i = 0; i < itemsJSON.length(); i++) {
				    JSONObject row = itemsJSON.getJSONObject(i);
				    Item item= new Item();
				    item.setCurrency(row.getString("currency_id"));
				    item.setItemId(row.getString("id"));
				    item.setPrice(row.getDouble("price"));
				    item.setTitle(row.getString("title"));
				    items.add(item);
				}
				ListView listView= (ListView) findViewById(R.id.items_list);
				ArrayAdapter<Item> adapter= new ArrayAdapter<Item>(context, android.R.layout.simple_list_item_1,items);
				listView.setAdapter(adapter);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        
        @Override
        protected void onProgressUpdate(Void... values) {
        	// TODO Auto-generated method stub
        	super.onProgressUpdate(values);
        }
        
        private String downloadUrl(String urlString) throws IOException {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            //conn.setDoInput(true);
            // Starts the query
            conn.connect();
            InputStream stream = conn.getInputStream();
            InputStreamReader inputStreamReader= new InputStreamReader(stream);
            BufferedReader bufferedReader= new BufferedReader(inputStreamReader);
            StringBuilder sb= new StringBuilder();
            String line= bufferedReader.readLine();
            while(line!=null){
            	sb.append(line);
            	line= bufferedReader.readLine();
            }
            return sb.toString();
        }
	}
}
