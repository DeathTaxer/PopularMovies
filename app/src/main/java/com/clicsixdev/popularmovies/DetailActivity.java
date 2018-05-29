package com.clicsixdev.popularmovies;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private TextView mTitleDisplay;
    private TextView mRatingDisplay;
    private TextView mDateDisplay;
    private TextView mDescDisplay;
    private ImageView mPosterDisplay;
    private Button mFavButton;
    private Movie movie;
    private String videoResponse;
    private String reviewResponse;
    private LinearLayout trailerLayout;
    private TextView reviewDisplay;
    private TextView reviewTitle;
    private int fav = 0;

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mPosterDisplay = (ImageView) findViewById(R.id.detail_poster);
        mTitleDisplay = (TextView) findViewById(R.id.movie_title);
        mRatingDisplay = (TextView) findViewById(R.id.rating_display);
        mDateDisplay = (TextView) findViewById(R.id.date_display);
        mDescDisplay = (TextView) findViewById(R.id.desc_display);
        mFavButton = (Button) findViewById(R.id.fav_button);
        trailerLayout = (LinearLayout) findViewById(R.id.trailers_group);
        reviewDisplay = (TextView) findViewById(R.id.review_display);
        reviewTitle = (TextView) findViewById(R.id.review_title_view);

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity != null){
                Bundle data = intentThatStartedThisActivity.getExtras();
                 movie = (Movie) data.getParcelable("Movie");


        }


        Uri uri = MovieListContract.MovieListEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(movie.getId()).build();

        Cursor cursor = getContentResolver().query(uri,
                null,null,null,null);

        if (cursor.getCount() == 1) {
            mFavButton.setText("Remove as Favourite");
            fav = 1;
        }

        mTitleDisplay.setText(movie.getTitle());
        mRatingDisplay.setText(movie.getUserRating() + "/10");
        mDateDisplay.setText(movie.getReleaseDate());
        mDescDisplay.setText(movie.getDescription());
        Picasso.with(this)
                .load(movie.getPosterUrl())
                .into(mPosterDisplay);
        mFavButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentValues values = new ContentValues();
                values.put(MovieListContract.MovieListEntry.COLUMN_MOVIE_ID,movie.getId());
                values.put(MovieListContract.MovieListEntry.COLUMN_MOVIE_TITLE,movie.getTitle());


               if (fav == 1 ){

                   Uri uri = MovieListContract.MovieListEntry.CONTENT_URI;
                   uri = uri.buildUpon().appendPath(movie.getId()).build();

                   int moviesDeleted = getContentResolver().delete(uri,null,null);

                   if (moviesDeleted != 0 ) {

                       fav = 0;
                       Toast.makeText(getBaseContext(), "Removed from the favourites", Toast.LENGTH_SHORT).show();
                       mFavButton.setText(R.string.action_fav);
                   }


               }

               else {
                   Uri retUri = getContentResolver().insert(MovieListContract.MovieListEntry.CONTENT_URI,values);

                   if (retUri != null)
                       Toast.makeText(getBaseContext(),"Added to the favourites",Toast.LENGTH_SHORT).show();
                   mFavButton.setText("Remove as Favourite");
                   fav = 1;



               }





            }
        });


        new AsyncTask<String , Void , String>(){

            @Override
            protected String doInBackground(String... id) {
                String response = new String();
                try {
                     response = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildVideoUrl(id[0]));
                     Log.d("url:",NetworkUtils.buildVideoUrl(id[0]).toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }


            @Override
            protected void onPostExecute(String s) {

                if (s != null){

                    try {
                        final List<String> trailers = NetworkUtils.getTrailerFromJson(DetailActivity.this,s);
                        Log.d("aravind",trailers.size()+"");
                        if (trailers.size() != 0){
                            for (int i = 0; i<trailers.size() ;i++){

                                LinearLayout.LayoutParams lparams = new LinearLayout.LayoutParams(
                                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                                Button tv = new Button(DetailActivity.this);

                                final Uri uri = Uri.parse(NetworkUtils.YT_URL+"/"+ trailers.get(i));

                                tv.setText("Trailer "+ (i+1));
                                float scale = getResources().getDisplayMetrics().density;
                                int dpAsPixels = (int) (8*scale + 0.5f);
                                tv.setPadding(dpAsPixels,dpAsPixels,dpAsPixels,dpAsPixels);

                                lparams.setMargins(0,dpAsPixels,0,0);
                                tv.setBackgroundColor(Color.parseColor("#229954"));

                                tv.setClickable(true);

                                tv.setFocusable(true);
                                tv.setLayoutParams(lparams);
                                tv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

                                tv.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {


                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                        if (intent.resolveActivity(getPackageManager()) != null) {
                                            startActivity(intent);
                                        }
                                    }


                                });


                                trailerLayout.addView(tv);

                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        }.execute(movie.getId());





        new AsyncTask<String , Void , String>(){

            @Override
            protected String doInBackground(String... id) {
                String response = new String();
                try {
                    response = NetworkUtils.getResponseFromHttpUrl(NetworkUtils.buildReviewUrl(id[0]));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return response;
            }


            @Override
            protected void onPostExecute(String s) {

                if (s != null){
                    try {
                        List<String> reviewList = NetworkUtils.getReviewFromJson(DetailActivity.this,s);
                        if (reviewList.size() != 0 ){
                            for (String review : reviewList) {
                                Log.d("review:",review);
                                reviewDisplay.append(review + "\n\n\n");
                            }
                        }

                        else {
                            reviewDisplay.setVisibility(View.INVISIBLE);
                            reviewTitle.setVisibility(View.INVISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        }.execute(movie.getId());





    }
}
