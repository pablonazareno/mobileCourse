package com.example.classtwo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SearchActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
	}
	
	public void makeSearch(View view) {
	    Intent intent = new Intent(this, ListActivity.class);
	    EditText editText = (EditText) findViewById(R.id.search_text);
	    String message = editText.getText().toString();
	    intent.putExtra(ListActivity.SEARCH_KEY, message);
	    startActivity(intent);
	}
}
