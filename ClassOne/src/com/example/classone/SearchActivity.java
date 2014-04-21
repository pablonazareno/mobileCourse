package com.example.classone;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

public class SearchActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search);
	}
	
	public void showSearching(View view) {
	    ProgressBar bar= (ProgressBar) findViewById(R.id.progressBar);
	    bar.setVisibility(View.VISIBLE);
	}
}
