import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.IOException;
import com.opencsv.exceptions.CsvValidationException;
import java.util.List;
import java.util.Set;

public class MovieDatabaseTest {
    private MovieDatabase db;
    private static final String MOVIES_PATH = "data/movies.csv";
    private static final String CREDITS_PATH = "data/credits.csv";

    @Before
    public void setUp() throws IOException, CsvValidationException {
        db = new MovieDatabase(MOVIES_PATH, CREDITS_PATH);
    }

    // ==================== CORE DATABASE OPERATIONS TESTS ====================

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

    @Test
    public void testGetMoviesByPerson() {
        // Test getting movies for a known actor
        Set<Movie> movies = db.getMoviesByPerson("Sam Worthington");
        assertNotNull("Should return a set of movies", movies);
        assertFalse("Should not be empty", movies.isEmpty());
        assertTrue("Should contain Avatar", 
            movies.stream().anyMatch(m -> m.getTitle().equals("Avatar")));
    }

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

    @Test(expected = IOException.class)
    public void testInvalidFilePaths() throws IOException, CsvValidationException {
        // Test loading with invalid file paths
        new MovieDatabase("nonexistent.csv", "nonexistent.csv");
    }
}
