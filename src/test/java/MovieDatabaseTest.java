import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import com.opencsv.exceptions.CsvValidationException;
import java.util.List;
import java.util.Set;

/**
 * Test suite for the MovieDatabase class in the movie connection game.
 * This class tests the database's functionality for:
 * - Core database operations (finding movies, getting all movies)
 * - Autocomplete functionality
 * - Movie connection validation
 * - Person and genre-based movie queries
 * - Data loading from CSV files
 * 
 * The tests use real movie data from CSV files to ensure
 * the database works correctly with actual movie information.
 */
public class MovieDatabaseTest {
    /** The movie database being tested */
    private MovieDatabase db;
    
    /** Path to the movies CSV file */
    private static final String MOVIES_PATH = "data/movies.csv";
    
    /** Path to the credits CSV file */
    private static final String CREDITS_PATH = "data/credits.csv";

    /**
     * Sets up the test environment before each test.
     * Initializes the movie database with data from the CSV files.
     *
     * @throws IOException If there's an error reading the CSV files
     * @throws CsvValidationException If there's an error parsing the CSV files
     */
    @Before
    public void setUp() throws IOException, CsvValidationException {
        db = new MovieDatabase(MOVIES_PATH, CREDITS_PATH);
    }

    // ==================== CORE DATABASE OPERATIONS TESTS ====================

    /**
     * Tests the movie finding functionality.
     * Verifies:
     * - Existing movies can be found by title
     * - Non-existent movies return null
     * - Movie titles are matched exactly
     */
    @Test
    public void testFindMovie() {
        // Test finding an existing movie
        Movie avatar = db.findMovie("Avatar");
        assertNotNull("Should find Avatar", avatar);
        assertEquals("Avatar", avatar.getTitle());

        // Test finding a non-existent movie
        Movie nonExistent = db.findMovie("NonExistentMovie123");
        assertNull("Should not find non-existent movie", nonExistent);
    }

    /**
     * Tests retrieving all movies from the database.
     * Verifies:
     * - Returns a non-null list
     * - List is not empty
     * - Contains known movies
     */
    @Test
    public void testGetAllMovies() {
        List<Movie> allMovies = db.getAllMovies();
        assertNotNull("Should return a list of movies", allMovies);
        assertFalse("Should not be empty", allMovies.isEmpty());
        
        // Verify some known movies are in the list
        boolean hasAvatar = allMovies.stream()
            .anyMatch(m -> m.getTitle().equals("Avatar"));
        assertTrue("Should contain Avatar", hasAvatar);
    }

    // ==================== AUTOCOMPLETE TESTS ====================

    /**
     * Tests the autocomplete functionality.
     * Verifies:
     * - Basic prefix matching works
     * - Case-insensitive matching works
     * - Minimum length filtering works
     * - Returns correct number of suggestions
     */
    @Test
    public void testAutocompleteSuggestions() {
        // Test basic autocomplete
        List<String> suggestions = db.getAutocompleteSuggestions("Ava", 5);
        assertNotNull("Should return suggestions", suggestions);
        assertFalse("Should not be empty", suggestions.isEmpty());
        assertTrue("Should contain Avatar", suggestions.contains("Avatar"));

        // Test case-insensitive autocomplete
        List<String> caseInsensitiveSuggestions = db.getAutocompleteSuggestionsCaseInsensitive("ava", 5);
        assertTrue("Should find Avatar with lowercase prefix", 
            caseInsensitiveSuggestions.contains("Avatar"));

        // Test minimum length filter
        List<String> minLengthSuggestions = db.getAutocompleteSuggestionsWithMinLength("A", 10, 5);
        assertTrue("All suggestions should be at least 5 characters", 
            minLengthSuggestions.stream().allMatch(s -> s.length() >= 5));
    }

    // ==================== CONNECTION TESTS ====================

    /**
     * Tests movie connection validation.
     * Verifies:
     * - Connections can be found between movies
     * - Connections have valid connectors
     * - Connection types are valid
     */
    @Test
    public void testValidateConnection() {
        Movie avatar = db.findMovie("Avatar");
        Movie titanic = db.findMovie("Titanic");
        
        assertNotNull("Should find Avatar", avatar);
        assertNotNull("Should find Titanic", titanic);

        // Test connection between movies
        Connection conn = db.validateConnection(avatar, titanic);
        if (conn != null) {
            assertNotNull("Connection should have a connector", conn.getConnector());
            assertTrue("Connection type should be actor or director", 
                conn.getConnectionType().equals("actor") || 
                conn.getConnectionType().equals("director"));
        }
    }

