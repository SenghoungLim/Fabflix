package edu.uci.ics.fabflixmobile.ui.movielist;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;
public class SingleMovieActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_movie);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        Log.d("SingleMovieActivity","Single Movie Activity Started");
        // Retrieve movie details from Intent
        String title = getIntent().getStringExtra("movieTitle");
        String year = getIntent().getStringExtra("movieYear");
        String director = getIntent().getStringExtra("movieDirector");
        String stars = getIntent().getStringExtra("movieStars");
        String genres = getIntent().getStringExtra("movieGenres");

        // Populate the UI with movie details
        TextView movieTitleTextView = findViewById(R.id.movieTitle);
        movieTitleTextView.setText(title);

        TextView movieYearTextView = findViewById(R.id.movieYear);
        movieYearTextView.setText("Year: " + year);

        TextView movieDirectorTextView = findViewById(R.id.movieDirector);
        movieDirectorTextView.setText("Director: " + director);

        TextView movieStarsTextView = findViewById(R.id.movieStars);
        movieStarsTextView.setText("Stars: " + stars);

        TextView movieGenresTextView = findViewById(R.id.movieGenres);
        movieGenresTextView.setText("Genres: " + genres);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
