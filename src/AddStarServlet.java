import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.StrongPasswordEncryptor;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.UUID;

@WebServlet (name = "AddStarServlet", urlPatterns = "/api/addStar")
public class AddStarServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    //Use http POST
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject responseJsonObj = new JsonObject();
        PrintWriter out = response.getWriter();

        try {
            Connection dbCon = dataSource.getConnection();

            // Get data from the form
            String starName = request.getParameter("starNameInput");
            String birthYear = request.getParameter("birthYearInput");

            // Generate starId
            String starId = UUID.randomUUID().toString().substring(0, 8);
            System.out.println("Star Name: " + starName);
            System.out.println("Birth Year: " + birthYear);
            System.out.println("Star ID: " + starId);
            // Prepare SQL query
            String query;
            PreparedStatement prepState;

            if (birthYear != null && !birthYear.isEmpty()) {
                query = "INSERT INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
                prepState = dbCon.prepareStatement(query);
                prepState.setString(1, starId);
                prepState.setString(2, starName);
                prepState.setString(3, birthYear);
            } else {
                query = "INSERT INTO stars (id, name) VALUES (?, ?)";
                prepState = dbCon.prepareStatement(query);
                prepState.setString(1, starId);
                prepState.setString(2, starName);
            }


            // Execute the query
            int rowsAffected = prepState.executeUpdate();

            // Process result
            if (rowsAffected > 0) {
                // Star added successfully
                responseJsonObj.addProperty("status", "success");
                responseJsonObj.addProperty("message", "Star added successfully");
                responseJsonObj.addProperty("starId", starId);
                responseJsonObj.addProperty("starName", starName);
                responseJsonObj.addProperty("birthYear", birthYear);
            } else {
                // Failed to add star
                responseJsonObj.addProperty("status", "fail");
                responseJsonObj.addProperty("message", "Failed to add star");
            }

            // Send response
            response.getWriter().write(responseJsonObj.toString());
            System.out.println("After wrote to json response: " + responseJsonObj);
            // Close resources
            prepState.close();
            dbCon.close();

        } catch (SQLException e) {
            System.out.println("Catch Exception started");
            e.printStackTrace();
            request.getServletContext().log("Error: ", e);
            responseJsonObj.addProperty("status", "error");
            responseJsonObj.addProperty("message", "SQL error in doPost: " + e.getMessage());
            response.getWriter().write(responseJsonObj.toString());
        }
    }
}