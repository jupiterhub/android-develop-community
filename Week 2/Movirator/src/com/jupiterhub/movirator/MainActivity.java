package com.jupiterhub.movirator;

import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.jupiterhub.movirator.api.MovieSearchTask;
import com.jupiterhub.movirator.vo.Movie;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	public void performApiRequest(View view) {
		try {
			EditText searchValue = (EditText) findViewById(R.id.searchText);
			String textValue = searchValue.getText().toString().trim();
			Movie movie = new MovieSearchTask(this).execute("http://www.omdbapi.com/?t=" + URLEncoder.encode(textValue, "UTF-8")).get();
			
			makeItVisibile(R.id.titleLabel);
			setTextValue(R.id.titleValue, movie.getTitle());
			
			makeItVisibile(R.id.releasedLabel);
			setTextValue(R.id.releasedValue, movie.getYear());
			
			makeItVisibile(R.id.runtimeLabel);
			setTextValue(R.id.runtime, movie.getRuntime());

			makeItVisibile(R.id.ratingLabel);
			setTextValue(R.id.ratingValue, movie.getImdbRating());
			
			makeItVisibile(R.id.image);
		} catch (Exception e) {
			Log.e("MainActivity", e.getMessage());
			e.printStackTrace();
		}
	}

	private void setTextValue(int elementId, String text) {
		TextView rating = (TextView) findViewById(elementId);
		rating.setText(text);
	}

	private void makeItVisibile(int elementId) {
		((View)findViewById(elementId)).setVisibility(View.VISIBLE);
	}
}