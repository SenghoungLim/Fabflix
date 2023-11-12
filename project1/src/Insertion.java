import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;

public class Insertion {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        try {
            DOMParser domParser = new DOMParser();
            domParser.runMains243();
            domParser.runCasts124();
            domParser.runActors63();

            Map<String, Film> filmDict = domParser.getFilmDict();
            Map<String, Star> starDict = domParser.getStarDict();
            Map<String, String> newGenreDict = domParser.getNewGenreDict();
            Map<String, String> starInMovieDict = domParser.getStarInMovieDict();
            Map<String, String> genresInMovieDict = domParser.getGenresInMovieDict();

            String loginUser = Parameters.username;
            String loginPasswd = Parameters.password;
            String loginUrl = "jdbc:" + Parameters.dbtype + "://" + Parameters.dbname;

            Class.forName("com.mysql.cj.jdbc.Driver");

            // Connect to the database
            try (Connection connection = DriverManager.getConnection(loginUrl, loginUser, loginPasswd)) {
                // Insert movies
                batchInsertMovies(connection, filmDict);
                System.out.println("INSERTED INTO movies");

                // Insert stars
                batchInsertStars(connection, starDict);
                System.out.println("INSERTED INTO stars");

                // Insert genres
                batchInsertGenres(connection, newGenreDict);
                System.out.println("INSERTED INTO genres");

                // Insert genres_in_movies
                batchInsertGenresInMovies(connection, genresInMovieDict);
                System.out.println("INSERTED INTO genres_in_movies");

                // Insert stars_in_movies
                batchInsertStarsInMovies(connection, starInMovieDict);
                System.out.println("INSERTED INTO stars_in_movies");

            } catch (SQLException e) {
                System.out.println("Database error: " + e.getMessage());
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("Total execution time: " + duration + " milliseconds");

        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private static void batchInsertMovies(Connection connection, Map<String, Film> filmDict) throws SQLException {
        String insertMovies = "INSERT IGNORE INTO movies (id, title, year, director) VALUES (?, ?, ?, ?)";
        try (PreparedStatement insertMoviesStatement = connection.prepareStatement(insertMovies)) {
            for (Map.Entry<String, Film> entry : filmDict.entrySet()) {
                Film film = entry.getValue();
                insertMoviesStatement.setString(1, film.getId());
                insertMoviesStatement.setString(2, film.getTitle());
                insertMoviesStatement.setString(3, film.getYear());
                insertMoviesStatement.setString(4, film.getDirector());
                insertMoviesStatement.executeUpdate();
            }
        }
    }

    private static void batchInsertStars(Connection connection, Map<String, Star> starDict) throws SQLException {
        String insertStars = "INSERT IGNORE INTO stars (id, name, birthYear) VALUES (?, ?, ?)";
        try (PreparedStatement insertStarsStatement = connection.prepareStatement(insertStars)) {
            for (Map.Entry<String, Star> entry : starDict.entrySet()) {
                Star star = entry.getValue();
                insertStarsStatement.setString(1, star.getId());
                insertStarsStatement.setString(2, star.getName());
                insertStarsStatement.setString(3, star.getDOB());
                insertStarsStatement.executeUpdate();
            }
        }
    }

    private static void batchInsertGenres(Connection connection, Map<String, String> newGenreDict) throws SQLException {
        String insertGenres = "INSERT IGNORE INTO genres (id, name) VALUES (?, ?)";
        try (PreparedStatement insertGenresStatement = connection.prepareStatement(insertGenres)) {
            for (Map.Entry<String, String> entry : newGenreDict.entrySet()) {
                String genreId = entry.getKey();
                String genreName = entry.getValue();
                insertGenresStatement.setString(1, genreId);
                insertGenresStatement.setString(2, genreName);
                insertGenresStatement.executeUpdate();
            }
        }
    }

    private static void batchInsertGenresInMovies(Connection connection, Map<String, String> genresInMovieDict) throws SQLException {
        String insertGenresInMovies = "INSERT IGNORE INTO genres_in_movies (genreId, movieId) VALUES (?, ?)";
        try (PreparedStatement insertGenresInMoviesStatement = connection.prepareStatement(insertGenresInMovies)) {
            for (Map.Entry<String, String> entry : genresInMovieDict.entrySet()) {
                String genreId = entry.getKey();
                String movieId = entry.getValue();
                insertGenresInMoviesStatement.setString(1, genreId);
                insertGenresInMoviesStatement.setString(2, movieId);
                insertGenresInMoviesStatement.executeUpdate();
            }
        }
    }

    private static void batchInsertStarsInMovies(Connection connection, Map<String, String> starInMovieDict) throws SQLException {
        String insertStarsInMovies = "INSERT IGNORE INTO stars_in_movies (starId, movieId) VALUES (?, ?)";
        try (PreparedStatement insertStarsInMoviesStatement = connection.prepareStatement(insertStarsInMovies)) {
            for (Map.Entry<String, String> entry : starInMovieDict.entrySet()) {
                String starId = entry.getKey();
                String movieId = entry.getValue();
                insertStarsInMoviesStatement.setString(1, starId);
                insertStarsInMoviesStatement.setString(2, movieId);
                insertStarsInMoviesStatement.executeUpdate();
            }
        }
    }
}