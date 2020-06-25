package com.example.flixster;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import okhttp3.Headers;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    private static final String GENRE = "item_genre";
    public static final String VIDEO_ID = "item_youtube_video_id";
    Movie movie;
    String movieGenres;

    TextView tvDTitle;
    TextView tvDOverview;
    TextView tvDate;
    TextView tvGenres;
    RatingBar rbVoteAverage;
    ImageView ivThumb;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
        assert actionBar != null;
        actionBar.hide();
        context = this;

        // client to request for videos
        final AsyncHttpClient client = new AsyncHttpClient();

        tvDTitle = findViewById(R.id.tvDTitle);
        tvDOverview = findViewById(R.id.tvDOverview);
        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        ivThumb = findViewById(R.id.ivThumb);
        tvDate = findViewById(R.id.tvDate);
        tvGenres = findViewById(R.id.tvGenres);

        ivThumb.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                String url = MainActivity.URL_PREFIX + "movie/" +
                        movie.getMovieId() + "/videos?api_key=" +
                        getString(R.string.moviedb_api_key);;
                client.get(url, null,
                        new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Headers headers, JSON json) {
                                try {
                                    ArrayList<String[]> keys = getVideoKeys(json.jsonObject.getJSONArray("results"));
                                    Intent intent = new Intent(context, MovieTrailerActivity.class);
                                    String[] data = keys.get(0);
                                    if (!data[1].equals("YouTube")) {
                                        Log.e(TAG, "Only Youtube videos supported");
                                        Toast.makeText(getApplicationContext(), "Only Youtube videos supported", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        assert data[2] != null;
                                        Toast.makeText(getApplicationContext(), "Playing " + data[2], Toast.LENGTH_SHORT).show();
                                        intent.putExtra(VIDEO_ID, data[0]);
                                        startActivity(intent);

                                    }
                                } catch (JSONException e) {
                                    Log.e(TAG, "JSON Parsing Exception", e);
                                }
                                catch (IndexOutOfBoundsException e) {
                                    String msg = "No results found";
                                    Log.i(TAG, msg);
                                    Toast.makeText(getApplicationContext(),
                                            msg,
                                            Toast.LENGTH_SHORT).show();
                                    Log.e(TAG, msg);
                                }
                            }

                            @Override
                            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                                String msg = "Could not access the trailer" + movie.getTitle();
                                Toast.makeText(getApplicationContext(),
                                        msg,
                                        Toast.LENGTH_SHORT).show();
                                Log.e(TAG, msg);
                            }
                        });
                }
        });



        movie = Parcels.unwrap(getIntent().getParcelableExtra(Movie.class.getSimpleName()));
        movieGenres = getIntent().getStringExtra(MovieAdapter.GENRE);
        assert movie != null;
        Log.d("MovieDetailsActivity", String.format("Showing details for '%s'", movie.getTitle()));

        tvDTitle.setText(movie.getTitle());
        tvDOverview.setText(movie.getOverview());
        tvDate.setText(movie.getReleaseDate());
        tvGenres.setText(movieGenres);
        String imageUrl;
        int placeHolderId;
        imageUrl = movie.getPosterPath();
        placeHolderId = R.drawable.flicks_movie_placeholder;
        loadRoundImage(ivThumb, imageUrl, placeHolderId);


        // vote average is 0..10, convert to 0..5 by dividing by 2
        float voteAverage = movie.getVoteAverage().floatValue();
        rbVoteAverage.setRating(voteAverage > 0 ? voteAverage / 2.0f : voteAverage);
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


    private boolean isValid(Context context) {
        if (context instanceof AppCompatActivity) {
            return !((AppCompatActivity) context).isDestroyed();
        }
        return true;
    }

    public void loadRoundImage(ImageView imageView, String url, int drawableId) {
        if (imageView == null) {
            Log.e(TAG, "loadRoundImage() -> imageView is null");
            return;
        }

        Context context = imageView.getContext();
        if (!isValid(context)) {
            Log.e(TAG, "Invalid context");
            return;
        }

        Glide.with(context)
                .load(url)
                .fitCenter()
                .apply(new RequestOptions()
                        .placeholder(drawableId))
                .into(imageView);
    }
}