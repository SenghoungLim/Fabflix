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
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "SearchServlet", urlPatterns = "/api/form")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;
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
            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameters
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star_name = request.getParameter("star-name");

            // Generate a SQL query
            String query = "SELECT\n" +
                    "    m.id,\n" +
                    "    m.title,\n" +
                    "    m.year,\n" +
                    "    m.director,\n" +
                    "    GROUP_CONCAT(DISTINCT g.name SEPARATOR ', ') AS genres,\n" +
                    "    GROUP_CONCAT(DISTINCT s.id SEPARATOR ', ') AS star_ids,\n" +
                    "    GROUP_CONCAT(DISTINCT s.name SEPARATOR ', ') AS stars,\n" +
                    "    MAX(r.rating) AS rating\n" +
                    "FROM movies m\n" +
                    "LEFT JOIN ratings r ON m.id = r.movieId\n" +
                    "LEFT JOIN genres_in_movies gm ON m.id = gm.movieId\n" +
                    "LEFT JOIN genres g ON gm.genreId = g.id\n" +
                    "LEFT JOIN stars_in_movies sm ON m.id = sm.movieId\n" +
                    "LEFT JOIN stars s ON sm.starId = s.id\n" +
                    "WHERE 1 = 1"; // Initialize the WHERE clause

            if (title.trim().length() > 0) {
                query += String.format(" AND m.title LIKE '%s%%'", title);
            }
            if (year.trim().length() > 0) {
                query += String.format(" AND m.year = '%s'", year);
            }
            if (director.trim().length() > 0) {
                query += String.format(" AND m.director LIKE '%s%%'", director);
            }
            if (star_name.trim().length() > 0) {
                query += String.format(" AND s.name LIKE '%s%%'", star_name);
            }

            query += " GROUP BY m.id, m.title, m.year, m.director";


            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = statement.executeQuery(query);

            // Create a JSON array to store the results.
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row in the result set.
            while (rs.next()) {
                String mtitle = rs.getString("title");
                String myear = rs.getString("year");
                String mdirector = rs.getString("director");
                String genres = rs.getString("genres");
                String star_ids = rs.getString("star_ids");
                String stars = rs.getString("stars");
                String rating = rs.getString("rating");

                // Create a JSON object for each movie and add it to the array.
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("title", mtitle);
                jsonObject.addProperty("year", myear);
                jsonObject.addProperty("director", mdirector);
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
        }
        catch (Exception e) {
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
