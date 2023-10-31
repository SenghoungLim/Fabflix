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
import java.util.ArrayList;
import java.util.List;

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
            // Construct a SQL query with parameters represented by "?" using a StringBuilder.
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT\n");
            queryBuilder.append("    s.name AS StarName, ");
            queryBuilder.append("    CASE ");
            queryBuilder.append("        WHEN s.birthYear IS NOT NULL THEN s.birthYear ");
            queryBuilder.append("        ELSE 'N/A' ");
            queryBuilder.append("    END AS YearOfBirth, ");
            queryBuilder.append("    GROUP_CONCAT(DISTINCT m.title ORDER BY m.year DESC, m.title ASC SEPARATOR ', ') AS MovieTitles, ");
            queryBuilder.append("    GROUP_CONCAT(DISTINCT m.id ORDER BY m.year DESC, m.title ASC SEPARATOR ', ') AS MovieIds ");
            queryBuilder.append("FROM stars AS s ");
            queryBuilder.append("LEFT JOIN stars_in_movies AS sm ON s.id = sm.starId ");
            queryBuilder.append("LEFT JOIN movies AS m ON sm.movieId = m.id ");
            queryBuilder.append("WHERE s.id = ? ");
            queryBuilder.append("GROUP BY s.name, YearOfBirth ");

            // Declare a prepared statement to execute the query.
            PreparedStatement statement = conn.prepareStatement(queryBuilder.toString());

            // Set the parameter represented by "?" in the query to the "id" obtained from the URL.
            statement.setString(1, id);

            // Execute the query and get the result set.
            ResultSet rs = statement.executeQuery();

            // Create a list to store the results.
            List<JsonObject> starList = new ArrayList<>();

            // Iterate through each row in the result set.
            while (rs.next()) {
                String starName = rs.getString("StarName");
                String yearOfBirth = rs.getString("YearOfBirth");
                String movieTitles = rs.getString("MovieTitles");
                String movieIds = rs.getString("MovieIds");

                // Create a JSON object for each star and add it to the list.
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("StarName", starName);
                jsonObject.addProperty("YearOfBirth", yearOfBirth);
                jsonObject.addProperty("MovieTitles", movieTitles);
                jsonObject.addProperty("MovieIds", movieIds);

                starList.add(jsonObject);
            }

            // Close the result set and the prepared statement.
            rs.close();
            statement.close();

            // Log the number of results retrieved for debugging purposes.
            request.getServletContext().log("Getting " + starList.size() + " results");

            // Write the JSON response to the output stream using a JsonArray.
            JsonArray jsonArray = new JsonArray();
            starList.forEach(jsonArray::add);
            out.write(jsonArray.toString());

            // Set the response status to 200 (OK).
            response.setStatus(200);
        } catch (SQLException e) {
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
