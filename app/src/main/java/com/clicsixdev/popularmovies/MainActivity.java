package com.clicsixdev.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler{

    private static String TAG = MainActivity.class.getSimpleName();
    private static final String POP = "pop";
    private static final String TOP = "top";


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

        loadMovieData(POP);



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


            new FetchMovieTask().execute(type);
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


    public class FetchMovieTask extends AsyncTask<String,Void,List<Movie>>{
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected List<Movie> doInBackground(String... params) {
            if (params.length == 0){
                return null;
            }

            String type = params[0];
            Log.i(TAG,params[0]);

            URL movieRequestUrl = NetworkUtils.buildUrl(type);
            try {
                String movieResponse = NetworkUtils.getResponseFromHttpUrl(movieRequestUrl);

                List<Movie> movieList = NetworkUtils.getMoviesFromJson(MainActivity.this,movieResponse);

                return movieList;
            }
            catch (Exception e){
                e.printStackTrace();
                return null;
            }

        }

        @Override
        protected void onPostExecute(List<Movie> movies) {
            mProgressBar.setVisibility(View.INVISIBLE);

//            Log.i("size of the list", movies.size()+"");

            if (movies != null) {
                showMovieDataView();
                mMovieAdapter.setMovieData(movies);
            }
            else
            {showErrorMsgView();}
        }
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
            return true;

        }

        if (id == R.id.action_top){
            mMovieAdapter.setMovieData(null);
            loadMovieData(TOP);
            return true;
        }

        if (id == R.id.action_refresh){
            mMovieAdapter.setMovieData(null);
            loadMovieData(POP);
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
