package com.clicsixdev.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>>{

    private static String TAG = MainActivity.class.getSimpleName();
    private static final String POP = "pop";
    private static final String TOP = "top";
    private static String currentType;

    private static final int MOVIE_SEARCH_LOADER = 11;


    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private ProgressBar mProgressBar;
    private TextView mErrorMsgDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_movie);

        mErrorMsgDisplay = (TextView) findViewById(R.id.error_msg_display);

        mProgressBar = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        GridLayoutManager layoutManager = new GridLayoutManager(this,2);

        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this);

        mRecyclerView.setAdapter(mMovieAdapter);

        Bundle typeBundle = new Bundle();

        typeBundle.putString("type_extra" , POP);

        currentType = POP;

        Log.i("aravind","In onCreate");

        getSupportLoaderManager().initLoader(MOVIE_SEARCH_LOADER,typeBundle,this);


    }

    @Override
    public void onClick(Movie clickedMovie) {
        Context context = this;
        Class destinationClass = DetailActivity.class;
        Intent intentToStartDetailActivity = new Intent(this,destinationClass);
        intentToStartDetailActivity.putExtra("Movie" , clickedMovie );
        startActivity(intentToStartDetailActivity);
    }

    private void loadMovieData(String type){
        if(isOnline() == true) {
            showMovieDataView();

            Bundle typeBundle = new Bundle();

            typeBundle.putString("type_extra" , type);

            LoaderManager loaderManager = getSupportLoaderManager();
            Loader<List<Movie>> movieSearchLoader = loaderManager.getLoader(MOVIE_SEARCH_LOADER);

            if (movieSearchLoader == null){
            loaderManager.initLoader(MOVIE_SEARCH_LOADER,typeBundle,this);
            }
            else {
                loaderManager.restartLoader(MOVIE_SEARCH_LOADER,typeBundle,this);

            }
        }
        else showErrorMsgView();
    }

    private void showMovieDataView(){
        mErrorMsgDisplay.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }


    private void showErrorMsgView(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMsgDisplay.setVisibility(View.VISIBLE);
    }


    @SuppressLint("StaticFieldLeak")
    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int id, @Nullable final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mList;

            @Override
            protected void onStartLoading() {

                Log.i("aravind","onStartLoading");

                if (args == null) {
                    return;
                }
                mProgressBar.setVisibility(View.VISIBLE);
                if (mList != null){
                    Log.i("aravind","In Caching Area");
                    deliverResult(mList);
                }
                else {
                    forceLoad();
                }

            }

            @Nullable
            @Override
            public List<Movie> loadInBackground() {
                Log.i("aravind","loadInBackground");

                   String searchType = args.getString("type_extra");

                    if (searchType == null || TextUtils.isEmpty(searchType)) {
                        return null;
                    }

                    URL movieRequestUrl = NetworkUtils.buildUrl(searchType);
                    try {
                        String movieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                       List<Movie>  movieList = NetworkUtils.getMoviesFromJson(MainActivity.this, movieResponse);

                        return movieList;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }


            }

            @Override
            public void deliverResult(@Nullable List<Movie> data) {

                Log.i("aravind","in Deliver Result");

                mList = data;
                super.deliverResult(data);

            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> data) {

        Log.i("aravind","onLoadFinished");

        mProgressBar.setVisibility(View.INVISIBLE);

//            Log.i("size of the list", movies.size()+"");

        if (data != null) {
            showMovieDataView();
            mMovieAdapter.setMovieData(data);
        }
        else
        {showErrorMsgView();}
    }



    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.master, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_popular)
        {
            mMovieAdapter.setMovieData(null);
            loadMovieData(POP);
            currentType = POP;
            return true;

        }

        if (id == R.id.action_top){
            mMovieAdapter.setMovieData(null);
            loadMovieData(TOP);
            currentType = TOP;
            return true;
        }

        if (id == R.id.action_refresh){
            mMovieAdapter.setMovieData(null);
            loadMovieData(currentType);
            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
