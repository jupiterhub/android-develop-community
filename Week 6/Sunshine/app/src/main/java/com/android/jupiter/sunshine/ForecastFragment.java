package com.android.jupiter.sunshine;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;


public class ForecastFragment extends Fragment {
    private ArrayAdapter<String> arrayAdapter;

    public ForecastFragment() {
    }

    @SuppressLint("Override")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_my, container, false);
        this.setHasOptionsMenu(true);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String>());

        listView.setAdapter(arrayAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Intent intent = new Intent(getActivity(), DetailActivity.class);
                intent.putExtra(Intent.EXTRA_TEXT, arrayAdapter.getItem(position));
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    @SuppressLint("Override")
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfragment, menu);
    }

    @SuppressLint("Override")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refresh:
                updateWeatherInfo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateWeatherInfo() {
        final String location = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
        new FetchWeatherTask(arrayAdapter).execute(location);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateWeatherInfo();
    }
}