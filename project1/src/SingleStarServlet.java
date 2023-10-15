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

@WebServlet(name = "SingleStarServlet", urlPatterns = "/api/single-star")
public class SingleStarServlet extends HttpServlet {
    private static final long serialVersionUID = 3L;
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

        // Retrieve the "id" parameter from the URL request.
        String id = request.getParameter("id");

        // Log the request for debugging purposes.
        request.getServletContext().log("Getting id: " + id);

        // Get an output stream for writing the response.
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            // Construct a SQL query with parameters represented by "?".
            String query = "SELECT\n" +
                            "    s.name AS StarName,\n" +
                            "    CASE\n" +
                            "        WHEN s.birthYear IS NOT NULL THEN s.birthYear\n" +
                            "        ELSE 'N/A'\n" +
                            "    END AS YearOfBirth,\n" +
                            "    GROUP_CONCAT(DISTINCT m.title SEPARATOR ', ') AS MovieTitles,\n" +
                            "    GROUP_CONCAT(DISTINCT m.id SEPARATOR ', ') AS MovieIds\n" +
                            "FROM stars AS s\n" +
                            "LEFT JOIN stars_in_movies AS sm ON s.id = sm.starId\n" +
                            "LEFT JOIN movies AS m ON sm.movieId = m.id\n" +
                            "WHERE s.id = ?";

            // Declare a prepared statement to execute the query.
            PreparedStatement statement = conn.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the "id" obtained from the URL.
            statement.setString(1, id);

            // Execute the query and get the result set.
            ResultSet rs = statement.executeQuery();

            // Create a JSON array to store the results.
            JsonArray jsonArray = new JsonArray();

            // Iterate through each row in the result set.
            while (rs.next()) {
                String starName = rs.getString("StarName");
                String yearOfBirth = rs.getString("YearOfBirth");
                String movieTitles = rs.getString("MovieTitles");
                String movieIds = rs.getString("MovieIds");

                // Create a JSON object for each star and add it to the array.
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("StarName", starName);
                jsonObject.addProperty("YearOfBirth", yearOfBirth);
                jsonObject.addProperty("MovieTitles", movieTitles);
                jsonObject.addProperty("MovieIds", movieIds);

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
