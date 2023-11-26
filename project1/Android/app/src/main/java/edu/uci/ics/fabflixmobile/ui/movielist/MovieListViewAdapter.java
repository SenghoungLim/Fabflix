package edu.uci.ics.fabflixmobile.ui.movielist;

import edu.uci.ics.fabflixmobile.R;
import edu.uci.ics.fabflixmobile.data.model.Movie;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import edu.uci.ics.fabflixmobile.data.Constants;

//create java object
public class MovieListViewAdapter extends ArrayAdapter<Movie> {
    private final ArrayList<Movie> movies;
    // View lookup cache
    private static class ViewHolder {
        TextView title;
        TextView subtitle;
        TextView director;
        TextView genre;
        TextView stars;
        TextView rating;
    }

    public MovieListViewAdapter(Context context, ArrayList<Movie> movies) {
        super(context, R.layout.movielist_row, movies);
        this.movies = movies;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the movie item for this position
        Movie movie = movies.get(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag
        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext()); //create new row
            convertView = inflater.inflate(R.layout.movielist_row, parent, false);
            viewHolder.title = convertView.findViewById(R.id.title);
            viewHolder.subtitle = convertView.findViewById(R.id.subtitle);
            viewHolder.director = convertView.findViewById(R.id.director);
            viewHolder.genre = convertView.findViewById(R.id.genres);
            viewHolder.stars = convertView.findViewById(R.id.stars);
            viewHolder.rating = convertView.findViewById(R.id.rating);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Populate the data from the data object via the viewHolder object
        // into the template view.
        viewHolder.title.setText(movie.getName());
        viewHolder.subtitle.setText("Year:" + movie.getYear() + "");
        viewHolder.director.setText("Director: " + movie.getDirector() + "");
        viewHolder.genre.setText("Genres: " + movie.getGenre() + "");
        viewHolder.stars.setText("Stars: " + movie.getStars() + "");
        viewHolder.rating.setText("Rating: " + movie.getRating() + "");
        // Return the completed view to render on screen
        return convertView;
    }
}