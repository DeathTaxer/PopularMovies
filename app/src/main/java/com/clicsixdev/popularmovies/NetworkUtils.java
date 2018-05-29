package com.clicsixdev.popularmovies;

import android.content.ContentValues;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class NetworkUtils {

    private static final String API_KEY = "";//Enter your api key here
    private static final String POP_URL = "https://api.themoviedb.org/3/movie/popular";
    private static final String TOP_URL = "https://api.themoviedb.org/3/movie/top_rated";
    private static final String MOVIE_URL = "https://api.themoviedb.org/3/movie";
    public static final String YT_URL = "https://www.youtube.com/watch";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";

    public static URL buildUrl(String type) {

        String typeUrl;

        if (type.equals(new String("pop")))
            typeUrl = POP_URL;
        else
            typeUrl = TOP_URL;


        Uri builtUri = Uri.parse(typeUrl).buildUpon()
                .appendQueryParameter("api_key", API_KEY)
                .build();
        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;


    }

    public static URL buildMovieUrl(String id) {

        Uri builtUri = Uri.parse(MOVIE_URL).buildUpon().appendPath(id)
                .appendQueryParameter("api_key", API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    public static URL buildVideoUrl(String id) {
        Uri builtUri = Uri.parse(MOVIE_URL).buildUpon().appendPath(id)
                .appendPath("videos")
                .appendQueryParameter("api_key", API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }


    public static URL buildReviewUrl(String id) {
        Uri builtUri = Uri.parse(MOVIE_URL).buildUpon().appendPath(id)
                .appendPath("reviews")
                .appendQueryParameter("api_key", API_KEY)
                .build();

        URL url = null;

        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;

    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner sc = new Scanner(in);
            sc.useDelimiter("\\A");
            boolean hasInput = sc.hasNext();
            if (hasInput) {
                return sc.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<Movie> getMoviesFromJson(Context context, String movieJsonStr) throws JSONException {
        String id, title, posterUrl, description, userRating, releaseDate;
        List<Movie> movieList = new ArrayList<Movie>();
        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray resultsArray = movieJson.getJSONArray("results");

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject movieDetail = resultsArray.getJSONObject(i);

            title = movieDetail.getString("original_title");

            posterUrl = IMAGE_BASE_URL + movieDetail.getString("poster_path");

            //Log.i("PosterUrl" , posterUrl);

            description = movieDetail.getString("overview");

            userRating = movieDetail.getString("vote_average");

            id = movieDetail.getString("id");

            releaseDate = movieDetail.getString("release_date");

            Log.i(title, releaseDate);

            movieList.add(new Movie(id, title, posterUrl, description, userRating, releaseDate));


        }

        return movieList;


    }

    public static Movie getMovieFromJson(Context context, String movieJsonStr) throws JSONException {

        String id, title, posterUrl, description, userRating, releaseDate;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        title = movieJson.getString("original_title");

        posterUrl = IMAGE_BASE_URL + movieJson.getString("poster_path");

        description = movieJson.getString("overview");

        userRating = movieJson.getString("vote_average");

        id = movieJson.getString("id");

        releaseDate = movieJson.getString("release_date");

        return new Movie(id, title, posterUrl, description, userRating, releaseDate);

    }


    public static List<String> getTrailerFromJson(Context context, String videoJsonStr) throws JSONException {
        List<String> trailerList = new ArrayList<String>();
        JSONObject videoJson = new JSONObject(videoJsonStr);

        JSONArray resultsArray = videoJson.getJSONArray("results");

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject video = resultsArray.getJSONObject(i);

            if (video.getString("type").equals("Trailer")) {

                trailerList.add(video.getString("key"));
            }


        }

        return trailerList;

    }


    public static List<String> getReviewFromJson(Context context, String reviewJsonStr) throws JSONException {
        List<String> reviewList = new ArrayList<String>();
        JSONObject reviewJson = new JSONObject(reviewJsonStr);

        JSONArray resultsArray = reviewJson.getJSONArray("results");

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject review = resultsArray.getJSONObject(i);

            reviewList.add(review.getString("author") + ":\n\n" + review.getString("content"));


        }

        return reviewList;


    }

}

