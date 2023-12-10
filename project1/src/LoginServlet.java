import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.google.gson.JsonObject;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;

import jakarta.servlet.http.HttpSession;
import org.jasypt.util.password.StrongPasswordEncryptor;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    // Use http POST
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");    // Response mime type
        JsonObject responseJsonObject = new JsonObject();
        // Output stream to STDOUT
        PrintWriter out = response.getWriter();
        System.out.println("LoginServlet Started");
        String mobile = request.getParameter("mobile");
        mobile = (mobile != null && !mobile.isEmpty()) ? mobile : null;

        // Verify reCAPTCHA
        /*
        if (mobile == null || !"true".equalsIgnoreCase(mobile)) {
            try {
                //String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
                //RecaptchaVerifyUtils.verify(gRecaptchaResponse);
                //System.out.println("reCAPTCHA Verification Success");

            } catch (Exception e) {
                responseJsonObject.addProperty("status", "fail");
                response.getWriter().write(responseJsonObject.toString());
                out.close();
                return;
            }
        }
         */
        try(out; Connection dbCon = dataSource.getConnection();){
            // Declare a new statement
            Statement statement = dbCon.createStatement();

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            String query = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            preparedStatement.setString(1, username);


            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = preparedStatement.executeQuery();
            //user id to identify user
            String userId = "";
            // Process the data
            System.out.println("JMeter");
            Boolean success = false;
            if (rs.next()) {
                String storedEncryptedPassword = rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(password, storedEncryptedPassword);
                if (success) {
                    HttpSession session = request.getSession(true);
                    userId = rs.getString("id");
                    session.setAttribute("user", new User(username));
                    session.setAttribute("loggedIn", "true");
                    session.setAttribute("id", userId);

                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");

                } else {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            } else {
                responseJsonObject.addProperty("status", "fail");
                request.getServletContext().log("Login failed");

                if (!username.equals(username)) {
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exist");
                } else {
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            response.getWriter().write(responseJsonObject.toString());
            rs.close();
            statement.close();
            dbCon.close();

        } catch (Exception e) {
            request.getServletContext().log("Error: ", e);
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("message", "SQL error in doPost: " + e.getMessage());
            response.getWriter().write(responseJsonObject.toString());
            return;
        }
        out.close();
    }
}

