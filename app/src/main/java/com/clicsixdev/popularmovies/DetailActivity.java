package com.clicsixdev.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    private TextView mTitleDisplay;
    private TextView mRatingDisplay;
    private TextView mDateDisplay;
    private TextView mDescDisplay;
    private ImageView mPosterDisplay;
    private Button mFavButton;
    private Movie movie;

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

        Intent intentThatStartedThisActivity = getIntent();

        if(intentThatStartedThisActivity != null){
                Bundle data = intentThatStartedThisActivity.getExtras();
                 movie = (Movie) data.getParcelable("Movie");


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

            }
        });
    }
}
