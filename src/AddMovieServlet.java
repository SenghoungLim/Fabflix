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

            String movieTitle = request.getParameter("movieTitleInput");
            int movieYear = Integer.parseInt(request.getParameter("movieYearInput"));
            String movieDirector = request.getParameter("movieDirectorInput");
            String starName = request.getParameter("starNameMovieInput");
            String genreName = request.getParameter("genreNameInput");

            System.out.println("Before checking movie exist");

            System.out.println("Inside if movieExists");

            String query = "{CALL add_movie(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            System.out.println("ELSE after call add_movie");

            try (CallableStatement callableStatement = dbCon.prepareCall(query)) {
                callableStatement.setString(1, movieTitle);
                callableStatement.setInt(2, movieYear);
                callableStatement.setString(3, movieDirector);
                callableStatement.setString(4, starName);
                callableStatement.setString(5, genreName);

                // Register output parameters
                callableStatement.registerOutParameter(6, Types.VARCHAR); // p_movie_id
                callableStatement.registerOutParameter(7, Types.VARCHAR); // p_star_id
                callableStatement.registerOutParameter(8, Types.INTEGER); // p_genre_id
                callableStatement.registerOutParameter(9, Types.VARCHAR); // p_message

                // Execute the stored procedure
                callableStatement.executeUpdate();

                // Retrieve the generated IDs and message
                String movieId = callableStatement.getString(6);
                String starId = callableStatement.getString(7);
                int genreId = callableStatement.getInt(8);
                String message = callableStatement.getString(9);

                // Build a response indicating success
                responseJsonObj.addProperty("status", "success");
                responseJsonObj.addProperty("message", message);
                responseJsonObj.addProperty("movieId", movieId);
                responseJsonObj.addProperty("starId", starId);
                responseJsonObj.addProperty("genreId", genreId);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            responseJsonObj.addProperty("status", "error");
            responseJsonObj.addProperty("message",  e.getMessage());

            // Print SQL state and error code
            System.out.println("SQL State: " + e.getSQLState());
            System.out.println("Error Code: " + e.getErrorCode());
            response.getWriter().write(responseJsonObj.toString());
            return;
        } finally {
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



}

