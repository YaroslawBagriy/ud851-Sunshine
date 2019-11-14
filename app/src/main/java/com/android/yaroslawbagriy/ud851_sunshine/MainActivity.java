package com.android.yaroslawbagriy.ud851_sunshine;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.yaroslawbagriy.ud851_sunshine.data.SunshinePreferences;
import com.android.yaroslawbagriy.ud851_sunshine.utilities.NetworkUtils;
import com.android.yaroslawbagriy.ud851_sunshine.utilities.OpenWeatherJsonUtils;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    private TextView mErrorMessageDisplay;

    private ProgressBar mLoadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        // Get a reference to the weather display TextView
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        // Get a reference to error message display TextView
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        // Get a reference to the progress bar
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        // Load weather data once all the views have been loaded
        loadWeatherData();
    }

    private void loadWeatherData() {
        showWeatherDataView();
        String preferredLocation = SunshinePreferences.getPreferredWeatherLocation(this);
        new FetchWeatherTask().execute(preferredLocation);
    }

    private void showWeatherDataView() {
        // Make error invisible
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        // Make weather view visible
        mWeatherTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        // Make error visible
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
        // Make weather view invisible
        mWeatherTextView.setVisibility(View.INVISIBLE);
    }

    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected String[] doInBackground(String... params) {
            if (params.length == 0) {
                return null;
            }

            String location = params[0];
            URL weatherRequestURL = NetworkUtils.buildUrl(location);

            try {
                String jsonWeatherResponse = NetworkUtils.getResponseFromHttpUrl(weatherRequestURL);
                String[] simpleJSONWeatherData = OpenWeatherJsonUtils
                        .getSimpleWeatherStringsFromJson(MainActivity.this, jsonWeatherResponse);
                return  simpleJSONWeatherData;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String[] weatherData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);

            if (weatherData != null) {
                showWeatherDataView();
                for (String weatherString : weatherData) {
                    mWeatherTextView.append((weatherString) + "\n\n\n");
                }
            } else {
                showErrorMessage();
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.forecast, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_refresh) {
            mWeatherTextView.setText("");
            loadWeatherData();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
