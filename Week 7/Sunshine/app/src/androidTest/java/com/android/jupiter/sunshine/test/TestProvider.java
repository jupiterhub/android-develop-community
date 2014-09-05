package com.android.jupiter.sunshine.test;

import android.annotation.TargetApi;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.android.jupiter.sunshine.data.WeatherDbHelper;

import java.util.Map;

import static com.android.jupiter.sunshine.data.WeatherContract.LocationEntry;
import static com.android.jupiter.sunshine.data.WeatherContract.WeatherEntry;

public class TestProvider extends AndroidTestCase {
    public static final String LOG_TAG = TestProvider.class.getSimpleName();
    public static final String CITY_NAME = "North Pole";
    public static final String CITY_NAME_UPDATE = "South Pole";
    public static final String MAX_TEMP_UPDATE = "99999999";

    @Override
    protected void setUp() throws Exception {
        // Cleanup records before executing tests
        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();
        db.delete(WeatherEntry.TABLE_NAME, null, null);
        db.delete(LocationEntry.TABLE_NAME, null, null);
        db.close();
    }

    public void testDeleteProvider() throws Throwable {
        // Given
        ContentValues locationValues = getLocationValues();
        final Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);
        mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, getWeatherValues((ContentUris.parseId(locationUri))));

        // When
        int deletedLocations = mContext.getContentResolver().delete(LocationEntry.CONTENT_URI, null, null);
        int deletedWeathers = mContext.getContentResolver().delete(WeatherEntry.CONTENT_URI, null, null);

        // Then
        assertEquals(1, deletedLocations);
        assertEquals(1, deletedWeathers);
    }

    public void testUpdateProvider() {
        // Given
        ContentValues locationValues = getLocationValues();
        final Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);

        final ContentValues weatherValues = getWeatherValues((ContentUris.parseId(locationUri)));
        final Uri weatherUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);


        // When
        locationValues.put(LocationEntry.COLUMN_CITY_NAME, CITY_NAME_UPDATE);
        final int updatedLocationEntries = mContext.getContentResolver().update(LocationEntry.CONTENT_URI, locationValues, null, null);

        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, MAX_TEMP_UPDATE);
        final int updatedWeatherEntries = mContext.getContentResolver().update(WeatherEntry.CONTENT_URI, weatherValues, null, null);

        // Then
        assertEquals(1, updatedLocationEntries);
        assertEquals(1, updatedWeatherEntries);

    }

    public void testGetType() {
        // content://com.jupiter.android.sunshine.app/weather/
        String type = mContext.getContentResolver().getType(WeatherEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.jupiter.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);


        String testLocation = "94074";
        // content://com.jupiter.android.sunshine.app/weather/94074
        type = mContext.getContentResolver().getType(WeatherEntry.buildWeatherLocation(testLocation));
        // vnd.android.cursor.dir/com.jupiter.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_TYPE, type);

        String testDate = "20140612";
        // content://com.jupiter.android.sunshine.app/weather/94074/20140612
        type = mContext.getContentResolver().getType(
                WeatherEntry.buildWeatherLocationWithDate(testLocation, testDate));
        // vnd.android.cursor.item/com.jupiter.android.sunshine.app/weather
        assertEquals(WeatherEntry.CONTENT_ITEM_TYPE, type);

        // content://com.jupiter.android.sunshine.app/location/
        type = mContext.getContentResolver().getType(LocationEntry.CONTENT_URI);
        // vnd.android.cursor.dir/com.jupiter.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_TYPE, type);

        // content://com.jupiter.android.sunshine.app/location/1
        type = mContext.getContentResolver().getType(LocationEntry.buildLocationUri(1L));
        // vnd.android.cursor.item/com.jupiter.android.sunshine.app/location
        assertEquals(LocationEntry.CONTENT_ITEM_TYPE, type);

    }


    public void testInsertReadProvider() {
        ContentValues locationValues = getLocationValues();

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        Uri locationUri = mContext.getContentResolver().insert(LocationEntry.CONTENT_URI, locationValues);

        // Verify we got a row back.
        assertNotNull(locationUri);
        final long locationRowId = ContentUris.parseId(locationUri);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                LocationEntry.buildLocationUri(locationRowId),  // Contet to query or LocationEntry.CONTENT_URI
                null, // Columns to select; null select *
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null // sort order
        );

        if (cursor.moveToFirst()) {
            validateCursorValues(locationValues, cursor);

            // Fantastic.  Now that we have a location, add some weather!
            ContentValues weatherValues = getWeatherValues(locationRowId);
            final Uri weatherEntryUri = mContext.getContentResolver().insert(WeatherEntry.CONTENT_URI, weatherValues);
            final long weatherRowId = ContentUris.parseId(weatherEntryUri);

            // verify insert success
            assertTrue(weatherRowId != -1);

            // Read inserted weather information
            cursor = mContext.getContentResolver().query(
                    WeatherEntry.CONTENT_URI,  // Table to Query
                    null, // Columns to select; null select *
                    null, // Columns for the "where" clause
                    null, // Values for the "where" clause
                    null // sort order
            );

            if (cursor.moveToNext()) {
                validateCursorValues(weatherValues, cursor);
            } else {
                // That's weird, it works on MY machine...
                fail("No values returned  for Weather:(");
            }

            // Good practice to close cursor after querying
            cursor.close();

            // Get the joined Weather and Location data
            cursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocation(TestDb.TEST_LOCATION),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );
            cursor.moveToNext();
            TestDb.validateCursorValues(weatherValues, cursor);
            cursor.close();


            // Get the joined Weather and Location data with a start date
            cursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationWithStartDate(
                            TestDb.TEST_LOCATION, TestDb.TEST_DATE),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );
            cursor.moveToNext();
            TestDb.validateCursorValues(weatherValues, cursor);
            cursor.close();

            // Get the joined Weather and Location data with a start date
            cursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationWithStartDate(
                            TestDb.TEST_LOCATION, TestDb.TEST_DATE),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null      // sort order
            );
            cursor.moveToNext();
            TestDb.validateCursorValues(weatherValues, cursor);
            cursor.close();

            // Get the joined Weather and Location data with a specific day
            cursor = mContext.getContentResolver().query(
                    WeatherEntry.buildWeatherLocationWithDate(
                            TestDb.TEST_LOCATION, TestDb.TEST_DATE),
                    null, // leaving "columns" null just returns all the columns.
                    null, // cols for "where" clause
                    null, // values for "where" clause
                    null  // sort order
            );
            cursor.moveToNext();
            TestDb.validateCursorValues(weatherValues, cursor);
            cursor.close();
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }
    }

    private ContentValues getWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, TestDb.TEST_DATE);
        weatherValues.put(WeatherEntry.COLUMN_DEGREES, 1.1);
        weatherValues.put(WeatherEntry.COLUMN_HUMIDITY, 1.2);
        weatherValues.put(WeatherEntry.COLUMN_PRESSURE, 1.3);
        weatherValues.put(WeatherEntry.COLUMN_MAX_TEMP, 75);
        weatherValues.put(WeatherEntry.COLUMN_MIN_TEMP, 65);
        weatherValues.put(WeatherEntry.COLUMN_SHORT_DESC, "Asteroids");
        weatherValues.put(WeatherEntry.COLUMN_WIND_SPEED, 5.5);
        weatherValues.put(WeatherEntry.COLUMN_WEATHER_ID, 321);
        return weatherValues;
    }

    private void validateCursorValues(ContentValues locationValues, Cursor cursor) {
        for (Map.Entry<String, Object> locationEntry : locationValues.valueSet()) {
            final int columnIndex = cursor.getColumnIndex(locationEntry.getKey());

            assertTrue(columnIndex != -1);
            assertEquals(locationEntry.getValue().toString(), cursor.getString(columnIndex));
        }
    }

    private ContentValues getLocationValues() {
        // Test data we're going to insert into the DB to see if it works.
        double testLatitude = 64.7488;
        double testLongitude = -147.353;


        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, TestDb.TEST_LOCATION);
        values.put(LocationEntry.COLUMN_CITY_NAME, CITY_NAME);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return values;
    }

    // The target api annotation is needed for the call to keySet -- we wouldn't want
    // to use this in our app, but in a test it's fine to assume a higher target.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    void addAllContentValues(ContentValues destination, ContentValues source) {
        for (String key : source.keySet()) {
            destination.put(key, source.getAsString(key));
        }
    }
}