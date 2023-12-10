import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

@WebServlet(name = "FulltextSearchServlet", urlPatterns = "/api/fulltext")
public class FulltextSearchServlet extends HttpServlet {
    private static final long serialVersionUID = 8L;
    private DataSource dataSource;

    private static final String CREATE_INDEX_SQL = "ALTER TABLE movies ADD FULLTEXT INDEX idx_movies_title (title)";
    private static final String INDEX_INFO_SQL = "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME = 'movies' AND INDEX_NAME = 'idx_movies_title'";

    public void init(ServletConfig config) throws ServletException {
        super.init(config); // Add this line

        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");

            // Create FULLTEXT index on 'title' column in 'movies' table
            createFulltextIndex();
        } catch (NamingException | SQLException e) {
            throw new ServletException(e);
        }
    }

    private void createFulltextIndex() throws SQLException {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement createIndexStatement = connection.prepareStatement(CREATE_INDEX_SQL)) {

            if (!doesIndexExist(connection)) {
                createIndexStatement.executeUpdate();
            }
        }
    }

    private boolean doesIndexExist(Connection connection) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INDEX_INFO_SQL)) {
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next();
            }
        }
    }

    private static String modifyString(String input) {
        StringBuilder modifiedStringBuilder = new StringBuilder();

        String[] words = input.split("\\s+");

        for (int i = 0; i < words.length; i++) {
            modifiedStringBuilder.append("+").append(words[i]).append("*");
            if (i < words.length - 1) {
                modifiedStringBuilder.append(" ");
            }
        }

        return modifiedStringBuilder.toString();
    }


    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        long elapsedTimeTJ;
        long startTimeTS = System.nanoTime();

        String contextPath = getServletContext().getRealPath("/");
        String xmlFilePath = contextPath + "log.txt";
        System.out.println(xmlFilePath);
        File myfile = new File(xmlFilePath);

        // Create the file if it doesn't exist
        if (!myfile.exists()) {
            myfile.createNewFile();
        }

        // Write to the file using FileWriter
        FileWriter writer = new FileWriter(myfile, true);

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        String sortField = request.getParameter("sortField");
        String sortOrder = request.getParameter("sortOrder");

        try (Connection dbCon = dataSource.getConnection()) {
            String fulltext = request.getParameter("fulltext");

            fulltext = modifyString(fulltext);

            String query = buildQuery(sortField, sortOrder, fulltext);

            String countQuery = buildCountQuery(fulltext);

            try (PreparedStatement statement = dbCon.prepareStatement(query);
                 PreparedStatement countStatement = dbCon.prepareStatement(countQuery)) {

                int paramIndex = 1;
                int paramIndex1 = 1;
                if (!fulltext.isEmpty()) {
                    statement.setString(paramIndex++, fulltext);
                    countStatement.setString(paramIndex1, fulltext);
                }

                System.out.println(statement);


                int moviePerPage = 10;
                String page = request.getParameter("page");
                statement.setInt(paramIndex++, (Integer.parseInt(page) - 1) * moviePerPage); // Limit
                statement.setInt(paramIndex, moviePerPage); // Offset

                long startTimeTJ = System.nanoTime();
                ResultSet rs = statement.executeQuery();
                ResultSet rs1 = countStatement.executeQuery();
                long endTimeTJ = System.nanoTime();
                elapsedTimeTJ = endTimeTJ - startTimeTJ; // elapsed time in nano seconds. Note: print the values in nanoseconds

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

        long endTimeTS = System.nanoTime();
        long elapsedTimeTS = endTimeTS - startTimeTS; // elapsed time in nano seconds. Note: print the values in nanoseconds 

        String content = String.valueOf(elapsedTimeTS) + " " + String.valueOf(elapsedTimeTJ) + "\n";
        // Write the content to the file
        writer.write(content);

        } catch (SQLException | NumberFormatException e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            getServletContext().log("Error:", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            out.close();
            writer.close(); // Add this line
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
