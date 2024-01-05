import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet(name = "SearchServlet", urlPatterns = "/api/search-mobile-movies")
public class SearchMobileServlet extends HttpServlet {
    private static final long serialVersionUID = 4L;
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
        String movieTitle = request.getParameter("title");

        if (movieTitle != null && !movieTitle.isEmpty()) {
            // Query the database and retrieve movie details
            try (Connection connection = dataSource.getConnection()) {
                String query = "SELECT * FROM movies WHERE title = ?";
                try (PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setString(1, movieTitle);
                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            // Retrieve movie details from the result set
                            String title = resultSet.getString("title");
                            int year = resultSet.getInt("year");
                            // Add more properties as needed

                            // Create a JSON response
                            String jsonResponse = String.format("{\"title\": \"%s\", \"year\": %d}", title, year);
                            out.println(jsonResponse);
                        } else {
                            // Movie not found
                            out.println("{\"error\": \"Movie not found\"}");
                        }
                    }
                }
            } catch (SQLException e) {
                // Handle database error
                e.printStackTrace();
                out.println("{\"error\": \"Database error\"}");
            }
        } else {
            // Movie title parameter not provided
            out.println("{\"error\": \"Movie title parameter not provided\"}");
        }
    }
}