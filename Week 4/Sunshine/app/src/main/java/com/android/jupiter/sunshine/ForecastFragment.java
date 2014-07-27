package com.android.jupiter.sunshine;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
        View rootView = inflater.inflate(R.layout.fragment_my, container, false);
        this.setHasOptionsMenu(true);

        ListView listView = (ListView) rootView.findViewById(R.id.listview_forecast);
        final String[] defaultForecast = {"Day - Weather - Min / Max"};
        arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, new ArrayList<String>(Arrays.asList(defaultForecast)));

        listView.setAdapter(arrayAdapter);

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
            case R.id.action_search:
                new FetchWeatherTask(arrayAdapter).execute("Singapore");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}