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

@WebServlet(name = "BrowseLetterServlet", urlPatterns = "/api/browse-letter")
public class BrowseLetterServlet extends HttpServlet {
    private static final long serialVersionUID = 6L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            // Initialize the servlet by looking up the data source from the JNDI context.
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            // If there is a naming exception, throw a runtime exception.
            throw new RuntimeException(e);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Set the response content type to JSON.
        response.setContentType("application/json");

        // Get an output stream for writing the response.
        PrintWriter out = response.getWriter();

        try {
            // Create a new connection to the database
            Connection dbCon = dataSource.getConnection();

            // Construct the SQL query using a StringBuilder for query construction.
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT m.id, m.title, m.year, m.director, ");
            queryBuilder.append("GROUP_CONCAT(DISTINCT g.name SEPARATOR ', ') AS genres, ");
            queryBuilder.append("GROUP_CONCAT(DISTINCT s.id SEPARATOR ', ') AS star_ids, ");
            queryBuilder.append("GROUP_CONCAT(DISTINCT s.name SEPARATOR ', ') AS stars, ");
            queryBuilder.append("MAX(r.rating) AS rating ");
            queryBuilder.append("FROM movies m ");
            queryBuilder.append("LEFT JOIN ratings r ON m.id = r.movieId ");
            queryBuilder.append("LEFT JOIN genres_in_movies gm ON m.id = gm.movieId ");
            queryBuilder.append("LEFT JOIN genres g ON gm.genreId = g.id ");
            queryBuilder.append("LEFT JOIN stars_in_movies sm ON m.id = sm.movieId ");
            queryBuilder.append("LEFT JOIN stars s ON sm.starId = s.id ");
            queryBuilder.append("WHERE 1 = 1");

            String letter = request.getParameter("letter");
            if (letter.equals("*")) {
                // Retrieve movies starting with non-alphanumeric characters (0-9, A-Z)
                queryBuilder.append(" AND (m.title REGEXP '^[^A-Za-z0-9]')");
            } else {
                // Retrieve movies starting with a specific letter (case-insensitive)
                queryBuilder.append(" AND LOWER(m.title) LIKE ?");
                letter = letter.toLowerCase(); // Convert letter to lowercase
            }

            queryBuilder.append(" GROUP BY m.id, m.title, m.year, m.director");

            // Log to localhost log
            request.getServletContext().log("query：" + queryBuilder.toString());

            // Create a PreparedStatement
            PreparedStatement statement = dbCon.prepareStatement(queryBuilder.toString());

            if (!letter.equals("*")) {
                statement.setString(1, letter + "%");
            }

            // Execute the query
            ResultSet rs = statement.executeQuery();

            // Create a JSON array to store the results.
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row in the result set.
            while (rs.next()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", rs.getString("id"));
                jsonObject.addProperty("title", rs.getString("title"));
                jsonObject.addProperty("year", rs.getString("year"));
                jsonObject.addProperty("director", rs.getString("director"));
                jsonObject.addProperty("genres", rs.getString("genres"));
                jsonObject.addProperty("star_ids", rs.getString("star_ids"));
                jsonObject.addProperty("stars", rs.getString("stars"));
                jsonObject.addProperty("rating", rs.getString("rating"));

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
