public class Film {
    private final String id;
    private final String title;
    private final String year;
    private final String director;
    public Film(String id, String title, String year, String director) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
    }
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

}