import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "BrowseGenreServlet", urlPatterns = "/api/browse-genre")
public class BrowseGenreServlet extends HttpServlet {
    private static final long serialVersionUID = 5L;
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        try (Connection dbCon = dataSource.getConnection()) {
            int moviePerPage = 25;
            String genre = request.getParameter("name");

            String page = request.getParameter("page");
            int offset = (Integer.parseInt(page) - 1) * moviePerPage;

            String query = buildQuery(sortField, sortOrder);
            String countQuery = buildCountQuery();

            try (PreparedStatement statement = dbCon.prepareStatement(query);
                 PreparedStatement countStatement = dbCon.prepareStatement(countQuery)) {

                statement.setString(1, genre + "%");
                statement.setInt(2, offset); // Limit
                statement.setInt(3, moviePerPage); // Offset

                countStatement.setString(1, genre + "%");
                ResultSet rs1 = countStatement.executeQuery();

                int totalRows = rs1.next() ? rs1.getInt("totalRows") : 0;

                ResultSet rs = statement.executeQuery();

                JsonArray jsonArray = new JsonArray();

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
                    jsonObject.addProperty("totalRows", totalRows);

                    jsonArray.add(jsonObject);
                }
                rs.close();
                rs1.close();
                statement.close();
                countStatement.close();

                request.getServletContext().log("Getting " + jsonArray.size() + " results");

                out.write(jsonArray.toString());
                response.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (SQLException | NumberFormatException e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            getServletContext().log("Error:", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            out.close();
        }
    }

    private String buildQuery(String sortField, String sortOrder) {
        StringBuilder queryBuilder = new StringBuilder(500);

        queryBuilder.append("WITH MovieList AS (");
        queryBuilder.append("    SELECT ");
        queryBuilder.append("        m.id, ");
        queryBuilder.append("        m.title, ");
        queryBuilder.append("        m.year, ");
        queryBuilder.append("        m.director, ");
        queryBuilder.append("        SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', '), ',', 3) AS genres ");
        queryBuilder.append("    FROM movies m ");
        queryBuilder.append("    LEFT JOIN genres_in_movies gm ON m.id = gm.movieId ");
        queryBuilder.append("    LEFT JOIN genres g ON gm.genreId = g.id ");
        queryBuilder.append("    GROUP BY m.id, m.title, m.year, m.director ");
        queryBuilder.append("    HAVING genres LIKE ? ");
        queryBuilder.append("    LIMIT ?, ?)");
        queryBuilder.append("\n");
        queryBuilder.append("SELECT ");
        queryBuilder.append("    m.id, ");
        queryBuilder.append("    m.title, ");
        queryBuilder.append("    m.year, ");
        queryBuilder.append("    m.director, ");
        queryBuilder.append("    m.genres, ");
        queryBuilder.append("    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ',', 3) AS star_ids, ");
        queryBuilder.append("    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ',', 3) AS stars, ");
        queryBuilder.append("    MAX(r.rating) AS rating ");
        queryBuilder.append("FROM MovieList AS m ");
        queryBuilder.append("LEFT JOIN ratings r ON m.id = r.movieId ");
        queryBuilder.append("LEFT JOIN stars_in_movies sm ON m.id = sm.movieId ");
        queryBuilder.append("LEFT JOIN stars s ON sm.starId = s.id ");
        queryBuilder.append("LEFT JOIN (");
        queryBuilder.append("    SELECT starId, COUNT(DISTINCT movieId) AS star_movie_count ");
        queryBuilder.append("    FROM stars_in_movies ");
        queryBuilder.append("    GROUP BY starId ");
        queryBuilder.append(") star_counts ON s.id = star_counts.starId ");
        queryBuilder.append("GROUP BY m.id, m.title, m.year, m.director ");

        if (sortOrder != null) {
            if ("Rating".equals(sortField) && "ASC".equals(sortOrder))
                queryBuilder.append("ORDER BY rating ASC, m.title ASC ");
            else if ("Rating".equals(sortField) && "DESC".equals(sortOrder))
                queryBuilder.append("ORDER BY rating DESC, m.title DESC ");
            else if ("Title".equals(sortField) && "ASC".equals(sortOrder))
                queryBuilder.append("ORDER BY m.title ASC, rating ASC ");
            else if ("Title".equals(sortField) && "DESC".equals(sortOrder))
                queryBuilder.append("ORDER BY m.title DESC, rating DESC ");
        }

        return queryBuilder.toString();
    }

    private String buildCountQuery() {
        StringBuilder queryBuilder = new StringBuilder(500);

        queryBuilder.append("SELECT COUNT(*) AS totalRows ");
        queryBuilder.append("FROM ( ");
        queryBuilder.append("    SELECT ");
        queryBuilder.append("        m.id, ");
        queryBuilder.append("        SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', '), ',', 3) AS genres ");
        queryBuilder.append("    FROM movies m ");
        queryBuilder.append("    LEFT JOIN genres_in_movies gm ON m.id = gm.movieId ");
        queryBuilder.append("    LEFT JOIN genres g ON gm.genreId = g.id ");
        queryBuilder.append("    GROUP BY m.id ");
        queryBuilder.append("    HAVING genres LIKE ? ");
        queryBuilder.append(") AS subquery;");
        return queryBuilder.toString();
    }
}
