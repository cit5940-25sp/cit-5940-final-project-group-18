import java.util.*;
import java.io.*;
import org.json.JSONArray;
import org.json.JSONObject;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import java.util.stream.Collectors;

/**
 * Manages a collection of movies and provides functionality for searching, autocomplete,
 * and finding connections between movies. This class serves as the central data store
 * for the movie connection game.
 * 
 * The database maintains several indices for efficient lookups:
 * - movieNameIndex: Maps movie titles to Movie objects for O(1) title lookups
 * - personIndex: Maps person names to sets of movies they're involved in for actor/director searches
 * - genreIndex: Maps genres to lists of movies for genre-based filtering
 * - movieTrie: Provides efficient prefix-based movie title search for autocomplete functionality
 */
public class MovieDatabase {
    /** Maps movie titles to their corresponding Movie objects for quick lookups */
    private Map<String, Movie> movieNameIndex;
    
    /** Maps person names to sets of movies they're involved in for connection validation */
    private Map<String, Set<Movie>> personIndex;
    
    /** Maps genres to lists of movies for genre-based filtering */
    private Map<String, List<Movie>> genreIndex;
    
    /** Trie data structure for efficient prefix-based movie title searches */
    private MovieTrie movieTrie;

    // ==================== CONSTRUCTORS ====================

    /**
     * Constructs an empty MovieDatabase with initialized data structures.
     * All indices are created but remain empty until movies are added.
     */
    public MovieDatabase() {
        this.movieNameIndex = new HashMap<>();
        this.personIndex = new HashMap<>();
        this.genreIndex = new HashMap<>();
        this.movieTrie = new MovieTrie();
    }

    /**
     * Constructs a MovieDatabase and initializes it with data from the specified CSV files.
     * The database will be populated with movies, their genres, cast, and crew information.
     *
     * @param moviesPath Path to the movies CSV file containing movie metadata
     * @param creditsPath Path to the credits CSV file containing cast and crew information
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
     * This method ensures the movie is properly indexed for all types of queries.
     *
     * @param movie The movie to add to the database
     */
    public void addMovie(Movie movie) {
        movieNameIndex.put(movie.getTitle(), movie);
        // Only add to trie if it has valid data
        if (hasValidData(movie)) {
            movieTrie.insert(movie.getTitle());
        }
        
        // Add movie to genre index for each of its genres
        for (String genre : movie.getGenres()) {
            genreIndex.computeIfAbsent(genre, k -> new ArrayList<>()).add(movie);
        }
    }

    /**
     * Finds a movie by its exact title.
     * This is a case-sensitive lookup that returns null if no exact match is found.
     *
     * @param name The exact title of the movie to find
     * @return The Movie object if found, null otherwise
     */
    public Movie findMovie(String name) {
        Movie movie = movieNameIndex.get(name);
        if (movie != null) {
            System.out.println("[DEBUG] Found movie: " + movie.getTitle());
            System.out.println("[DEBUG] Cast size: " + movie.getCast().size());
            System.out.println("[DEBUG] Crew size: " + movie.getCrew().size());
            
            // Only return the movie if it has valid data
            if (!hasValidData(movie)) {
                System.out.println("[DEBUG] Skipping movie due to missing cast/crew data: " + movie.getTitle());
                return null;
            }
        }
        return movie;
    }

    /**
     * Gets all movies in the database.
     * Returns a defensive copy of the movie collection to prevent external modification.
     *
     * @return List of all Movie objects in the database
     */
    public List<Movie> getAllMovies() {
        return movieNameIndex.values().stream()
            .filter(movie -> !movie.getCast().isEmpty() && !movie.getCrew().isEmpty())
            .collect(Collectors.toList());
    }

    // ==================== SEARCH AND AUTOCOMPLETE ====================

    /**
     * Gets autocomplete suggestions for a given prefix.
     * Returns up to k movie titles that start with the given prefix.
     *
     * @param prefix The prefix to search for
     * @param k The maximum number of suggestions to return
     * @return List of movie titles that start with the given prefix
     */
    public List<String> getAutocompleteSuggestions(String prefix, int k) {
        return movieTrie.getWordsWithPrefix(prefix, k);
    }

    /**
     * Gets case-insensitive autocomplete suggestions for a given prefix.
     * Returns up to k movie titles that start with the given prefix, ignoring case.
     *
     * @param prefix The prefix to search for (case-insensitive)
     * @param k The maximum number of suggestions to return
     * @return List of movie titles that start with the given prefix (case-insensitive)
     */
    public List<String> getAutocompleteSuggestionsCaseInsensitive(String prefix, int k) {
        return movieTrie.getWordsWithPrefix(prefix.toLowerCase(), k);
    }

    /**
     * Gets autocomplete suggestions with a minimum length filter.
     * Returns up to k movie titles that start with the given prefix and meet the length requirement.
     *
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
     * The connection type (actor or director) is determined by the shared person's role.
     *
     * @param m1 The first movie
     * @param m2 The second movie
     * @return A Connection object if a valid connection exists, null otherwise
     */
    public Connection validateConnection(Movie m1, Movie m2) {
        System.out.println("[DEBUG] Validating connection between: " + m1.getTitle() + " and " + m2.getTitle());
        
        // Check for shared actors
        for (Person actor : m1.getCast()) {
            if (m2.getCast().contains(actor)) {
                System.out.println("[DEBUG] Found shared actor: " + actor.getName());
                return new Connection(m1, m2, actor, "actor");
            }
        }
        
        // Check for shared directors
        for (Person director : m1.getCrew()) {
            if (m2.getCrew().contains(director)) {
                System.out.println("[DEBUG] Found shared director: " + director.getName());
                return new Connection(m1, m2, director, "director");
            }
        }
        
        System.out.println("[DEBUG] No valid connection found between movies");
        return null;
    }

