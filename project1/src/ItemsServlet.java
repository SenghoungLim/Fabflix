import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

// Declaring a WebServlet called ItemServlet, which maps to url "/items"
@WebServlet(name = "ItemServlet", urlPatterns = "/items")

public class ItemsServlet extends HttpServlet {
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        // Get a instance of current session on the request
        HttpSession session = request.getSession();

        // Retrieve data named "previousItems" from session
        ArrayList<String> previousItems = (ArrayList<String>) session.getAttribute("previousItems");

        // If "previousItems" is not found on session, means this is a new user, thus we create a new previousItems
        // ArrayList for the user
        if (previousItems == null) {

            // Add the newly created ArrayList to session, so that it could be retrieved next time
            previousItems = new ArrayList<String>();
            session.setAttribute("previousItems", previousItems);
        }

        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");

        String newItem = request.getParameter("newItem"); // Get parameter that sent by GET request url

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        String title = "Items Purchased";

        out.println(String.format("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">\n" +
                "<html>\n" +
                "   <head>" +
                "   <title>%s</title>" +
                "   </head>\n" +
                "   <body bgcolor=\"#FDF5E6\">\n" +
                "       <h1>%s</h1>", title, title));

        // In order to prevent multiple clients, requests from altering previousItems ArrayList at the same time, we
        // lock the ArrayList while updating
        synchronized (previousItems) {
            if (newItem != null) {
                previousItems.add(newItem); // Add the new item to the previousItems ArrayList
            }

            // Display the current previousItems ArrayList
            if (previousItems.size() == 0) {
                out.println("<i>No items</i>");
            } else {
                out.println("<ul>");
                for (String previousItem : previousItems) {
                    out.println("<li>" + previousItem);
                }
                out.println("</ul>");
            }
        }

        out.println("</body></html>");
    }
}
