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

        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        try (Connection dbCon = dataSource.getConnection()) {
            // Generate a SQL query using PreparedStatement to prevent SQL injection.
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String starName = request.getParameter("starName");

            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT m.id, m.title, m.year, m.director, ");
            queryBuilder.append("SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', '), ',', 3) AS genres, ");
            queryBuilder.append("SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ',', 3) AS star_ids, ");
            queryBuilder.append("SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ',', 3) AS stars, ");
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
            queryBuilder.append("WHERE 1=1 ");

            if (!title.isEmpty()) {
                queryBuilder.append(" AND m.title LIKE ?");
            }
            if (!year.isEmpty()) {
                queryBuilder.append(" AND m.year = ?");
            }
            if (!director.isEmpty()) {
                queryBuilder.append(" AND m.director LIKE ?");
            }
            if (!starName.isEmpty()) {
                queryBuilder.append(" AND s.name LIKE ?");
            }
            queryBuilder.append(" GROUP BY m.id, m.title, m.year, m.director ");

            if (sortOrder != null) {
                if (sortField.equals("Rating") && sortOrder.equals("ASC"))
                    queryBuilder.append("ORDER BY rating ASC, m.title ASC ");
                else if (sortField.equals("Rating") && sortOrder.equals("DESC"))
                    queryBuilder.append("ORDER BY rating DESC, m.title DESC ");
                else if (sortField.equals("Title") && sortOrder.equals("ASC"))
                    queryBuilder.append("ORDER BY m.title ASC, rating ASC ");
                else if (sortField.equals("Title") && sortOrder.equals("DESC"))
                    queryBuilder.append("ORDER BY m.title DESC, rating DESC ");
            }

            queryBuilder.append("LIMIT ?, ?"); // Add pagination limit and offset

            // Log to localhost log
            request.getServletContext().log("query: " + queryBuilder.toString());

            // Prepare the SQL query with placeholders.
            PreparedStatement statement = dbCon.prepareStatement(queryBuilder.toString());

            // Set the parameter values for the placeholders, using '%' for LIKE queries.
            int paramIndex = 1;
            if (!title.isEmpty()) {
                statement.setString(paramIndex++, "%" + title + "%");
            }
            if (!year.isEmpty()) {
                statement.setString(paramIndex++, year);
            }
            if (!director.isEmpty()) {
                statement.setString(paramIndex++, "%" + director + "%");
            }
            if (!starName.isEmpty()) {
                statement.setString(paramIndex++, "%" + starName + "%");
            }

            int moviePerPage = 25;
            String page = request.getParameter("page");
            statement.setInt(paramIndex++, (Integer.parseInt(page) - 1) * moviePerPage); // Limit
            statement.setInt(paramIndex, moviePerPage); // Offset

            // Perform the query
            ResultSet rs = statement.executeQuery();
            StringBuilder queryBuilder1 = new StringBuilder();
            queryBuilder1.append("SELECT COUNT(*) AS totalRows FROM (");
            queryBuilder1.append("SELECT m.id, m.title, m.year, m.director, ");
            queryBuilder1.append("SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', '), ',', 3) AS genres, ");
            queryBuilder1.append("SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ',', 3) AS star_ids, ");
            queryBuilder1.append("SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ',', 3) AS stars, ");
            queryBuilder1.append("MAX(r.rating) AS rating ");
            queryBuilder1.append("FROM movies m ");
            queryBuilder1.append("LEFT JOIN ratings r ON m.id = r.movieId ");
            queryBuilder1.append("LEFT JOIN genres_in_movies gm ON m.id = gm.movieId ");
            queryBuilder1.append("LEFT JOIN genres g ON gm.genreId = g.id ");
            queryBuilder1.append("LEFT JOIN stars_in_movies sm ON m.id = sm.movieId ");
            queryBuilder1.append("LEFT JOIN stars s ON sm.starId = s.id ");
            queryBuilder1.append("LEFT JOIN (");
            queryBuilder1.append("SELECT starId, COUNT(DISTINCT movieId) AS star_movie_count ");
            queryBuilder1.append("FROM stars_in_movies ");
            queryBuilder1.append("GROUP BY starId) star_counts ON s.id = star_counts.starId ");
            queryBuilder1.append("WHERE 1=1 ");

            if (!title.isEmpty()) {
                queryBuilder1.append(" AND m.title LIKE ?");
            }
            if (!year.isEmpty()) {
                queryBuilder1.append(" AND m.year = ?");
            }
            if (!director.isEmpty()) {
                queryBuilder1.append(" AND m.director LIKE ?");
            }
            if (!starName.isEmpty()) {
                queryBuilder1.append(" AND s.name LIKE ?");
            }
            queryBuilder1.append(" GROUP BY m.id, m.title, m.year, m.director ");

            queryBuilder1.append(") AS subquery");

            // Create a PreparedStatement
            PreparedStatement statement1 = dbCon.prepareStatement(queryBuilder1.toString());

            // Set the parameter values for the placeholders, using '%' for LIKE queries.
            int paramIndex1 = 1;
            if (!title.isEmpty()) {
                statement1.setString(paramIndex1++, "%" + title + "%");
            }
            if (!year.isEmpty()) {
                statement1.setString(paramIndex1++, year);
            }
            if (!director.isEmpty()) {
                statement1.setString(paramIndex1++, "%" + director + "%");
            }
            if (!starName.isEmpty()) {
                statement1.setString(paramIndex1, "%" + starName + "%");
            }

            // Log the query to the localhost log
            request.getServletContext().log("query1：" + queryBuilder1.toString());
            ResultSet rs1 = statement1.executeQuery();

            // Create a JSON array to store the results.
            JsonArray jsonArray = new JsonArray();

            int totalRows = 0;  // Initialize totalRows variable
            // Move the cursor to the first row of rs1 (there should be only one row)
            if (rs1.next()) {
                totalRows = rs1.getInt("totalRows");
            }

            // Iterate through each row in the result set.
            while (rs.next()) {
                String id = rs.getString("id");
                String mtitle = rs.getString("title");
                String myear = rs.getString("year");
                String mdirector = rs.getString("director");
                String genres = rs.getString("genres");
                String star_ids = rs.getString("star_ids");
                String stars = rs.getString("stars");
                String rating = rs.getString("rating");

                // Create a JSON object for each movie and add it to the array.
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", mtitle);
                jsonObject.addProperty("year", myear);
                jsonObject.addProperty("director", mdirector);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("star_ids", star_ids);
                jsonObject.addProperty("stars", stars);
                jsonObject.addProperty("rating", rating);
                jsonObject.addProperty("totalRows", totalRows);


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