    /**
     * Gets all movies of a specific genre.
     * Returns an empty list if no movies are found for the given genre.
     *
     * @param genre The genre to search for
     * @return List of movies in the specified genre
     */
    public List<Movie> getMoviesByGenre(String genre) {
        return genreIndex.getOrDefault(genre, new ArrayList<>());
    }

    /**
     * Gets all movies a person has been involved in.
     * Returns an empty set if no movies are found for the given person.
     *
     * @param personName The name of the person
     * @return Set of movies the person has worked on
     */
    public Set<Movie> getMoviesByPerson(String personName) {
        return personIndex.getOrDefault(personName, new HashSet<>());
    }

    // ==================== DATA LOADING AND PARSING ====================

    /**
     * Loads movie and credit data from CSV files.
     * This method populates the database with movies and their associated information.
     * It handles both movie metadata and cast/crew information.
     *
     * @param moviesPath Path to the movies CSV file
     * @param creditsPath Path to the credits CSV file
     * @throws IOException If there's an error reading the files
     * @throws CsvValidationException If there's an error parsing the CSV files
     */
    public void loadFromCSV(String moviesPath, String creditsPath) throws IOException, CsvValidationException {
        Map<Integer, Movie> idToMovie = new HashMap<>();
        
        // First load all movies without adding to trie
        try (CSVReader reader = new CSVReader(new FileReader(moviesPath))) {
            String[] fields;
            reader.readNext(); // skip header
            while ((fields = reader.readNext()) != null) {
                int id = Integer.parseInt(fields[3]);
                String title = fields[17];
                List<String> genres = parseGenres(fields[1]);
                String releaseDate = fields[11];
                int year = 0;
                if (releaseDate != null && releaseDate.length() >= 4) {
                    try {
                        year = Integer.parseInt(releaseDate.substring(0, 4));
                    } catch (Exception e) {
                        // fallback or log error
                    }
                }
                Movie movie = new Movie(id, title, year, genres, new ArrayList<>(), new ArrayList<>());
                idToMovie.put(id, movie);
                movieNameIndex.put(title, movie);  // Add to name index but not trie yet
                addMovie(movie);  // Add to genre index
            }
        }

        // Then load credits and add to trie only if movie has cast/crew
        try (CSVReader reader = new CSVReader(new FileReader(creditsPath))) {
            String[] fields;
            reader.readNext(); // skip header
            while ((fields = reader.readNext()) != null) {
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
                
                // Only add to trie if movie has cast and crew
                if (!movie.getCast().isEmpty() && !movie.getCrew().isEmpty()) {
                    movieTrie.insert(movie.getTitle());
                }
            }
        }
    }

    /**
     * Parses genres from a JSON string in the movies CSV.
     * Handles malformed JSON gracefully by returning an empty list.
     *
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
     * Handles malformed JSON gracefully by returning an empty list.
     * Updates the personIndex with the parsed information.
     *
     * @param json JSON string containing cast or crew information
     * @param type Either "cast" or "crew" to indicate the type of people being parsed
     * @param movie The movie these people are associated with
     * @return List of Person objects
     */
    private List<Person> parsePeople(String json, String type, Movie movie) {
        List<Person> people = new ArrayList<>();
        if (json == null || json.isEmpty() || !json.trim().startsWith("[")) {
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
                
                // Only add to personIndex if it's a cast member or a director
                if (type.equals("cast") || (type.equals("crew") && role.equalsIgnoreCase("Director"))) {
                    personIndex.computeIfAbsent(name, k -> new HashSet<>()).add(movie);
                }
                
                people.add(person);
            }
        } catch (Exception e) {
            System.err.println("Error parsing JSON for " + type);
        }
        return people;
    }

    // Add this new method
    private boolean hasValidData(Movie movie) {
        return movie != null && !movie.getCast().isEmpty() && !movie.getCrew().isEmpty();
    }

    // ==================== RANDOM SELECTION METHODS ====================

    /**
     * Gets a random genre from the database.
     * @return A random genre, or null if none exist
     */
    public String getRandomGenre() {
        List<String> genres = getAllGenres();
        if (genres.isEmpty()) return null;
        return genres.get(new java.util.Random().nextInt(genres.size()));
    }

    /**
     * Gets a random actor from the database.
     * @return A random actor name, or null if none exist
     */
    public String getRandomActor() {
        List<String> actors = getAllActors();
        if (actors.isEmpty()) return null;
        return actors.get(new java.util.Random().nextInt(actors.size()));
    }

    /**
     * Gets a random director from the database.
     * @return A random director name, or null if none exist
     */
    public String getRandomDirector() {
        List<String> directors = getAllDirectors();
        if (directors.isEmpty()) return null;
        return directors.get(new java.util.Random().nextInt(directors.size()));
    }

    /**
     * Gets all unique genres in the database.
     * @return List of all unique genres
     */
    public List<String> getAllGenres() {
        return new ArrayList<>(genreIndex.keySet());
    }

    /**
     * Gets all unique actor names in the database.
     * @return List of all unique actor names
     */
    public List<String> getAllActors() {
        Set<String> actors = new HashSet<>();
        for (Movie movie : getAllMovies()) {
            for (Person person : movie.getCast()) {
                actors.add(person.getName());
            }
        }
        return new ArrayList<>(actors);
    }

    /**
     * Gets all unique director names in the database.
     * @return List of all unique director names
     */
    public List<String> getAllDirectors() {
        Set<String> directors = new HashSet<>();
        for (Movie movie : getAllMovies()) {
            for (Person person : movie.getCrew()) {
                if (person.getRole().equalsIgnoreCase("director")) {
                    directors.add(person.getName());
                }
            }
        }
        return new ArrayList<>(directors);
    }
}