package com.clicsixdev.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private List<Movie> mMovieData;

    private final MovieAdapterOnClickHandler mClickHandler;


    public interface MovieAdapterOnClickHandler {
        void onClick(Movie clickedMovie);

    }


    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){

        mClickHandler = clickHandler;

    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public final ImageView mMovieview;

        public MovieViewHolder(View view){
            super(view);
            mMovieview = (ImageView) view.findViewById(R.id.movie_poster);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie clickedMovie = mMovieData.get(adapterPosition);
            mClickHandler.onClick(clickedMovie);
        }
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem,parent,false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        Movie currentMovie = mMovieData.get(position);

        Log.i("posterurl" , currentMovie.getPosterUrl());

        Picasso.with(holder.itemView.getContext())
                .load(currentMovie.getPosterUrl())
                .into(holder.mMovieview);

    }

    @Override
    public int getItemCount() {
        if (mMovieData == null)
            return 0;
        else
            return mMovieData.size();
    }

    public void setMovieData(List<Movie> movieData){

        mMovieData = movieData;
        notifyDataSetChanged();


    }
}
