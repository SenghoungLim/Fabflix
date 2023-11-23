import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class DOMParser {
    private final Map<String, Film> filmDict = new HashMap<>();
    private String director = null;
    private final Map<String, Star> starDict = new HashMap<>();
    private int newStarCount = 0;
    private final Map<String, String> genreDict = new HashMap<>();
    private final Map<String, String> newGenreDict = new HashMap<>();
    private int newGenreCount = 0;
    private final Map<String, String> starInMovieDict = new HashMap<>();
    private final Map<String, String> genresInMovieDict = new HashMap<>();

    // Method to populate the genre dictionary with initial values
    private void populateGenreDict() {
        genreDict.put("1", "Action");
        genreDict.put("2", "Adult");
        genreDict.put("3", "Adventure");
        genreDict.put("4", "Animation");
        genreDict.put("5", "Biography");
        genreDict.put("6", "Comedy");
        genreDict.put("7", "Crime");
        genreDict.put("8", "Documentary");
        genreDict.put("9", "Drama");
        genreDict.put("10", "Family");
        genreDict.put("11", "Fantasy");
        genreDict.put("12", "History");
        genreDict.put("13", "Horror");
        genreDict.put("14", "Music");
        genreDict.put("15", "Musical");
        genreDict.put("16", "Mystery");
        genreDict.put("17", "Reality-TV");
        genreDict.put("18", "Romance");
        genreDict.put("19", "Sci-Fi");
        genreDict.put("20", "Sport");
        genreDict.put("21", "Thriller");
        genreDict.put("22", "War");
        genreDict.put("23", "Western");

        newGenreCount = genreDict.size() +1;
    }

    // Method to run the parsing of mains243.xml
    public void runMains243() {
        populateGenreDict();
        String main243Url = "stanford-movies/mains243.xml";

        Document dom = parseXmlFile(main243Url);
        parseMains243(dom);
    }

    // Method to run the parsing of casts124.xml
    public void runCasts124() {
        String cast124 = "stanford-movies/casts124.xml";

        Document dom = parseXmlFile(cast124);
        parseCast124(dom);
    }

    public void runActors63() {
        String actors63 = "stanford-movies/actors63.xml";

        Document dom = parseXmlFile(actors63);
        parseActors63(dom);
    }

    // Method to parse XML file and return the Document
    private Document parseXmlFile(String url) {
        Document dom = null;
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            dom = documentBuilder.parse(url);

        } catch (ParserConfigurationException | SAXException | IOException error) {
            error.printStackTrace();
        }
        return dom;
    }

    // Method to parse mains243.xml
    private void parseMains243(Document dom) {
        Element moviesElement = dom.getDocumentElement();
        NodeList directorFilmsList = moviesElement.getElementsByTagName("directorfilms");

        for (int i = 0; i < directorFilmsList.getLength(); i++) {
            Element directorFilmsElement = (Element) directorFilmsList.item(i);

            NodeList directorList = directorFilmsElement.getElementsByTagName("director");
            Element directorElement = (Element) directorList.item(0);
            director = getTextValue(directorElement, "dirname");

            NodeList filmsList = directorFilmsElement.getElementsByTagName("films");
            Element filmsElement = (Element) filmsList.item(0);
            parseFilms(filmsElement);
        }
    }

    // Method to parse films in mains243.xml
    private void parseFilms(Element element) {
        NodeList filmsList = element.getElementsByTagName("film");
        for (int i = 0; i < filmsList.getLength(); i++) {
            Element filmElement = (Element) filmsList.item(i);
            parseFilm(filmElement);
        }
    }

    // Method to parse a film element
    private void parseFilm(Element element) {
        String filmId = getTextValue(element, "fid");
        String title = getTextValue(element, "t");
        String year = getTextValue(element, "year");

        NodeList genresList = element.getElementsByTagName("cats");
        if (genresList.getLength() > 0) {
            Element catElement = (Element) genresList.item(0);
            String genreText = getTextValue(catElement,"cat");

            if (genreText != null && !genreText.isEmpty()) {
                String[] genres = genreText.split("[\\s\\.]+");
                for (String genre : genres) {
                    // Remove non-alphanumeric characters except "-"
                    genre = genre.replaceAll("[^A-Za-z0-9-]", "");

                    genre = genre.trim();
                    if (genre != null && !genre.isEmpty()) {
                        if (!genreDict.containsValue(genre.trim()) && !newGenreDict.containsValue(genre.trim())) {
                            newGenreDict.put(String.valueOf(newGenreCount++), genre);
                        }
                        genresInMovieDict.put(getGenreId(genre), filmId);
                    }
                }
            }
        }

        Film film = new Film(filmId, title, year, director);
        if (filmId != null && !filmId.trim().isEmpty())
            filmDict.put(filmId, film);
    }

    // Method to get the genre ID from newGenreDict
    private String getGenreId(String genre) {
        for (Map.Entry<String, String> entry : newGenreDict.entrySet()) {
            if (entry.getValue().equals(genre)) {
                return entry.getKey();
            }
        }
        for (Map.Entry<String, String> entry : genreDict.entrySet()) {
            if (entry.getValue().equals(genre)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Method to get text value from an element by tag name
    private String getTextValue(Element element, String tagName) {
        String textValue = null;
        NodeList nodeList = element.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            if(!nodeList.item(0).getTextContent().isEmpty())
                textValue = nodeList.item(0).getTextContent();
        }
        return textValue;
    }

    // Method to parse casts124.xml
    private void parseCast124(Document dom) {
        Element castsElement = dom.getDocumentElement();
        NodeList directorFilmsList = castsElement.getElementsByTagName("dirfilms");

        for (int i = 0; i < directorFilmsList.getLength(); i++) {
            Element directorFilmsElement = (Element) directorFilmsList.item(i);
            parseStars(directorFilmsElement);
        }
    }

    // Method to parse stars in casts124.xml
    private void parseStars(Element element) {
        NodeList filmsList = element.getElementsByTagName("filmc");
        for (int i = 0; i < filmsList.getLength(); i++) {
            Element filmcElement = (Element) filmsList.item(i);

            NodeList mList = filmcElement.getElementsByTagName("m");
            for (int j = 0; j < mList.getLength(); j++) {
                Element mElement = (Element) mList.item(j);
                parseStar(mElement);
            }
        }
    }

    // Method to parse a star element
    private void parseStar(Element element) {
        String movieId = getTextValue(element, "f");
        String name = getTextValue(element, "a");
        String DOB = null;

        String starId = movieId+(newStarCount++);

        Star star = new Star(starId, name, DOB);
        starDict.put(starId, star);
        starInMovieDict.put(starId, movieId);
    }

    private void parseActors63(Document dom) {
        Element actorsElement = dom.getDocumentElement();
        NodeList actorList = actorsElement.getElementsByTagName("actor");

        for (int i = 0; i < actorList.getLength(); i++) {
            Element actorElement = (Element) actorList.item(i);
            parseActor(actorElement);
        }
    }

    private void parseActor(Element element) {
        String actorName = getTextValue(element, "stagename");
        String DOB = getTextValue(element, "dob");

        String starId = actorName.toLowerCase().replaceAll("\\s", "") + (newStarCount++);

        // Check if starDict already contains a star with the same actor name
        boolean starWithSameNameExists = starDict.values().stream()
                .anyMatch(star -> actorName.equals(star.getName()));

        if (starWithSameNameExists) {
            // Find the star with the same actor name
            String existingStarId = starDict.entrySet()
                    .stream()
                    .filter(entry -> actorName.equals(entry.getValue().getName()))
                    .map(Map.Entry::getKey)
                    .findFirst()
                    .orElse(null);


            Star existingStar = starDict.get(existingStarId);
            existingStar.setDOB(DOB);

        } else {
            // Create a new star if no existing star with the same actor name
            Star star = new Star(starId, actorName, DOB);
            starDict.put(starId, star);
        }
    }

    // Method to print films from mains243.xml
    private void printMains243() {
        System.out.println("Film Dictionary:");

        for (Map.Entry<String, Film> entry : filmDict.entrySet()) {
            Film film = entry.getValue();
            System.out.println("ID: " + entry.getKey() + ", Title: " + film.getTitle() + ", Year: " + film.getYear() + ", Director: " + film.getDirector());
        }
    }

    // Method to print stars from casts124.xml
    private void printCast124() {
        System.out.println("Star Dictionary:");

        for (Map.Entry<String, Star> entry : starDict.entrySet()) {
            Star star = entry.getValue();
            System.out.println("ID: " + entry.getKey() + ", Name: " + star.getName() + ", DOB: " + star.getDOB());
        }
    }

    // Method to print new genre dictionary
    private void printNewGenreDict() {
        System.out.println("New Genre Dictionary:");

        for (Map.Entry<String, String> entry : newGenreDict.entrySet()) {
            System.out.println("ID: " + entry.getKey() + ", Name: " + entry.getValue());
        }
    }

    // Method to print star in movie dictionary
    private void printStarInMovieDict() {
        System.out.println("Star In Movie Dictionary:");

        for (Map.Entry<String, String> entry : starInMovieDict.entrySet()) {
            System.out.println("starId: " + entry.getKey() + ", movieId: " + entry.getValue());
        }
    }

    // Method to print genres in movie dictionary
    private void printGenresInMovieDict() {
        System.out.println("Genres In Movie Dictionary:");

        for (Map.Entry<String, String> entry : genresInMovieDict.entrySet()) {
            System.out.println("genreId: " + entry.getKey() + ", movieId: " + entry.getValue());
        }
    }

    private void printActors63() {
        System.out.println("Actors Dictionary:");

        for (Map.Entry<String, Star> entry : starDict.entrySet()) {
            Star star = entry.getValue();
            System.out.println("ID: " + entry.getKey() + ", Name: " + star.getName() + ", DOB: " + star.getDOB());
        }
    }

    // Getter methods for the dictionaries
    public Map<String, Film> getFilmDict() {return filmDict;}

    public Map<String, Star> getStarDict() {return starDict;}

    public Map<String, String> getNewGenreDict() {return newGenreDict;}

    public Map<String, String> getStarInMovieDict() {return starInMovieDict;}

    public Map<String, String> getGenresInMovieDict() {return genresInMovieDict;}


    public static void main(String[] args) {
        DOMParser domParser = new DOMParser();
        domParser.runMains243();
        domParser.runCasts124();
        domParser.runActors63();
    }

}
