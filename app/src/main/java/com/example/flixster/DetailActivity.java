package com.example.flixster;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.databinding.ActivityDetailBinding;
import com.example.flixster.models.Movie;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import okhttp3.Headers;

public class DetailActivity extends YouTubeBaseActivity {
    private static final String TAG = "DetailActivity";
    private static final int RECOVERY_REQUEST = 1;
    Movie movie;
    String movieGenres;

    TextView overviewTextView, voteCountTextView, dateTextView, genreTextView, popularityTextView;
    RatingBar votesAverageRatingBar;
    Context context;
    AsyncHttpClient client;
    YouTubePlayerView youTubeView;
    ActivityDetailBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        context = this;
        // client to request for videos
        client = new AsyncHttpClient();
        bindViews();
        getMovieAndGenresFromIntent();
        fillYoutubeView();

        voteCountTextView.setText(getString(R.string.votes_count, movie.getVoteCount()));
        popularityTextView.setText(getString(R.string.popularity_i, movie.getPopularity()));

        overviewTextView.setText(movie.getOverview());
        dateTextView.setText(movie.getReleaseDate());
        genreTextView.setText(movieGenres);
        // vote average is 0 .. 10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        votesAverageRatingBar.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
    }

    private void getMovieAndGenresFromIntent() {
        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        movieGenres = getIntent().getStringExtra(MovieAdapter.GENRE);
        assert movie != null;
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));
    }

    private void fillYoutubeView() {
        String url = MainActivity.URL_PREFIX + "movie/" +
                movie.getMovieId() + "/videos?api_key=" +
                getString(R.string.moviedb_api_key);


        client.get(url, null,
                new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        try {
                            ArrayList<String[]> keys = getVideoKeys(json.jsonObject.getJSONArray("results"));
                            ArrayList<String[]> filtered = getVideosFromSite(keys, "YouTube");
                            final String[] data = filtered.get(0);
                            assert data[2] != null;
                            Log.i(TAG, "YouTube video URL acquired");

                            youTubeView.initialize(getString(R.string.youtube_api_key), new YouTubePlayer.OnInitializedListener() {
                                @Override
                                public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                                    if (!b) {
                                        youTubePlayer.cueVideo(data[0]);
                                    }
                                }

                                @Override
                                public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                                    if (youTubeInitializationResult.isUserRecoverableError()) {
                                        youTubeInitializationResult.getErrorDialog((Activity) context, RECOVERY_REQUEST).show();
                                    }
                                    else {
                                        Log.e(this.getClass().getSimpleName(), getString(R.string.player_error));
                                        String error = String.format(getString(R.string.player_error), youTubeInitializationResult.toString());
                                        showToast(error);
                                    }
                                }
                            });
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON Parsing Exception", e);
                        }
                        catch (IndexOutOfBoundsException e) {
                            String msg = "No results found";
                            Log.i(TAG, msg);
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.d(TAG, "Could not access the trailer" + movie.getTitle());
                    }
                });

    }

    private void bindViews() {
        overviewTextView = binding.overviewTextView;
        votesAverageRatingBar = binding.votesAverageRatingBar;
        dateTextView = binding.dateTextView;
        genreTextView = binding.genreTextView;
        youTubeView = binding.youtubeView;
        voteCountTextView = binding.votesCountTextView;
        popularityTextView = binding.popularityTextView;
    }


    private ArrayList<String[]> getVideosFromSite(ArrayList<String[]> allVideos, String site) {
        ArrayList<String[]> siteVideos = new ArrayList<>();
        for (String[] videoStruct: allVideos) {
            if (videoStruct[1].equals(site)) {
                siteVideos.add(videoStruct);
            }
        }
        return siteVideos;
    }

    private ArrayList<String[]> getVideoKeys(JSONArray videos) throws JSONException {
        ArrayList<String[]> keys = new ArrayList<>();
        for (int i = 0; i < videos.length(); i++) {
            JSONObject video = videos.getJSONObject(i);
            keys.add(new String[] {video.getString("key"), video.getString("site"),
                    video.getString("type")});
        }
        return keys;
    }

    private void showToast(String msg) {
        Toast.makeText(getApplicationContext(),
                msg,
                Toast.LENGTH_SHORT).show();
        Log.e(TAG, msg);
    }
}