package com.clicsixdev.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

public class Movie implements Parcelable{

    private String id;
    private String title;
    private String posterUrl;
    private String description;
    private String userRating;
    private String releaseDate;


    public Movie(String id,String title, String posterUrl, String description, String userRating, String releaseDate) {
        this.title = title;
        this.posterUrl = posterUrl;
        this.description = description;
        this.userRating = userRating;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    private Movie(Parcel in){
        id = in.readString();
        title = in.readString();
        posterUrl = in.readString();
        description = in.readString();
        userRating = in.readString();
        releaseDate = in.readString();

    }

    public String getTitle() {
        return title;
    }

    public String getPosterUrl() {
        return posterUrl;
    }

    public String getDescription() {
        return description;
    }

    public String getUserRating() {
        return userRating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public String getId() {
        return id;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(posterUrl);
        parcel.writeString(description);
        parcel.writeString(userRating);
        parcel.writeString(releaseDate);


    }

    public static final Parcelable.Creator<Movie>  CREATOR = new Parcelable.Creator<Movie>(){
        @Override
        public Movie createFromParcel(Parcel parcel) {
            return new Movie(parcel);
        }

        @Override
        public Movie[] newArray(int i) {
            return new Movie[i];
        }
    };

    @Override
    public String toString() {
        return "Movie:" + title +"\n"
                + "PosterUrl:" + posterUrl + "\n"
                + "description:" + description + "\n"
                + "userRating:" + userRating +"\n"
                + "releaseDate:" + releaseDate + "\n";
    }
}
