package com.android.jupiter.sunshine.test;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.util.Log;

import com.android.jupiter.sunshine.data.WeatherDbHelper;

import java.util.Map;

import static com.android.jupiter.sunshine.data.WeatherContract.LocationEntry;
import static com.android.jupiter.sunshine.data.WeatherContract.WeatherEntry;

public class TestDb extends AndroidTestCase {
    public static final String LOG_TAG = TestDb.class.getSimpleName();
    public static final String CITY_NAME = "North Pole";

    public static final String TEST_LOCATION = "Singapore";
    public static final String TEST_DATE = "20141205";

    public void testCreateDb() throws Throwable {
        mContext.deleteDatabase(WeatherDbHelper.DATABASE_NAME);

        SQLiteDatabase db = new WeatherDbHelper(this.mContext).getWritableDatabase();

        assertEquals(true, db.isOpen());
        db.close();
    }

    public void testInsertReadDb() {
        ContentValues locationValues = getLocationValues();

        // If there's an error in those massive SQL table creation Strings,
        // errors will be thrown here when you try to get a writable database.
        WeatherDbHelper dbHelper = new WeatherDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long locationRowId;
        locationRowId = db.insert(LocationEntry.TABLE_NAME, null, locationValues);

        // Verify we got a row back.
        assertTrue(locationRowId != -1);
        Log.d(LOG_TAG, "New row id: " + locationRowId);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor cursor = db.query(
                LocationEntry.TABLE_NAME,  // Table to Query
                null, // Columns to select; null select *
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        if (cursor.moveToFirst()) {
            validateCursorValues(locationValues, cursor);

            // Fantastic.  Now that we have a location, add some weather!
            ContentValues weatherValues = getWeatherValues(locationRowId);
            final long weatherRowId = db.insert(WeatherEntry.TABLE_NAME, null, weatherValues);

            // verify insert success
            assertTrue(weatherRowId != -1);

            // Read inserted weather information
            cursor = db.query(WeatherEntry.TABLE_NAME, null, null, null, null, null, null);

            if (cursor.moveToNext()) {
                validateCursorValues(weatherValues, cursor);
            } else {
                // That's weird, it works on MY machine...
                fail("No values returned  for Weather:(");
            }
            dbHelper.close();
        } else {
            // That's weird, it works on MY machine...
            fail("No values returned :(");
        }
    }

    private ContentValues getWeatherValues(long locationRowId) {
        ContentValues weatherValues = new ContentValues();
        weatherValues.put(WeatherEntry.COLUMN_LOC_KEY, locationRowId);
        weatherValues.put(WeatherEntry.COLUMN_DATETEXT, "20141205");
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

    public static void validateCursorValues(ContentValues locationValues, Cursor cursor) {
        for (Map.Entry<String, Object> locationEntry : locationValues.valueSet()) {
            final int columnIndex = cursor.getColumnIndex(locationEntry.getKey());


            assertTrue(columnIndex != -1);
            assertEquals(locationEntry.getValue().toString(), cursor.getString(columnIndex));
        }
    }

    private ContentValues getLocationValues() {
        // Test data we're going to insert into the DB to see if it works.
        String testLocationSetting = "99705";
        double testLatitude = 64.7488;
        double testLongitude = -147.353;


        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(LocationEntry.COLUMN_LOCATION_SETTING, testLocationSetting);
        values.put(LocationEntry.COLUMN_CITY_NAME, CITY_NAME);
        values.put(LocationEntry.COLUMN_COORD_LAT, testLatitude);
        values.put(LocationEntry.COLUMN_COORD_LONG, testLongitude);
        return values;
    }
}