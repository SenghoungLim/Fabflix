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
import java.sql.*;

@WebServlet(name = "FulltextSearchServlet", urlPatterns = "/api/fulltext")
public class FulltextSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 8L;
    private DataSource dataSource;

    private static final String CREATE_INDEX_SQL = "ALTER TABLE ? ADD FULLTEXT INDEX ? (?)";
    private static final String INDEX_INFO_SQL = "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME = ? AND INDEX_NAME = ?";

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");

            // Create FULLTEXT index on 'title' column in 'movies' table
            createFulltextIndex();
        } catch (NamingException | SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void createFulltextIndex() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement createIndexStatement = connection.prepareStatement(CREATE_INDEX_SQL)) {

            if (!doesIndexExist(connection)) {
                createIndexStatement.setString(1, "movies");
                createIndexStatement.setString(2, "title");
                createIndexStatement.setString(3, "idx_movies_title");
                createIndexStatement.executeUpdate();
            }
        }
    }

    private boolean doesIndexExist(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INDEX_INFO_SQL)) {
            statement.setString(1, "movies");
            statement.setString(2, "idx_movies_title");
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        try (Connection dbCon = dataSource.getConnection()) {
            String fulltext = request.getParameter("fulltext");

            String query = buildQuery(sortField, sortOrder, fulltext);

            String countQuery = buildCountQuery(fulltext);

            try (PreparedStatement statement = dbCon.prepareStatement(query);
                 PreparedStatement countStatement = dbCon.prepareStatement(countQuery)) {

                int paramIndex = 1;
                int paramIndex1 = 1;
                if (!fulltext.isEmpty()) {
                    statement.setString(paramIndex++, '+' + fulltext + '*');
                    countStatement.setString(paramIndex1, '+' + fulltext + '*');
                }

                int moviePerPage = 25;
                String page = request.getParameter("page");
                statement.setInt(paramIndex++, (Integer.parseInt(page) - 1) * moviePerPage); // Limit
                statement.setInt(paramIndex, moviePerPage); // Offset

                ResultSet rs = statement.executeQuery();
                ResultSet rs1 = countStatement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                int totalRows = rs1.next() ? rs1.getInt("totalRows") : 0;

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

    private String buildQuery(String sortField, String sortOrder, String fulltext) {
        StringBuilder queryBuilder = new StringBuilder(500);
        queryBuilder.append("WITH MovieList AS (");
        queryBuilder.append("    SELECT ");
        queryBuilder.append("        m.id, ");
        queryBuilder.append("        m.title, ");
        queryBuilder.append("        m.year, ");
        queryBuilder.append("        m.director ");
        queryBuilder.append("    FROM movies m ");
        queryBuilder.append("    WHERE 1 = 1");

        if (!fulltext.isEmpty()) {
            queryBuilder.append(" AND MATCH(title) AGAINST(? IN BOOLEAN MODE)");
        }

        queryBuilder.append("    LIMIT ?, ?)");
        queryBuilder.append("\n");

        queryBuilder.append("SELECT ");
        queryBuilder.append("    m.id, ");
        queryBuilder.append("    m.title, ");
        queryBuilder.append("    m.year, ");
        queryBuilder.append("    m.director, ");
        queryBuilder.append("    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.name ASC SEPARATOR ', '), ',', 3) AS genres, ");
        queryBuilder.append("    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.id ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ',', 3) AS star_ids, ");
        queryBuilder.append("    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY star_movie_count DESC, s.name ASC SEPARATOR ', '), ',', 3) AS stars, ");
        queryBuilder.append("    MAX(r.rating) AS rating ");
        queryBuilder.append("FROM MovieList AS m ");
        queryBuilder.append("LEFT JOIN ratings r ON m.id = r.movieId ");
        queryBuilder.append("LEFT JOIN stars_in_movies sm ON m.id = sm.movieId ");
        queryBuilder.append("LEFT JOIN stars s ON sm.starId = s.id ");
        queryBuilder.append("LEFT JOIN genres_in_movies gm ON m.id = gm.movieId ");
        queryBuilder.append("LEFT JOIN genres g ON gm.genreId = g.id ");
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

    private String buildCountQuery(String fulltext)  {
        StringBuilder queryBuilder = new StringBuilder(500);
        queryBuilder.append("SELECT COUNT(*) AS totalRows ");
        queryBuilder.append("FROM ( ");
        queryBuilder.append("    SELECT 1 "); // Change: Select a constant value
        queryBuilder.append("    FROM movies m ");
        queryBuilder.append("    WHERE 1 = 1");

        if (!fulltext.isEmpty()) {
            queryBuilder.append(" AND MATCH(title) AGAINST(? IN BOOLEAN MODE)");
        }

        queryBuilder.append("    GROUP BY m.id");
        queryBuilder.append(") AS subquery");

        return queryBuilder.toString();
    }
}
