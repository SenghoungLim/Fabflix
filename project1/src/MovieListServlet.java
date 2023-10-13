import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "MovieListServlet", urlPatterns = "/api/movie-list")
public class MovieListServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/moviedb");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection conn = dataSource.getConnection()) {
            Statement statement = conn.createStatement();

            String query = "SELECT\n" +
                    "    m.id,\n" +
                    "    m.title,\n" +
                    "    m.year,\n" +
                    "    m.director,\n" +
                    "    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT g.name ORDER BY g.id ASC SEPARATOR ', '), ', ', 3) AS genres,\n" +
                    "    SUBSTRING_INDEX(GROUP_CONCAT(DISTINCT s.name ORDER BY s.id ASC SEPARATOR ', '), ', ', 3) AS stars,\n" +
                    "    MAX(r.rating) AS rating\n" +
                    "FROM\n" +
                    "    movies m\n" +
                    "LEFT JOIN\n" +
                    "    ratings r ON m.id = r.movieId\n" +
                    "LEFT JOIN\n" +
                    "    genres_in_movies gm ON m.id = gm.movieId\n" +
                    "LEFT JOIN\n" +
                    "    genres g ON gm.genreId = g.id\n" +
                    "LEFT JOIN\n" +
                    "    stars_in_movies sm ON m.id = sm.movieId\n" +
                    "LEFT JOIN\n" +
                    "    stars s ON sm.starId = s.id\n" +
                    "GROUP BY\n" +
                    "    m.id, m.title, m.year, m.director\n" +
                    "ORDER BY\n" +
                    "    rating DESC\n" +
                    "LIMIT 20";

            ResultSet rs = statement.executeQuery(query);
            JsonArray jsonArray = new JsonArray();

            while (rs.next()) {
                String id = rs.getString("id");
                String title = rs.getString("title");
                String year = rs.getString("year");
                String director = rs.getString("director");
                String genres = rs.getString("genres");
                String stars = rs.getString("stars");
                String rating = rs.getString("rating");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("id", id);
                jsonObject.addProperty("title", title);
                jsonObject.addProperty("year", year);
                jsonObject.addProperty("director", director);
                jsonObject.addProperty("genres", genres);
                jsonObject.addProperty("stars", stars);
                jsonObject.addProperty("rating", rating);

                jsonArray.add(jsonObject);
            }
            rs.close();
            statement.close();

            request.getServletContext().log("getting " + jsonArray.size() + " results");

            out.write(jsonArray.toString());
            response.setStatus(200);
        } catch (Exception e) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());
            response.setStatus(500);
        } finally {
            out.close();
        }
    }
}
