package com.clicsixdev.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieListDbHelper extends SQLiteOpenHelper{

    public static final String DATABASE_NAME = "moviesDb.db";

    private static final int VERSION = 1;

    MovieListDbHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);

    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String CREATE_TABLE = "CREATE TABLE " + MovieListContract.MovieListEntry.TABLE_NAME + " (" +
                MovieListContract.MovieListEntry._ID + " INTERGER PRIMARY KEY, " +
                MovieListContract.MovieListEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                MovieListContract.MovieListEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL);";

            sqLiteDatabase.execSQL(CREATE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieListContract.MovieListEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);



    }
}
