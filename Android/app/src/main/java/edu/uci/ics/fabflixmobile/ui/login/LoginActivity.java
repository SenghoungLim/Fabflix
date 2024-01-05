package edu.uci.ics.fabflixmobile.ui.login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import edu.uci.ics.fabflixmobile.data.NetworkManager;
import edu.uci.ics.fabflixmobile.data.Constants;
import edu.uci.ics.fabflixmobile.databinding.ActivityLoginBinding;
import edu.uci.ics.fabflixmobile.ui.movielist.MovieListActivity;
import edu.uci.ics.fabflixmobile.ui.movielist.SearchActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import edu.uci.ics.fabflixmobile.data.Constants;
public class LoginActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private TextView message;

    /*
      In Android, localhost is the address of the device or the emulator.
      To connect to your machine, you need to use the below IP address
     */
    //Getting url for api
    Constants constants = new Constants();
    private String baseURL = constants.getUrl();
    @Override
    public void onCreate(Bundle savedInstanceState) { //gets called first
        super.onCreate(savedInstanceState);

        ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
        // upon creation, inflate and initialize the layout
        setContentView(binding.getRoot()); //Goes ot xml, and xml creates the element (button, editText...)

        username = binding.username; //point to xml editText element
        password = binding.password; //same
        message = binding.message; //same
        final Button loginButton = binding.login; //bind with button login

        //assign a listener to call a function to handle the user request when clicking a button
        loginButton.setOnClickListener(view -> login()); //Whenever click executes login function below
    }

    @SuppressLint("SetTextI18n")
    public void login() {
        message.setText("Trying to login");
        // use the same network queue across our application
        final RequestQueue queue = NetworkManager.sharedManager(this).queue; //java request queue to fabflix
        // request type is POST
        final StringRequest loginRequest = new StringRequest(
                Request.Method.POST,
                baseURL + "/api/login",
                response -> {
                    // TODO: should parse the json response to redirect to appropriate functions
                    //  upon different response value.
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        Log.d("Return result from servlet, ", response);
                        handleMobileLogin(jsonResponse);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    // error
                    if (error.networkResponse != null) {
                        int statusCode = error.networkResponse.statusCode;
                        Log.d("login.error", "Status Code: " + statusCode);
                    } else {
                        Log.d("login.error", "Network error" + error.toString());
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                // POST request form data
                final Map<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                params.put("mobile", "true");
                Log.d("params,", params.toString());
                return params;
            }
        };
        // important: queue.add is where the login request is actually sent
        queue.add(loginRequest);
    }

    private void handleMobileLogin(JSONObject rep) throws JSONException {
        String status = rep.getString("status");
        Log.d("login.success", rep.toString());
        if ("success".equals(status)){
            Log.d("login.success", rep.toString());
            //Complete and destroy login activity once successful
            finish();
            // initialize the activity(page)/destination
            Intent SearchPage = new Intent(LoginActivity.this, SearchActivity.class);
            //Redirect to hte movielist page(MovieListActivity.class in our file)
            // activate the list page.
            startActivity(SearchPage);
        } else{
            CharSequence text = "Credentials failed, please try again.";
            int duration = Toast.LENGTH_SHORT;
            Toast toast = Toast.makeText(LoginActivity.this, text, duration);
            toast.show();
        }
    }

}