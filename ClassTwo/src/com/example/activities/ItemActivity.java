package com.example.activities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.classtwo.R;
import com.example.domains.Item;

public class ItemActivity extends Activity{
	
	public static final String ITEM_ID= "com.example.activities.ItemActivity.ItemId";
	public static final String ITEM= "com.example.activities.ItemActivity.Item";
	private static final String ITEM_URL= "https://api.mercadolibre.com/items/";
	private static final String ITEM_ATTRIBUTES= "?attributes=id,title,subtitle,price,currency_id,sold_quantity,thumbnail,pictures";
	
	private Item item;
	private String itemId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_item);
	    Intent intent= getIntent();
	    itemId= intent.getStringExtra(ITEM_ID);
	    item= (Item)intent.getSerializableExtra(ITEM);
	    if(item!=null){
	    	loadItemInformation(item);
	    }else{
	    	ItemFinderTask task= new ItemFinderTask();
	    	task.execute(itemId);
	    }
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable(ITEM_ID, itemId);
		if(item!=null){
			outState.putSerializable(ITEM, item);
		}		
	}
	
	private void loadItemInformation(Item item){
		TextView title= (TextView)findViewById(R.id.title);
		TextView subTitle= (TextView)findViewById(R.id.subtitle);
		ImageView picture= (ImageView)findViewById(R.id.picture);
		TextView price= (TextView)findViewById(R.id.price);
		
		title.setText(item.getTitle());
		subTitle.setText(item.getSubTitle());
		picture.setImageResource(R.drawable.nopic);;
		price.setText(item.getPrice().toString());
	}
	
	private class ItemFinderTask extends AsyncTask<String, Void, Item>{

		@Override
		protected Item doInBackground(String... params) {
			String itemId= params[0];
			StringBuilder sb= new StringBuilder(ITEM_URL);
			sb.append(itemId);
			sb.append(ITEM_ATTRIBUTES);
			String data= null;
			Item item= null;
			try {
				data = downloadUrl(sb.toString());
				JSONObject json=new JSONObject(data);
				item= new Item();
				item.setItemId(json.getString("id"));
				item.setTitle(json.getString("title"));
				item.setSubTitle(json.getString("subtitle"));
				item.setPrice(json.getDouble("price"));
			    item.setCurrency(json.getString("currency_id"));
			    item.setThumbnailURL(json.getString("thumbnail"));
				item.setSoldQuantity(json.getInt("sold_quantity"));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return item;
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
		
		@Override
		protected void onPostExecute(Item result) {
			loadItemInformation(result);
		}
		
	}
}
