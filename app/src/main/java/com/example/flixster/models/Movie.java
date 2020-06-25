package com.example.flixster.models;

import android.content.Intent;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.parceler.Parcel;

@Parcel
public class Movie {
    private String mPosterPath;
    private String mTitle;
    private String mOverview;
    private String mBackdropPath;
    private Double mVoteAverage;
    private Integer[] mGenres;
    private boolean isAdult;

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public void setReleaseDate(String mReleaseDate) {
        this.mReleaseDate = mReleaseDate;
    }

    private String mReleaseDate;
    private Integer voteCount;
    private Integer popularity;

    public void setGenres(Integer[] genres) {
        this.mGenres = genres;
    }

    public Integer[] getGenres() {
        return mGenres;
    }


    public Movie() {}

    public Movie(JSONObject jsonObject) throws JSONException {
        mPosterPath = jsonObject.getString("poster_path");
        mBackdropPath = jsonObject.getString("backdrop_path");
        mTitle = jsonObject.getString("title");
        mOverview = jsonObject.getString("overview");
        mVoteAverage = jsonObject.getDouble("vote_average");
        mGenres = toIntArray(jsonObject.getJSONArray("genre_ids"));
        mReleaseDate = jsonObject.getString("release_date");
    }

    private static Integer[] toIntArray(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            Log.e("Movie", "null pointer received instead of json array");
            return new Integer[1];
        }
        Integer[] numbers = new Integer[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); ++i) {
            numbers[i] = jsonArray.getInt(i);
        }
        return numbers;
    }

    public static List<Movie> fromJsonArray(final JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }


    public static Map<Integer, String> genresFromJsonArray(final JSONArray genresJsonArray) throws JSONException {
        Map<Integer, String> genres = new HashMap<>();
        for (int i = 0; i < genresJsonArray.length(); i++) {
            JSONObject jsonObject = genresJsonArray.getJSONObject(i);
            genres.put(jsonObject.getInt("id"), jsonObject.getString("name"));
        }
        return genres;
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", mPosterPath);
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w342/%s", mBackdropPath);
    }

    public void setPosterPath(String posterPath) {
        this.mPosterPath = posterPath;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public Double getVoteAverage() {
        return mVoteAverage;
    }

    public String getOverview() {
        return mOverview;
    }

    public void setOverview(String overview) {
        this.mOverview = overview;
    }
}
