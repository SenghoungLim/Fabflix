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
import java.sql.*;

@WebServlet(name = "AutocompleteServlet", urlPatterns = "/api/autocomplete")
public class AutocompleteServlet extends HttpServlet {
    private static final long serialVersionUID = 9L;
    private DataSource dataSource;

    private static final String CREATE_INDEX_SQL = "ALTER TABLE movies ADD FULLTEXT INDEX idx_movies_title (title)";
    private static final String INDEX_INFO_SQL = "SELECT INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS WHERE TABLE_NAME = 'movies' AND INDEX_NAME = 'idx_movies_title'";

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");

            // Create FULLTEXT index on 'title' column in 'movies' table
            createFulltextIndex();
        } catch (NamingException e) {
            throw new RuntimeException(e);
        } catch (SQLException e) {
            throw new RuntimeException(e);
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
        try (Connection dbCon = dataSource.getConnection()) {
            String autocomplete = request.getParameter("autocomplete");

            String newAutocomplete = modifyString(autocomplete);

            String query = buildQuery(newAutocomplete);

            try (PreparedStatement statement = dbCon.prepareStatement(query)) {

                if (!autocomplete.isEmpty()) {
                    // Set parameter for SQL LIKE (partial matching)
                    statement.setString(1, '%' + autocomplete + '%');

                    // Set parameter for full-text search
                    statement.setString(2, newAutocomplete);
                }

                System.out.println(statement);


                ResultSet rs = statement.executeQuery();


                JsonArray jsonArray = new JsonArray();
                while (rs.next()) {
                    jsonArray.add(generateJsonObject(rs.getString("id"), rs.getString("title")));
                }

                rs.close();
                statement.close();
                request.getServletContext().log("Getting " + jsonArray.size() + " results");

                response.getWriter().write(jsonArray.toString());
                response.setStatus(HttpServletResponse.SC_OK);
            }

        } catch (SQLException | NumberFormatException e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());

            getServletContext().log("Error:", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private static JsonObject generateJsonObject(String movieId, String title) {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("value", title);

        JsonObject additionalDataJsonObject = new JsonObject();
        additionalDataJsonObject.addProperty("movieId", movieId);

        jsonObject.add("data", additionalDataJsonObject);
        return jsonObject;
    }

    private String buildQuery(String fulltext) {
        StringBuilder queryBuilder = new StringBuilder(500);
        queryBuilder.append("SELECT ");
        queryBuilder.append("   m.id, ");
        queryBuilder.append("   m.title ");
        queryBuilder.append("FROM movies m ");
        queryBuilder.append("WHERE 1 = 1");

        if (!fulltext.isEmpty()) {
            // SQL LIKE for partial matching
            queryBuilder.append(" AND m.title LIKE ?");

            // Full-text search using MATCH ... AGAINST
            queryBuilder.append(" OR MATCH(m.title) AGAINST (? IN BOOLEAN MODE)");
        }

        queryBuilder.append(" LIMIT 10");
        return queryBuilder.toString();
    }

}

