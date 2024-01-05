import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            // Initialize the servlet by looking up the data source from the JNDI context.
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set the response content type to JSON.
        response.setContentType("application/json");

        // Retrieve the "id" parameter from the URL request.
        String id = request.getParameter("id");

        // Log the request for debugging purposes.
        request.getServletContext().log("Getting movie information for id: " + id);

        // Get an output stream for writing the response.
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            // Construct a SQL query with parameters represented by "?" using a StringBuilder.
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT m.title, m.year, m.director, ");
            queryBuilder.append("GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', ') AS genres, ");
            queryBuilder.append("GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', ') AS star_ids, ");
            queryBuilder.append("GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', ') AS stars, ");
            queryBuilder.append("MAX(r.rating) AS rating ");
            queryBuilder.append("FROM movies m ");
            queryBuilder.append("LEFT JOIN ratings r ON m.id = r.movieId ");
            queryBuilder.append("LEFT JOIN genres_in_movies gm ON m.id = gm.movieId ");
            queryBuilder.append("LEFT JOIN genres g ON gm.genreId = g.id ");
            queryBuilder.append("LEFT JOIN stars_in_movies sm ON m.id = sm.movieId ");
            queryBuilder.append("LEFT JOIN stars s ON sm.starId = s.id ");
            queryBuilder.append("LEFT JOIN (");
            queryBuilder.append("SELECT starId, COUNT(DISTINCT movieId) AS star_movie_count ");
            queryBuilder.append("FROM stars_in_movies ");
            queryBuilder.append("GROUP BY starId) star_counts ON s.id = star_counts.starId ");
            queryBuilder.append("WHERE m.id = ?");

            // Declare a prepared statement to execute the query.
            PreparedStatement statement = conn.prepareStatement(queryBuilder.toString());

            // Set the parameter represented by "?" in the query to the "id" obtained from the URL.
            statement.setString(1, id);

            // Execute the query and get the result set.
            ResultSet rs = statement.executeQuery();

            // Create a JSON array to store the results.
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row in the result set.
            while (rs.next()) {
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String genres = rs.getString("genres");
                String star_ids = rs.getString("star_ids");
                String stars = rs.getString("stars");
                String rating = rs.getString("rating");

                // Create a JSON object for each movie and add it to the array.
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("star_ids", star_ids);
                jsonObject.addProperty("stars", stars);
                jsonObject.addProperty("rating", rating);

                jsonArray.add(jsonObject);
            }
            // Close the result set and the prepared statement.
            rs.close();
            statement.close();

            // Log the number of results retrieved for debugging purposes.
            request.getServletContext().log("Getting " + jsonArray.size() + " results");
            // Write the JSON response to the output stream.
            out.write(jsonArray.toString());
            // Set the response status to 200 (OK).
            response.setStatus(200);
        } catch (Exception e) {
            // Handle exceptions by sending an error message in the JSON response.
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            // Log the error for debugging purposes.
            request.getServletContext().log("Error:", e);
            // Set the response status to 500 (Internal Server Error).
            response.setStatus(500);
        } finally {
            // Always remember to close the database connection after usage, done here by try-with-resources.
            out.close();
        }
    }
}
