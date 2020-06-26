package com.example.flixster;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

public class MainActivity extends AppCompatActivity {
    public static final String URL_PREFIX = "https://api.themoviedb.org/3/";
    public static final String NOW_PLAYING = "movie/now_playing";
    public static final String MOVIE_GENRES = "genre/movie/list";
    public static final String TAG = "MainActivity";
    private static final int TIME_INTERVAL = 2000;

    List<Movie> movies;
    Map<Integer, String> genres;
    MovieAdapter movieAdapter;

    private long mBackPressed;

    /** press back twice to exit feature. */
    @Override
    public void onBackPressed() {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), getString(R.string.exit_press_back_twice_message), Toast.LENGTH_SHORT).show();
        }
        mBackPressed = System.currentTimeMillis();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // set the action bar title
        setTitle(getString(R.string.now_playing));

        RecyclerView rvMovies = findViewById(R.id.rvMovies);

        movies = new ArrayList<>();
        genres = new HashMap<>();
        movieAdapter = new MovieAdapter(this, movies, genres);
        rvMovies.setAdapter(movieAdapter);
        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        AsyncHttpClient client = new AsyncHttpClient();

        client.get(URL_PREFIX + NOW_PLAYING + "?api_key=" + getString(R.string.moviedb_api_key), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    final JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results: " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Movies: " + movies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit Json Exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });

        client.get(URL_PREFIX + MOVIE_GENRES + "?api_key=" + getString(R.string.moviedb_api_key), new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    final JSONArray results = jsonObject.getJSONArray("genres");
                    Log.i(TAG, "Results: " + results.toString());
                    genres.putAll(Movie.genresFromJsonArray(results));
                    movieAdapter.notifyDataSetChanged();
                    Log.i(TAG, "Genres: " + genres.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit Json Exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure");
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu_now_playing
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_now_playing, menu);
        return true;
    }

    public void closeApp(MenuItem item) {
        super.onBackPressed();
    }
}