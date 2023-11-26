package edu.uci.ics.fabflixmobile.data.model;

/**
 * Movie class that captures movie information for movies retrieved from MovieListActivity
 */
public class Movie {
    private final String id;
    private final String title;
    private final short year;
    private final String director;
    private final String genre;
    private final String stars;
    private final String rating;



    public Movie(String id, String title, short year, String director, String genre, String stars, String rating) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genre = genre;
        this.stars = stars;
        this.rating = rating;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return title;
    }

    public short getYear() {
        return year;
    }
    public String getDirector() {
        return director;
    }
    public String getGenre() {
        return genre;
    }
    public String getStars() {
        return stars;
    }
    public String getRating() {
        return rating;
    }
}