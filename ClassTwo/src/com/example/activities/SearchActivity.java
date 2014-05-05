package com.example.activities;

import com.example.classtwo.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

public class SearchActivity extends Activity {
	
	private static final String LAST_SEARCH_KEY="com.example.activities.SearchActivity.LastSearch";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
		SharedPreferences preferences= getPreferences(Context.MODE_PRIVATE);
		String previousSearch= preferences.getString(LAST_SEARCH_KEY, null);
		if(previousSearch!=null){
			EditText editText = (EditText) findViewById(R.id.search_text);
			editText.setText(previousSearch);
		}
	}
	
	public void makeSearch(View view) {
	    EditText editText = (EditText) findViewById(R.id.search_text);
	    String message = editText.getText().toString();
	    Log.d("APP", "Saving last search");
	    SharedPreferences preferences= getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor= preferences.edit();
		editor.putString(LAST_SEARCH_KEY, message);
		editor.commit();
		Log.d("APP","Searching for "+message);
	    Intent intent = new Intent(this, ListActivity.class);
	    intent.putExtra(ListActivity.SEARCH_KEY, message);
	    startActivity(intent);
	}
}
