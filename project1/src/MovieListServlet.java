/*
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
import java.sql.SQLException;


@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
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
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            // Define the SQL query using PreparedStatement to prevent SQL injection and StringBuilder for query construction.
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("WITH TopRatings AS (");
            queryBuilder.append("    SELECT movieId, MAX(rating) AS max_rating");
            queryBuilder.append("    FROM ratings");
            queryBuilder.append("    GROUP BY movieId");
            queryBuilder.append("    ORDER BY max_rating DESC");
            queryBuilder.append("    LIMIT 20");
            queryBuilder.append(")");
            queryBuilder.append("SELECT");
            queryBuilder.append("    m.id,");
            queryBuilder.append("    m.title,");
            queryBuilder.append("    m.year,");
            queryBuilder.append("    m.director,");
            queryBuilder.append("    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name SEPARATOR ', '), ', ', 3) AS genres,");
            queryBuilder.append("    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id SEPARATOR ', '), ', ', 3)  AS star_ids,");
            queryBuilder.append("    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name SEPARATOR ', '), ', ', 3)  AS stars,");
            queryBuilder.append("    tr.max_rating AS rating");
            queryBuilder.append("FROM movies m");
            queryBuilder.append("JOIN TopRatings tr ON m.id = tr.movieId");
            queryBuilder.append("LEFT JOIN genres_in_movies gm ON m.id = gm.movieId");
            queryBuilder.append("LEFT JOIN genres g ON gm.genreId = g.id");
            queryBuilder.append("LEFT JOIN stars_in_movies sm ON m.id = sm.movieId");
            queryBuilder.append("LEFT JOIN stars s ON sm.starId = s.id");
            queryBuilder.append("GROUP BY m.id, m.title, m.year, m.director");
            queryBuilder.append("ORDER BY rating DESC");

            // Prepare and execute the SQL query using PreparedStatement.
            PreparedStatement statement = conn.prepareStatement(queryBuilder.toString());
            ResultSet rs = statement.executeQuery();
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                // Retrieve data from the result set for each movie.
                String id = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String genres = rs.getString("genres");
                String star_ids = rs.getString("star_ids");
                String stars = rs.getString("stars");
                String rating = rs.getString("rating");

                // Create a JSON object to represent the movie and add it to the JSON array.
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("star_ids", star_ids);
                jsonObject.addProperty("stars", stars);
                jsonObject.addProperty("rating", rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            // Log the number of results retrieved and send the JSON response to the client.
            request.getServletContext().log("Getting " + jsonArray.size() + " results");
            out.write(jsonArray.toString());
            response.setStatus(200);
        } catch (SQLException e) {
            // Handle exceptions by sending an error message in the JSON response.
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            request.getServletContext().log("Error:", e);
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
 */