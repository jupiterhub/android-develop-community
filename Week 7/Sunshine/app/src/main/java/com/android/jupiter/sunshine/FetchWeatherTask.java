package com.android.jupiter.sunshine;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

    private final String LOG_TAG = FetchWeatherTask.class.getSimpleName();

    private ArrayAdapter<String> arrayAdapter;
    private Context mContext;

    public FetchWeatherTask(Context context, ArrayAdapter<String> arrayAdapter) {
        mContext = context;
        this.arrayAdapter = arrayAdapter;
    }

    @Override
    protected void onPostExecute(String[] daysForecast) {
        if (daysForecast != null) {
            arrayAdapter.clear();
            arrayAdapter    .addAll(Arrays.asList(daysForecast));
        }
    }

    @Override
    protected String[] doInBackground(String... strings) {

        String forecastJsonString = null;
        HttpURLConnection urlConnection = null;
        BufferedReader bufferedReader = null;
        try {
            final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily";
            final String PARAM_LOCATION = "q";
            final String PARAM_MODE = "mode";
            final String PARAM_UNITS = "units";
            final String PARAM_DAYS = "cnt";

            final String json = "JSON";
            final String metric = "metric";
            final String noOfDays = "7";

            Uri builder = Uri.parse(FORECAST_BASE_URL).buildUpon()
                    .appendQueryParameter(PARAM_LOCATION, strings[0])
                    .appendQueryParameter(PARAM_MODE, json)
                    .appendQueryParameter(PARAM_UNITS, metric)
                    .appendQueryParameter(PARAM_DAYS, noOfDays).build();


            URL url = new URL(builder.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do
                return null;
            }

            bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;

            }

            forecastJsonString = buffer.toString();

            final String[] weatherDataFromJson = new WeatherDataParser(mContext).getWeatherDataFromJson(forecastJsonString, Integer.valueOf(noOfDays));

            return weatherDataFromJson;

        } catch (IOException ioException) {
            Log.e(LOG_TAG, "Error", ioException);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.
            forecastJsonString = null;
            return null;
        } catch (JSONException e) {
            // Unable to parse JSON string
            Log.e(LOG_TAG, "Error", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error Closing Stream", e);
                }
            }
        }
    }
}
