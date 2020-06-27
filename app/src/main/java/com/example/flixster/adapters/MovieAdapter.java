package com.example.flixster.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.bumptech.glide.request.RequestOptions;
import com.example.flixster.DetailActivity;
import com.example.flixster.R;
import com.example.flixster.databinding.ItemMovieBinding;
import com.example.flixster.models.Movie;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;


public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private static final int RADIUS = 20; // corner radius, higher value = more rounded
    private static final int MARGIN = 0; // crop margin, set to 0 for corners with no crop
    private static final String TAG = "MovieAdapter";
    public static final String GENRE = "item_genre";

    Context context;
    List<Movie> movies;
    Map<Integer, String> genres;
    ItemMovieBinding binding;

    public MovieAdapter(Context context, List<Movie> movies, Map<Integer, String> genres) {
        this.context = context;
        this.movies = movies;
        this.genres =  genres;

    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater =
                LayoutInflater.from(parent.getContext());
        binding =
                ItemMovieBinding.inflate(layoutInflater, parent, false);
        return new MovieViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView titleTextView;
        TextView overviewTextView;
        ImageView posterImageView;

        public MovieViewHolder(@NonNull ItemMovieBinding binding) {
            super(binding.getRoot());
            titleTextView = binding.titleTextView;
            overviewTextView = binding.overviewTextView;
            posterImageView = binding.posterImageView;

            itemView.setOnClickListener(this);
        }

        public void bind(Movie movie) {
            titleTextView.setText(movie.getTitle());
            overviewTextView.setText(movie.getOverview());
            String imageUrl;
            int placeHolderId;
            if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
                imageUrl = movie.getBackdropPath();
                placeHolderId = R.drawable.flicks_backdrop_placeholder;
            } else {
                imageUrl = movie.getPosterPath();
                placeHolderId = R.drawable.flicks_movie_placeholder;
            }
            loadRoundImage(posterImageView, imageUrl, placeHolderId);
        }

        private String getGenres(Movie movie){
            List<String> g = new ArrayList<>();
            for (Integer genreId : movie.getGenres()) {
                String genre = genres.get(genreId);
                if (genre != null)
                    g.add(genre);
            }
            return TextUtils.join("\n", g);
        }


        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            if (position != RecyclerView.NO_POSITION) {
                Movie movie = movies.get(position);
                Intent intent = new Intent(context, DetailActivity.class);
                intent.putExtra(Movie.class.getSimpleName(), Parcels.wrap(movie));
                intent.putExtra(GENRE, getGenres(movie));
                context.startActivity(intent);
            }
        }

        private boolean isValid(Context context) {
            if (context instanceof AppCompatActivity) {
                return !((AppCompatActivity) context).isDestroyed();
            }
            return true;
        }

        /**
         * Rounds the margins of an ImageView and sends the modified ImageView to the display
         */

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
                            .placeholder(drawableId)
                            .transform(new RoundedCornersTransformation(RADIUS, MARGIN)))
                    .into(imageView);
        }
    }
}

