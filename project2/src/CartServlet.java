import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/cart
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        JsonArray cartItems = (JsonArray) session.getAttribute("cart");

        if (cartItems == null) {
            cartItems = new JsonArray();
        }
        List<String> cartItemList = new ArrayList<>();

        for (JsonElement element : cartItems) {
            cartItemList.add(element.toString());
        }

        request.getServletContext().log("getting " + cartItemList.size() + " items");
        JsonArray cartItemsArray = new JsonArray();

        cartItems.forEach(cartItemsArray::add);
        System.out.println("Show doGet cartItemsArray" + cartItemsArray);
        // write all the data into the jsonObject
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.add("cart", cartItemsArray);
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        //Getting the title, quantity and price of the movie
        // Retrieve the data sent from the client
        String movieId = request.getParameter("movieId");
        String movieTitle = request.getParameter("movieTitle");
        String moviePrice = request.getParameter("moviePrice");
        String quantity = request.getParameter("quantity");
        // Create a JSON object to store the cart items
        JsonObject cartJsonObject = new JsonObject();
        // Check if the cart already exists in the session
        JsonArray cart = (JsonArray) session.getAttribute("cart");
        if (cart == null) {
            cart = new JsonArray();
            session.setAttribute("cart", cart);
        }
        // Create a JSON object for the current item
        JsonObject itemJsonObject = new JsonObject();
        itemJsonObject.addProperty("movieId", movieId);
        itemJsonObject.addProperty("movieTitle", movieTitle);
        itemJsonObject.addProperty("moviePrice", moviePrice);
        itemJsonObject.addProperty("quantity", quantity);

        // Add the current item to the cart
        cart.add(itemJsonObject);
        // Respond with a JSON object indicating success
        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.add("cart", cart);
        responseJsonObject.addProperty("status", "success");
        System.out.println("CartServlet User JSON data: " + cart);
        System.out.println("CartServlet responseJsonObj: " + responseJsonObject);
        response.getWriter().write(responseJsonObject.toString());
    }
}
