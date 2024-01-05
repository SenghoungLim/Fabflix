    package edu.uci.ics.fabflixmobile.ui.movielist;

    import android.annotation.SuppressLint;
    import android.content.Intent;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.MenuItem;
    import android.view.View;
    import android.widget.ArrayAdapter;
    import android.widget.ListView;
    import android.widget.Toast;
    import android.widget.SearchView;

    import androidx.appcompat.app.AppCompatActivity;

    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.StringRequest;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.Map;

    import edu.uci.ics.fabflixmobile.R;
    import edu.uci.ics.fabflixmobile.data.Constants;
    import edu.uci.ics.fabflixmobile.data.NetworkManager;
    import edu.uci.ics.fabflixmobile.data.model.Movie;

    public class MovieListActivity extends AppCompatActivity {
        Constants constants = new Constants();
        private String baseUrl = constants.getUrl();
        private int currentPage = 1;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_movielist);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            String searchInput = getIntent().getStringExtra("searchInput");
            Log.d("B4 fetching movies data! ", searchInput);
            fetchMoviesFromBackend(searchInput, currentPage);
            //Next and prev btns
            findViewById(R.id.nextButton).setOnClickListener(v -> {
                currentPage++;
                fetchMoviesFromBackend(searchInput, currentPage);
            });

            findViewById(R.id.prevButton).setOnClickListener(v -> {
                if (currentPage > 1) {
                    currentPage--;
                    fetchMoviesFromBackend(searchInput, currentPage);
                }
            });

        }

        @SuppressLint("SetTextI18n")
        public void fetchMoviesFromBackend(String query, int curPage) {
            //message.setText("Trying to login");
            // use the same network queue across our application
            final RequestQueue queue = NetworkManager.sharedManager(this).queue; //java request queue to fabflix
            // request type is POST
            final StringRequest searchRequest = new StringRequest(

                    Request.Method.GET,
                    baseUrl + "/api/fulltext?fulltext=" + query + "&page=" + curPage ,
                    response -> {
                        try {
                            Log.d("Movies response: ", String.valueOf(response));
                            JSONArray jsonArray = new JSONArray(response);
                            final ArrayList<Movie> movies = new ArrayList<>();
                            movies.clear();

                            // Add data to the movies list
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String id = jsonObject.getString("id");
                                String title = jsonObject.getString("title");
                                String year = jsonObject.getString("year");
                                String director = jsonObject.getString("director");
                                String genre = jsonObject.getString("genres");
                                String stars = jsonObject.getString("stars");
                                String rating = jsonObject.getString("rating");

                                // Create a Movie object
                                Movie movie = new Movie(id, title, Short.parseShort(year), director, genre, stars, rating);
                                movies.add(movie);
                            }
                            updateUI(movies);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("Catch error inside response of fetching! ", query);
                        }
                    },
                    error -> {
                        Log.d("login.error", error.toString());
                        Toast.makeText(getApplicationContext(), "Error fetching movies", Toast.LENGTH_SHORT).show();
                    }) {

            };
            queue.add(searchRequest);
        }

        private void updateUI(ArrayList<Movie> movies){
            MovieListViewAdapter adapter = new MovieListViewAdapter(this, movies);
            ListView listView = findViewById(R.id.list);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener((parent, view, position, id) -> {
                Movie movie = movies.get(position);//tell which movie is clicked
                @SuppressLint("DefaultLocale") String message = String.format("Clicked on position: %d, name: %s, %d", position, movie.getName(), movie.getYear());
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MovieListActivity.this, SingleMovieActivity.class);

                // Pass movie details to SingleMovieActivity
                intent.putExtra("movieTitle", movie.getName());
                intent.putExtra("movieYear", String.valueOf(movie.getYear()));
                intent.putExtra("movieDirector", movie.getDirector());
                intent.putExtra("movieStars", movie.getStars());
                intent.putExtra("movieGenres", movie.getGenre());

                // Start SingleMovieActivity
                startActivity(intent);
            });
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

    }
