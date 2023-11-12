import com.google.gson.JsonObject;

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
import java.sql.*;

@WebServlet(name = "AddMovieServlet", urlPatterns = "/api/addMovie")
public class AddMovieServlet extends HttpServlet {
    private DataSource dataSource;
    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    // Use http POST
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject responseJsonObj = new JsonObject();
        PrintWriter out = response.getWriter();
        Connection dbCon = null;

        try {
            dbCon = dataSource.getConnection();

            // Get data from the form
            String movieTitle = request.getParameter("movieTitleInput");
            int movieYear = Integer.parseInt(request.getParameter("movieYearInput"));
            String movieDirector = request.getParameter("movieDirectorInput");
            String starName = request.getParameter("starNameMovieInput");
            String genreName = request.getParameter("genreNameInput");

            // Check if the movie already exists
            System.out.println("Before checking movie exist");

            if (movieExists(dbCon, movieTitle, movieYear, movieDirector)) {
                System.out.println("Inside if movieExists");
                responseJsonObj.addProperty("status", "error");
                responseJsonObj.addProperty("message", "Movie already exists in the database.");
            } else {
                System.out.println("Inside if movieExists");
                // Prepare SQL call to add_movie stored procedure
                String query = "{CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?)}";
                System.out.println("ELSE after call add_movie");
                try (CallableStatement callableStatement = dbCon.prepareCall(query)) {
                    System.out.println("Inside try of add_movie");
                    callableStatement.setString(1, movieTitle);
                    callableStatement.setInt(2, movieYear);
                    callableStatement.setString(3, movieDirector);
                    callableStatement.setString(4, starName);
                    callableStatement.setString(5, genreName);
                    System.out.println("After setString");
                    // Register output parameters
                    callableStatement.registerOutParameter(6, Types.VARCHAR); // p_movie_id
                    callableStatement.registerOutParameter(7, Types.VARCHAR); // p_star_id
                    callableStatement.registerOutParameter(8, Types.INTEGER); // p_genre_id
                    System.out.println("After registerOut");
                    // Execute the stored procedure
                    callableStatement.executeUpdate();
                    System.out.println("After callable executeUpdate");
                    //Retrieve the generated IDs
                    String movieId = callableStatement.getString(6);
                    String starId = callableStatement.getString(7);
                    int genreId = callableStatement.getInt(8);

                    // Build a response indicating success
                    responseJsonObj.addProperty("status", "success");
                    responseJsonObj.addProperty("message", "Movie added successfully");
                    responseJsonObj.addProperty("movieTitle", movieTitle);
                    responseJsonObj.addProperty("movieYear", movieYear);
                    responseJsonObj.addProperty("movieDirector", movieDirector);
                    responseJsonObj.addProperty("starName", starName);
                    responseJsonObj.addProperty("genreName", genreName);
                    responseJsonObj.addProperty("movieId", movieId);
                    responseJsonObj.addProperty("starId", starId);
                    responseJsonObj.addProperty("genreId", genreId);
                    System.out.println("after addProperty");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            responseJsonObj.addProperty("status", "error");
            responseJsonObj.addProperty("message", "SQL error in doPost: " + e.getMessage());
            // Print SQL state and error code
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            response.getWriter().write(responseJsonObj.toString());
            return;
        } finally {
            // Close resources in the finally block
            try {
                if (dbCon != null) {
                    dbCon.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }

        // Send response outside the try block
        response.getWriter().write(responseJsonObj.toString());
        System.out.println("AddMovie response: " + responseJsonObj);
        System.out.println("End of doPost method");
    }

    // Helper method to check if the movie already exists
    private boolean movieExists(Connection connection, String title, int year, String director) throws SQLException {
        String query = "SELECT COUNT(*) FROM movies WHERE title = ? AND year = ? AND director = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, title);
            preparedStatement.setInt(2, year);
            preparedStatement.setString(3, director);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                System.out.println("Result from query: " + resultSet);
                resultSet.next();
                int count = resultSet.getInt(1);
                return count > 0;
            }
        }
    }
}

