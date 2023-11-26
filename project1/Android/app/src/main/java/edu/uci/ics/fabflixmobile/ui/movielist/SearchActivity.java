package edu.uci.ics.fabflixmobile.ui.movielist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import edu.uci.ics.fabflixmobile.R;


public class SearchActivity extends AppCompatActivity{
    private EditText searchInput;
    private Button searchButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // initialize/inflate the layout
        setContentView(R.layout.search_bar);

        searchInput  = findViewById(R.id.searchBarId);
        searchButton = findViewById(R.id.searchButtonId);

        searchButton.setOnClickListener(view -> {
            if (!searchInput.getText().toString().isEmpty()) {
                search();
            }
        });
    }

    public void search() {
        Bundle bundle = new Bundle();
        bundle.putString("searchInput", searchInput.getText().toString());
        Intent intent = new Intent(this, MovieListActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }
}