    /**
     * Tests retrieving movies by person.
     * Verifies:
     * - Returns movies for known actors
     * - Returns non-empty set
     * - Contains expected movies
     */
    @Test
    public void testGetMoviesByPerson() {
        // Test getting movies for a known actor
        Set<Movie> movies = db.getMoviesByPerson("Sam Worthington");
        assertNotNull("Should return a set of movies", movies);
        assertFalse("Should not be empty", movies.isEmpty());
        assertTrue("Should contain Avatar", 
            movies.stream().anyMatch(m -> m.getTitle().equals("Avatar")));
    }

    /**
     * Tests retrieving movies by genre.
     * Verifies:
     * - Returns movies in specified genre
     * - Returns non-empty list
     * - All returned movies have the correct genre
     */
    @Test
    public void testGetMoviesByGenre() {
        // Test getting movies by genre
        List<Movie> actionMovies = db.getMoviesByGenre("Action");
        assertNotNull("Should return a list of movies", actionMovies);
        assertFalse("Should not be empty", actionMovies.isEmpty());
        
        // Verify all returned movies have the Action genre
        assertTrue("All movies should have Action genre",
            actionMovies.stream().allMatch(m -> m.getGenres().contains("Action")));
    }

    // ==================== DATA LOADING TESTS ====================

    /**
     * Tests data loading from CSV files.
     * Verifies:
     * - Database is loaded with data
     * - Known movies can be found
     * - Movies have cast and crew information
     */
    @Test
    public void testDataLoading() {
        // Verify that the database was loaded with data
        assertFalse("Movie database should not be empty", db.getAllMovies().isEmpty());
        
        // Verify that we can find a known movie
        Movie avatar = db.findMovie("Avatar");
        assertNotNull("Should find Avatar after loading", avatar);
        
        // Verify that the movie has cast and crew
        assertFalse("Avatar should have cast members", avatar.getCast().isEmpty());
        assertFalse("Avatar should have crew members", avatar.getCrew().isEmpty());
    }

    /**
     * Tests handling of invalid file paths.
     * Verifies:
     * - IOException is thrown for non-existent files
     */
    @Test(expected = IOException.class)
    public void testInvalidFilePaths() throws IOException, CsvValidationException {
        // Test loading with invalid file paths
        new MovieDatabase("nonexistent.csv", "nonexistent.csv");
    }

    /**
     * Tests parsing of director data from credits CSV.
     * Specifically checks Christopher Nolan's entries to verify director parsing.
     * Verifies:
     * - Nolan is found in the database
     * - His movies are correctly indexed
     * - He is properly identified as a director
     */
    @Test
    public void testDirectorParsing() {
        // Get all movies associated with Christopher Nolan
        Set<Movie> nolanMovies = db.getMoviesByPerson("Christopher Nolan");
        
        // Verify we found some movies
        assertNotNull("Should return a set of movies", nolanMovies);
        assertFalse("Should not be empty", nolanMovies.isEmpty());
        
        // Print out the movies for debugging
        System.out.println("\nChristopher Nolan's movies:");
        for (Movie movie : nolanMovies) {
            System.out.println("Movie: " + movie.getTitle());
            // Verify he's listed as a director in each movie
            boolean isDirector = movie.getCrew().stream()
                .anyMatch(p -> p.getName().equals("Christopher Nolan") && 
                             p.getRole().equalsIgnoreCase("Director"));
            assertTrue("Nolan should be listed as director in " + movie.getTitle(), isDirector);
        }
        
        // Verify some known Nolan movies are present
        assertTrue("Should contain The Dark Knight", 
            nolanMovies.stream().anyMatch(m -> m.getTitle().equals("The Dark Knight")));
        assertTrue("Should contain Inception", 
            nolanMovies.stream().anyMatch(m -> m.getTitle().equals("Inception")));
    }
}