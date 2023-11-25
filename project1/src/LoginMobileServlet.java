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

@WebServlet(name = "LoginMobileServlet", urlPatterns = "/api/androidLogin")
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
    public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException{
        response.setContentType("application/json");
        JsonObject responseJsonObj = new JsonObject();

        //Verify username, password with Encryption
        try{
            //Connect to database
            Connection dbCon = dataSource.getConnection();
            Statement statement = dbCon.createStatement();
            //Get authentication info
            String mobileUsername = request.getParameter("username");
            String mobilePassword = request.getParameter("password");

            String query = "SELECT * FROM employees WHERE email = ?";
            PreparedStatement prepState = dbCon.prepareStatement(query);
            prepState.setString(1, mobileUsername);
            //Process result
            ResultSet rs = prepState.executeQuery();
            Boolean success = false;
            if (rs.next()){
                String storedEncryptedPassword = rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(mobilePassword, storedEncryptedPassword);

                if (success){
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user", new User(mobileUsername));
                    session.setAttribute("loggedIn", "true");
                    MobileSuccessLogin(responseJsonObj);
                }else{
                    MobileFailLogin(responseJsonObj);
                    System.out.println("Mobile login failed: " + responseJsonObj );
                }
            } else{
                responseJsonObj.addProperty("status", "fail");
                responseJsonObj.addProperty("message", "incorrect password");
            }
            response.getWriter().write(responseJsonObj.toString());
            rs.close();
            statement.close();
            dbCon.close();

        } catch(Exception e){
            request.getServletContext().log("Error: ", e);
            responseJsonObj.addProperty("status", "error");
            responseJsonObj.addProperty("message", "SQL error in doPost: " + e.getMessage());
            response.getWriter().write(responseJsonObj.toString());
        }

    }
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        doPost(request, response);
        System.out.print("doGet for mobile");
    }
    private void MobileSuccessLogin (JsonObject responseJsonObj){
        responseJsonObj.addProperty("status", "success");
        responseJsonObj.addProperty("message", "Mobile Login Successfully");
        System.out.println("Mobile Login Successfully");
        System.out.println("Response JSON: " + responseJsonObj);
    }
    //Employee failed login msg
    private void MobileFailLogin (JsonObject responseJsonObj){
        responseJsonObj.addProperty("status", "fail");
        responseJsonObj.addProperty("message", "Mobile Login failed");
        System.out.println("Mobile Login failed");
    }
}

