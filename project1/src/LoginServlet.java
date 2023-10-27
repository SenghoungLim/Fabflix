import javax.naming.InitialContext;
import javax.naming.NamingException;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedbexample,
 * generates output as a html <table>
 */

// Declaring a WebServlet called FormServlet, which maps to url "/form"
@WebServlet(name = "FormServlet", urlPatterns = "/login")
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

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Building page head with title
        out.println("<html><head><title>MovieDBExample: Found Records</title></head>");

        // Building page body
        out.println("<body><h1>MovieDBExample: Found Records</h1>");


        try {

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            String query = "SELECT * FROM customers WHERE email = ? AND password = ?";
            PreparedStatement preparedStatement = dbCon.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);

            // Log to localhost log
            request.getServletContext().log("query：" + query);

            // Perform the query
            ResultSet rs = preparedStatement.executeQuery();

            // Process the data
            if (rs.next()) {
                //Redirect to homepage
                RequestDispatcher rd = request.getRequestDispatcher("index.html");
                rd.forward(request, response);
            }
            else{
                out.println("Login Failed :(");
            }
            //Testing
//            while (rs.next()) {
//                String m_ID = rs.getString("ID");
//                String m_Name = rs.getString("firstName");
//                out.println(String.format("<tr><td>%s</td><td>%s</td></tr>", m_ID, m_Name));
//            }
//            out.println("</table>");

            // Close all structures
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

            // Output Error Massage to html
            out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doPost: %s</p></body></html>", e.getMessage()));
            return;
        }
        out.close();
    }
}
