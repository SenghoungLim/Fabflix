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
@WebServlet(name = "LoginServlet", urlPatterns = "/api/android-login")
public class LoginMobileServlet extends HttpServlet {

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

//        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
//        System.out.println("gRecaptchaResponse=" + gRecaptchaResponse);

        // Verify reCAPTCHA
//        try {
//            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
//            System.out.println("reCAPTCHA Verification Success" );
//
//        } catch (Exception e) {
//            responseJsonObject.addProperty("status", "fail");
//            response.getWriter().write(responseJsonObject.toString());
//            out.close();
//            return;
//        }
        try {
            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            String query = "SELECT * FROM customers WHERE email = ?";
            PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            preparedStatement.setString(1, username);
//            preparedStatement.setString(2, password);

            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = preparedStatement.executeQuery();
            //user id to identify user
            String userId = "";
            // Process the data
            Boolean success = false;
            if (rs.next()) {
                String storedEncryptedPassword = rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(password, storedEncryptedPassword);
                if(success) {
                    HttpSession session = request.getSession(true);
                    userId = rs.getString("id");
                    session.setAttribute("user", new User(username));
                    session.setAttribute("loggedIn", "true");
                    session.setAttribute("id", userId);
                    //String encryptedPassword = rs.getString("password");
                    //success = new StrongPasswordEncryptor().checkPassword(password, encryptedPassword);
                    System.out.println("user ID," + userId);
                    System.out.println("user email (user): " + session.getAttribute("user"));
                    System.out.println("isLoggedIn: " + session.getAttribute("loggedIn"));
                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                }else{
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect password");
                }
            }
            else{
                // Login fail
                responseJsonObject.addProperty("status", "fail");
                // Log to localhost log
                request.getServletContext().log("Login failed");
                // sample error messages. in practice, it is not a good idea to tell user which one is incorrect/not exist.
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
            /*
             * After you deploy the WAR file through tomcat manager webpage,
             *   there's no console to see the print messages.
             * Tomcat append all the print messages to the file: tomcat_directory/logs/catalina.out
             *
             * To view the last n lines (for example, 100 lines) of messages you can use:
             *   tail -100 catalina.out
             * This can help you debug your program after deploying it on AWS.
             */
            request.getServletContext().log("Error: ", e);
            responseJsonObject.addProperty("status", "error");
            responseJsonObject.addProperty("message", "SQL error in doPost: " + e.getMessage());
            response.getWriter().write(responseJsonObject.toString());

            // Output Error Message to html
            //out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doPost: %s</p></body></html>", e.getMessage()));
            return;
        }
        out.close();
    }
}

