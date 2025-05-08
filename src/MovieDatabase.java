import java.util.*;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.util.stream.Collectors;

/**
 * MovieDatabase class manages a collection of movies and provides functionality for
 * searching, autocomplete, and finding connections between movies.
 * It maintains several indices for efficient lookups:
 * - movieNameIndex: Maps movie titles to Movie objects
 * - personIndex: Maps person names to sets of movies they're involved in
 * - genreIndex: Maps genres to lists of movies
 * - movieTrie: Provides efficient prefix-based movie title search
 */
public class MovieDatabase {
    private Map<String, Movie> movieNameIndex;
    private Map<String, Set<Movie>> personIndex;
    private Map<String, List<Movie>> genreIndex;
    private MovieTrie movieTrie;

    // ==================== CONSTRUCTORS ====================

    /**
     * Constructs an empty MovieDatabase with initialized data structures.
     */
    public MovieDatabase() {
        this.movieNameIndex = new HashMap<>();
        this.personIndex = new HashMap<>();
        this.genreIndex = new HashMap<>();
        this.movieTrie = new MovieTrie();
    }

    /**
     * Constructs a MovieDatabase and initializes it with data from the specified CSV files.
     * @param moviesPath Path to the movies CSV file
     * @param creditsPath Path to the credits CSV file
     * @throws IOException If there's an error reading the files
     * @throws CsvValidationException If there's an error parsing the CSV files
     */
    public MovieDatabase(String moviesPath, String creditsPath) throws IOException, CsvValidationException {
        this();
        loadFromCSV(moviesPath, creditsPath);
    }

    // ==================== CORE DATABASE OPERATIONS ====================

    /**
     * Adds a movie to the database and updates all relevant indices.
     * @param movie The movie to add
     */
    public void addMovie(Movie movie) {
        movieNameIndex.put(movie.getTitle(), movie);
        movieTrie.insert(movie.getTitle());
        
        // Add movie to genre index for each of its genres
        for (String genre : movie.getGenres()) {
            genreIndex.computeIfAbsent(genre, k -> new ArrayList<>()).add(movie);
        }
    }

    /**
     * Finds a movie by its exact title.
     * @param name The title of the movie to find
     * @return The Movie object if found, null otherwise
     */
    public Movie findMovie(String name) {
        return movieNameIndex.get(name);
    }

    /**
     * Gets all movies in the database.
     * @return List of all Movie objects
     */
    public List<Movie> getAllMovies() {
        return new ArrayList<>(movieNameIndex.values());
    }

    // ==================== SEARCH AND AUTOCOMPLETE ====================

    /**
     * Gets autocomplete suggestions for a given prefix.
     * @param prefix The prefix to search for
     * @param k The maximum number of suggestions to return
     * @return List of movie titles that start with the given prefix
     */
    public List<String> getAutocompleteSuggestions(String prefix, int k) {
        return movieTrie.getWordsWithPrefix(prefix, k);
    }

    /**
     * Gets autocomplete suggestions for a given prefix, case-insensitive.
     * @param prefix The prefix to search for
     * @param k The maximum number of suggestions to return
     * @return List of movie titles that start with the given prefix (case-insensitive)
     */
    public List<String> getAutocompleteSuggestionsCaseInsensitive(String prefix, int k) {
        return movieTrie.getWordsWithPrefix(prefix.toLowerCase(), k);
    }

    /**
     * Gets autocomplete suggestions for a given prefix with a minimum length filter.
     * @param prefix The prefix to search for
     * @param k The maximum number of suggestions to return
     * @param minLength The minimum length of movie titles to include
     * @return List of movie titles that start with the given prefix and meet the length requirement
     */
    public List<String> getAutocompleteSuggestionsWithMinLength(String prefix, int k, int minLength) {
        List<String> suggestions = movieTrie.getWordsWithPrefix(prefix, k);
        return suggestions.stream()
            .filter(s -> s.length() >= minLength)
            .collect(Collectors.toList());
    }

    // ==================== MOVIE CONNECTIONS AND RELATIONSHIPS ====================

    /**
     * Validates if there is a valid connection between two movies.
     * A valid connection exists if the movies share any cast or crew members.
     * @param m1 The first movie
     * @param m2 The second movie
     * @return A Connection object if a valid connection exists, null otherwise
     */
    public Connection validateConnection(Movie m1, Movie m2) {
        // Check for shared actors
        for (Person actor : m1.getCast()) {
            if (m2.getCast().contains(actor)) {
                return new Connection(m1, m2, actor, "actor");
            }
        }
        // Check for shared directors
        for (Person director : m1.getCrew()) {
            if (m2.getCrew().contains(director)) {
                return new Connection(m1, m2, director, "director");
            }
        }
        return null;
    }

