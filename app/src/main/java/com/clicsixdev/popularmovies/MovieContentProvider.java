package com.clicsixdev.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.IntentFilter;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.clicsixdev.popularmovies.MovieListContract.MovieListEntry;

public class MovieContentProvider extends ContentProvider{

    private MovieListDbHelper dbHelper;

    public static final int MOVIESLIST = 100;
    public static final int MOVIE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();



    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //Uri for list of Movies
        uriMatcher.addURI(MovieListContract.AUTHORITY,MovieListContract.PATH_MOVIES,MOVIESLIST);
        //Uri for single Movie
        uriMatcher.addURI(MovieListContract.AUTHORITY,MovieListContract.PATH_MOVIES + "/#",MOVIE_WITH_ID);

        return uriMatcher;
    }


    @Override
    public boolean onCreate() {
        Context context = getContext();
        dbHelper = new MovieListDbHelper(context);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = dbHelper.getReadableDatabase();

        int match = sUriMatcher.match(uri);

        Cursor retCursor;

        switch (match){

            case MOVIESLIST:
                retCursor = db.query(MovieListEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case MOVIE_WITH_ID:

                String movieId = uri.getPathSegments().get(1);
                retCursor = db.query(MovieListEntry.TABLE_NAME,
                        projection,
                        MovieListEntry.COLUMN_MOVIE_ID+"=?",
                        new String[]{movieId},
                        null,
                        null,
                        sortOrder);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        int match = sUriMatcher.match(uri);

        Uri returnUri;

        switch (match){
            case MOVIESLIST:
                long id = db.insert(MovieListEntry.TABLE_NAME,null,contentValues);
                if (id > 0){
                    returnUri = ContentUris.withAppendedId(MovieListEntry.CONTENT_URI,id);
                }
                else
                    throw new android.database.SQLException("Failed to insert the row" + uri);
                break;

                default:
                    throw new UnsupportedOperationException("Unknown uri" + uri);
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return returnUri;



    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {

       final SQLiteDatabase db = dbHelper.getWritableDatabase();

       int match = sUriMatcher.match(uri);

       int moviesDeleted;

       switch (match) {
           case MOVIE_WITH_ID:

               String movieId = uri.getPathSegments().get(1);

               moviesDeleted = db.delete(MovieListEntry.TABLE_NAME,MovieListEntry.COLUMN_MOVIE_ID+"=?",new String[]{movieId});
               break;
           default:
               throw new UnsupportedOperationException("Unknown uri: " + uri);
       }
       if (moviesDeleted != 0){
           getContext().getContentResolver().notifyChange(uri,null);
       }

       return moviesDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
