import java.util.List;

public class Movie {
    private int id;
    private String title;
    private int year;
    private List<String> genres;
    private List<Person> cast;
    private List<Person> crew;

    public Movie(int id, String title, int year, List<String> genres, List<Person> cast, List<Person> crew) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.genres = genres;
        this.cast = cast;
        this.crew = crew;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getYear() {
        return year;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<Person> getCast() {
        return cast;
    }

    public List<Person> getCrew() {
        return crew;
    }
}
