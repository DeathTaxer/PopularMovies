package com.clicsixdev.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

public class MovieListContract {

    public static final String AUTHORITY = "com.clicsixdev.popularmovies";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieListEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build();


        public static final String TABLE_NAME = "movielist";
        public static final String COLUMN_MOVIE_ID = "movieId";
        }
}
