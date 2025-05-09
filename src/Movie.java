import java.util.List;

/**
 * Represents a movie in the game, containing all relevant information about the movie
 * including its title, year, genres, cast, and crew. This class serves as a data model
 * for movies in the movie connection game.
 */
public class Movie {
    /** Unique identifier for the movie */
    private int id;
    
    /** Title of the movie */
    private String title;
    
    /** Release year of the movie */
    private int year;
    
    /** List of genres associated with the movie */
    private List<String> genres;
    
    /** List of actors and actresses in the movie */
    private List<Person> cast;
    
    /** List of crew members (directors, writers, etc.) of the movie */
    private List<Person> crew;

    /**
     * Constructs a new Movie with the specified details.
     *
     * @param id The unique identifier for the movie
     * @param title The title of the movie
     * @param year The release year of the movie
     * @param genres List of genres associated with the movie
     * @param cast List of actors and actresses in the movie
     * @param crew List of crew members of the movie
     */
    public Movie(int id, String title, int year, List<String> genres, List<Person> cast, List<Person> crew) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.genres = genres;
        this.cast = cast;
        this.crew = crew;
    }

    /**
     * Gets the unique identifier of the movie.
     *
     * @return The movie's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the title of the movie.
     *
     * @return The movie's title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Gets the release year of the movie.
     *
     * @return The year the movie was released
     */
    public int getYear() {
        return year;
    }

    /**
     * Gets the list of genres associated with the movie.
     *
     * @return List of genres for the movie
     */
    public List<String> getGenres() {
        return genres;
    }

    /**
     * Gets the list of actors and actresses in the movie.
     *
     * @return List of cast members
     */
    public List<Person> getCast() {
        return cast;
    }

    /**
     * Gets the list of crew members of the movie.
     *
     * @return List of crew members
     */
    public List<Person> getCrew() {
        return crew;
    }
}
