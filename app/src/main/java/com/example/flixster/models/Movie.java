package com.example.flixster.models;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Date;

import org.parceler.Parcel;

@Parcel
public class Movie {
    private final SimpleDateFormat mDateParser = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private String mPosterPath;
    private String mTitle;
    private String mOverview;
    private String mBackdropPath;
    private Double mVoteAverage;
    private Integer[] mGenres;
    private Date mReleaseDate;
    private Integer mMovieId;
    private Double mPopularity;
    private Integer mVoteCount;

    public Double getPopularity() {
        return mPopularity;
    }

    public void setPopularity(Double mPopularity) {
        this.mPopularity = mPopularity;
    }

    public Integer getVoteCount() {
        return mVoteCount;
    }

    public void setVoteCount(Integer mVoteCount) {
        this.mVoteCount = mVoteCount;
    }

    public Integer getMovieId() {
        return mMovieId;
    }

    public void setMovieId(Integer movieId) {
        this.mMovieId = movieId;
    }

    public String getReleaseDate() {
        LocalDate date = mReleaseDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(date);
    }

    public void setGenres(Integer[] genres) {
        this.mGenres = genres;
    }

    public Integer[] getGenres() {
        return mGenres;
    }

    public Movie() {}

    public Movie(JSONObject jsonObject) throws JSONException, ParseException {
        mPosterPath = jsonObject.getString("poster_path");
        mBackdropPath = jsonObject.getString("backdrop_path");
        mTitle = jsonObject.getString("title");
        mOverview = jsonObject.getString("overview");
        mVoteAverage = jsonObject.getDouble("vote_average");
        mGenres = toIntArray(jsonObject.getJSONArray("genre_ids"));
        mReleaseDate = mDateParser.parse(jsonObject.getString("release_date"));
        mMovieId = jsonObject.getInt("id");
        mPopularity = jsonObject.getDouble("popularity");
        mVoteCount = jsonObject.getInt("vote_count");
    }

    private static Integer[] toIntArray(JSONArray jsonArray) throws JSONException {
        if (jsonArray == null) {
            Log.e("Movie", "null pointer received instead of json array");
            return new Integer[1];
        }
        //
        Integer[] numbers = new Integer[jsonArray.length()];

        for (int i = 0; i < jsonArray.length(); ++i) {
            numbers[i] = jsonArray.getInt(i);
        }
        return numbers;
    }

    public static List<Movie> fromJsonArray(final JSONArray movieJsonArray) throws JSONException, ParseException {
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