    /**
     * Gets all movies of a specific genre.
     * @param genre The genre to search for
     * @return List of movies in the specified genre
     */
    public List<Movie> getMoviesByGenre(String genre) {
        return genreIndex.getOrDefault(genre, new ArrayList<>());
    }

    /**
     * Gets all movies a person has been involved in.
     * @param personName The name of the person
     * @return Set of movies the person has worked on
     */
    public Set<Movie> getMoviesByPerson(String personName) {
        return personIndex.getOrDefault(personName, new HashSet<>());
    }

    // ==================== DATA LOADING AND PARSING ====================

    /**
     * Loads movie and credit data from CSV files.
     * @param moviesPath Path to the movies CSV file
     * @param creditsPath Path to the credits CSV file
     * @throws IOException If there's an error reading the files
     * @throws CsvValidationException If there's an error parsing the CSV files
     */
    public void loadFromCSV(String moviesPath, String creditsPath) throws IOException, CsvValidationException {
        Map<Integer, Movie> idToMovie = new HashMap<>();
        try (CSVReader reader = new CSVReader(new FileReader(moviesPath))) {
            String[] fields;
            reader.readNext(); // skip header
            while ((fields = reader.readNext()) != null) {
                int id = Integer.parseInt(fields[3]);
                String title = fields[17];
                List<String> genres = parseGenres(fields[1]);
                Movie movie = new Movie(id, title, 0, genres, new ArrayList<>(), new ArrayList<>());
                idToMovie.put(id, movie);
                addMovie(movie);
            }
        }

        // Load credits (cast and crew) using OpenCSV
        try (CSVReader reader = new CSVReader(new FileReader(creditsPath))) {
            String[] fields;
            reader.readNext(); // skip header
            while ((fields = reader.readNext()) != null) {
                // Skip lines that don't have at least 4 columns
                if (fields.length < 4) {
                    System.err.println("Skipping malformed credits row (insufficient columns): " + Arrays.toString(fields));
                    continue;
                }
                int movieId = Integer.parseInt(fields[0]);
                Movie movie = idToMovie.get(movieId);
                if (movie == null) continue;
                List<Person> cast = parsePeople(fields[2], "cast", movie);
                List<Person> crew = parsePeople(fields[3], "crew", movie);
                movie.getCast().addAll(cast);
                movie.getCrew().addAll(crew);
            }
        }
    }

    /**
     * Parses genres from a JSON string in the movies CSV.
     * @param json JSON string containing genre information
     * @return List of genre names
     */
    private List<String> parseGenres(String json) {
        List<String> genres = new ArrayList<>();
        if (json == null || json.isEmpty() || !json.trim().startsWith("[")) {
            return genres;
        }
        // Convert CSV-style double double-quotes to single double-quote
        json = json.replaceAll("\"\"", "\"");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                genres.add(arr.getJSONObject(i).getString("name"));
            }
        } catch (Exception e) {
            System.err.println("Skipping malformed JSON for genres: " + json);
        }
        return genres;
    }

    /**
     * Parses people (cast or crew) from a JSON string in the credits CSV.
     * @param json JSON string containing cast or crew information
     * @param type Either "cast" or "crew" to indicate the type of people being parsed
     * @param movie The movie these people are associated with
     * @return List of Person objects
     */
    private List<Person> parsePeople(String json, String type, Movie movie) {
        List<Person> people = new ArrayList<>();
        if (json == null || json.isEmpty() || !json.trim().startsWith("[")) {
            System.err.println("Empty or invalid JSON for " + type + ": " + json);
            return people;
        }
        // Convert CSV-style double double-quotes to single double-quote
        json = json.replaceAll("\"\"", "\"");
        try {
            JSONArray arr = new JSONArray(json);
            for (int i = 0; i < arr.length(); i++) {
                JSONObject obj = arr.getJSONObject(i);
                String name = obj.getString("name");
                String role = type.equals("cast") ? 
                    obj.optString("character", "") : 
                    obj.optString("job", "");
                
                Person person = new Person(0, name, role);
                
                // Add movie to person's set of movies
                personIndex.computeIfAbsent(name, k -> new HashSet<>()).add(movie);
                
                people.add(person);
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON for " + type + ": " + e.getMessage());
            System.err.println("JSON content: " + json);
        }
        return people;
    }
}