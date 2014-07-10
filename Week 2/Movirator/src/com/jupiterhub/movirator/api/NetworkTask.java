package com.jupiterhub.movirator.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public abstract class NetworkTask<T> extends AsyncTask<String, Void, T> {

	private Context context;
	private ProgressDialog dialog;
	
	public NetworkTask(Context context) {
		this.context = context;
	}

	protected abstract T convertJSONResults(String json);
	
	@Override
	protected T doInBackground(String... uri) {
		return convertJSONResults(executeHttpRequest(uri[0]));
	}
	
	
	@Override
	protected void onPreExecute() {
		dialog = ProgressDialog.show(context, "", "Hunting movie goers..");
		super.onPreExecute();
	}
	
	@Override
	protected void onPostExecute(T result) {
		dialog.dismiss();
		super.onPostExecute(result);
	}

	private String executeHttpRequest(String uri) {
		HttpClient httpClient = new DefaultHttpClient();
		HttpGet get = new HttpGet(uri);
		StringBuilder httpEntityResponse = new StringBuilder();
		
		InputStream in = null;
		try {
			HttpResponse response = httpClient.execute(get);
			int statusCode = response.getStatusLine().getStatusCode();
		
			if (statusCode == 200) {
				in = response.getEntity().getContent();
				BufferedReader reader = new BufferedReader(new InputStreamReader(in));

				String line;
				while ((line = reader.readLine()) != null) {
					httpEntityResponse.append(line);
				}
				
				Log.d("HttpRequest", "Request success");
			} else {
				Log.d("HttpRequest", "Response is not 200");
			}
			
		} catch (ClientProtocolException e) {
			Log.e("HttpRequest", e.getMessage());
			e.printStackTrace();
		} catch (IOException e) {
			Log.e("HttpRequest", e.getMessage());
			e.printStackTrace();
		}  finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return httpEntityResponse.toString();
	}

	protected Context getContext() {
		return context;
	}
	
	
}