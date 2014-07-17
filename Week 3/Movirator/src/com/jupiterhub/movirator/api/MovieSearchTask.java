package com.jupiterhub.movirator.api;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.jupiterhub.movirator.vo.Movie;

public class MovieSearchTask extends NetworkTask<Movie> {

	public MovieSearchTask(Context context) {
		super(context);
	}

	@Override
	protected Movie convertJSONResults(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			String title = jsonObject.getString("Title");
			String year = jsonObject.getString("Year");
			String runtime = jsonObject.getString("Runtime");
			String imdbRating = jsonObject.getString("imdbRating");
			return new Movie(title, year, runtime, imdbRating);
		} catch (JSONException e) {
			Log.e("MovieSearchTask", e.getMessage());
			e.printStackTrace();
			return new Movie("N.A.", "N.A.", "N.A.", "N.A.");
		}
	}
}