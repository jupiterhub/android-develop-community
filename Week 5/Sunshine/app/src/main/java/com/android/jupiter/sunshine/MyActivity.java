package com.android.jupiter.sunshine;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;


public class MyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new ForecastFragment())
                    .commit();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_map) {
            useUserPreferredLocation();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void useUserPreferredLocation() {
        final String geolocation = PreferenceManager.getDefaultSharedPreferences(this).getString(
                getString(R.string.pref_location_key),
                getString(R.string.pref_location_default));
       final Uri geoLocationUri = Uri.parse("geo:0,0?").buildUpon()
               .appendQueryParameter("z", "11")
                .appendQueryParameter("q", geolocation).build();

        final Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocationUri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
}
