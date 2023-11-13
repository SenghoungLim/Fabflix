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

@WebServlet (name = "DashboardServlet", urlPatterns = "/api/_dashboard")
public class DashboardServlet extends HttpServlet {
    private DataSource dataSource;

    public void init(ServletConfig config){
        try{
            dataSource = (DataSource) new InitialContext().lookup ("java:comp/env/jdbc/moviedb");
        } catch (NamingException e){
            e.printStackTrace();
        }
    }
    //Use http POST (hide password & username) to verify the employee authentication
    public void doPost (HttpServletRequest request, HttpServletResponse response) throws IOException{
        System.out.println("Employee login started");
        response.setContentType("application/json");
        JsonObject responseJsonObj = new JsonObject();
        PrintWriter out = response.getWriter();
        //reCaptCha
        String gRecaptchaResponse = request.getParameter("g-recaptcha-response");
        System.out.println("gRecaptchaResponse:" + gRecaptchaResponse);
        try {
            RecaptchaVerifyUtils.verify(gRecaptchaResponse);
        } catch(Exception e) {
           recaptchaVerifyFailed(responseJsonObj);
        }
        //Verify username, password with Encryption
        try{
            System.out.println("Try data connection");
            //Connect to database
            Connection dbCon = dataSource.getConnection();
            Statement statement = dbCon.createStatement();
            //Get authentication info
            String empUsername = request.getParameter("employeeUsername");
            String empPassword = request.getParameter("employeePassword");
            System.out.print("Employee username: " + empUsername);
            System.out.print("Employee Password: " + empPassword);
            String query = "SELECT * FROM employees WHERE email = ?";
            PreparedStatement prepState = dbCon.prepareStatement(query);
            prepState.setString(1, empUsername);
            //Process result
            ResultSet rs = prepState.executeQuery();
            Boolean success = false;
            if (rs.next()){
                String storedEncryptedPassword = rs.getString("password");
                success = new StrongPasswordEncryptor().checkPassword(empPassword, storedEncryptedPassword);

                if (success){
                    HttpSession session = request.getSession(true);
                    session.setAttribute("user", new User(empUsername));
                    session.setAttribute("loggedIn", "true");
                    employeeSuccessLogin(responseJsonObj);
                }else{
                    employeeFailLogin(responseJsonObj);
                    System.out.println("Emp Success failed: " + responseJsonObj );
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
            return;
        }
    }
    //Metadata
    // Add this method to DashboardServlet
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        JsonObject responseJsonObj = new JsonObject();
        PrintWriter out = response.getWriter();

        // Check if the user is logged in
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("loggedIn") != null) {
            try {
                // Connect to the database
                Connection dbCon = dataSource.getConnection();
                DatabaseMetaData metaData = dbCon.getMetaData();

                // Specify the tables to include
                String[] includedTables = {"movies", "stars", "stars_in_movies", "genres", "genres_in_movies", "customers", "sales", "creditcards", "ratings", "employees"};

                JsonArray tablesArray = new JsonArray();

                for (String tableName : includedTables) {
                    JsonObject tableObject = new JsonObject();
                    tableObject.addProperty("tableName", tableName);

                    ResultSet columns = metaData.getColumns(null, null, tableName, "%");
                    JsonArray columnsArray = new JsonArray();

                    while (columns.next()) {
                        String columnName = columns.getString("COLUMN_NAME");
                        String columnType = columns.getString("TYPE_NAME");

                        JsonObject columnObject = new JsonObject();
                        columnObject.addProperty("columnName", columnName);
                        columnObject.addProperty("columnType", columnType);

                        columnsArray.add(columnObject);
                    }

                    tableObject.add("columns", columnsArray);
                    tablesArray.add(tableObject);
                }

                responseJsonObj.add("tables", tablesArray);
                responseJsonObj.addProperty("status", "success");
                responseJsonObj.addProperty("message", "Database metadata retrieved successfully");
                //System.out.println("Response JSON For metadata: " + responseJsonObj);
                dbCon.close();
            } catch (SQLException e) {
                responseJsonObj.addProperty("status", "error");
                responseJsonObj.addProperty("message", "SQL error in doGet: " + e.getMessage());
            }
        } else {
            responseJsonObj.addProperty("status", "fail");
            responseJsonObj.addProperty("message", "User not logged in");
        }

        out.write(responseJsonObj.toString());
        out.close();
    }

    //Recaptcha error
    private void recaptchaVerifyFailed (JsonObject responseJsonObj){
        responseJsonObj.addProperty("status", "fail");
        responseJsonObj.addProperty("message", "reCAPTCHA verification failed");
        System.out.println("reCAPTCHA verification failed");
    }
    //Employee success login msg
    private void employeeSuccessLogin (JsonObject responseJsonObj){
        responseJsonObj.addProperty("status", "success");
        responseJsonObj.addProperty("message", "Employee Login Successfully");
        System.out.println("Employee Login Successfully");
        System.out.println("Response JSON: " + responseJsonObj);
    }
    //Employee failed login msg
    private void employeeFailLogin (JsonObject responseJsonObj){
        responseJsonObj.addProperty("status", "fail");
        responseJsonObj.addProperty("message", "Employee Login failed");
        System.out.println("Employee Login failed");
    }

}
