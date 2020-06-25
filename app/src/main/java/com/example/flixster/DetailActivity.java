package com.example.flixster;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.flixster.adapters.MovieAdapter;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.Objects;

public class DetailActivity extends AppCompatActivity {
    private static final String TAG = "DetailActivity";
    public static final String GENRE = "item_genre";
    Movie movie;
    String movieGenres;

    TextView tvDTitle;
    TextView tvDOverview;
    TextView tvDate;
    TextView tvGenres;
    RatingBar rbVoteAverage;
    ImageView ivThumb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        ActionBar actionBar = getSupportActionBar(); // or getActionBar();
//        Objects.requireNonNull(getSupportActionBar()).setTitle("My new title"); // set the top title
        assert actionBar != null;
        actionBar.hide();

        tvDTitle = findViewById(R.id.tvDTitle);
        tvDOverview = findViewById(R.id.tvDOverview);
        rbVoteAverage = findViewById(R.id.rbVoteAverage);
        ivThumb = findViewById(R.id.ivThumb);
        tvDate = findViewById(R.id.tvDate);
        tvGenres = findViewById(R.id.tvGenres);


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