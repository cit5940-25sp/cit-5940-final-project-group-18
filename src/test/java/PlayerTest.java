import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

/**
 * Test suite for the Player class in the movie connection game.
 * This class tests the player's functionality for:
 * - Player initialization and basic properties
 * - Progress tracking and updates
 * - Integration with different win strategies:
 *   - Genre-based win strategy
 *   - Actor-based win strategy
 *   - Director-based win strategy
 * 
 * The tests verify that players can:
 * - Track their progress towards winning
 * - Work with different types of win strategies
 * - Correctly determine win conditions
 */
public class PlayerTest {
    /** The player being tested */
    private Player player;
    
    /** Test movies with predefined attributes */
    private Movie movieA, movieB;
    
    /** Test people (actor and director) for movie connections */
    private Person actor, director;

    /**
     * Sets up the test environment before each test.
     * Creates:
     * - Test people (actor and director)
     * - Test movies with shared people
     * - A test player with a dummy win strategy
     */
    @Before
    public void setUp() {
        actor = new Person(1, "Actor One", "actor");
        director = new Person(2, "Director One", "director");
        
        movieA = new Movie(1, "Movie A", 2000, 
            Arrays.asList("Action"), 
            Arrays.asList(actor), 
            Arrays.asList(director));
        
        movieB = new Movie(2, "Movie B", 2001, 
            Arrays.asList("Action"), 
            Arrays.asList(actor), 
            Arrays.asList(director));

        player = new Player("Test Player", new DummyWinStrategy());
    }

    /**
     * Tests player initialization.
     * Verifies:
     * - Player name is set correctly
     * - Win strategy is initialized
     */
    @Test
    public void testPlayerInitialization() {
        assertEquals("Test Player", player.getName());
        assertNotNull(player.getWinStrategy());
    }

    /**
     * Tests progress updating functionality.
     * Verifies:
     * - Progress can be updated with a movie
     * - Win condition is checked correctly
     * - Dummy strategy behavior
     */
    @Test
    public void testUpdateProgress() {
        List<Movie> playedMovies = new ArrayList<>();
        playedMovies.add(movieA);
        player.updateProgress(movieA);
        assertFalse(player.hasWon(playedMovies)); // Dummy strategy always returns false
    }

    /**
     * Tests integration with GenreWinStrategy.
     * Verifies:
     * - Progress is tracked for genre-based wins
     * - Win condition is met when required number of movies
     *   in the target genre are played
     */
    @Test
    public void testWinStrategyIntegration() {
        // Test with GenreWinStrategy
        Player genrePlayer = new Player("Genre Player", 
            new GenreWinStrategy("Action", 2));
        
        genrePlayer.updateProgress(movieA);
        genrePlayer.updateProgress(movieB);
        
        List<Movie> playedMovies = Arrays.asList(movieA, movieB);
        assertTrue(genrePlayer.hasWon(playedMovies));
    }

    /**
     * Tests integration with ActorWinStrategy.
     * Verifies:
     * - Progress is tracked for actor-based wins
     * - Win condition is met when required number of movies
     *   with the target actor are played
     */
    @Test
    public void testActorWinStrategyIntegration() {
        // Test with ActorWinStrategy
        Player actorPlayer = new Player("Actor Player", 
            new ActorWinStrategy("Actor One", 2));
        
        actorPlayer.updateProgress(movieA);
        actorPlayer.updateProgress(movieB);
        
        List<Movie> playedMovies = Arrays.asList(movieA, movieB);
        assertTrue(actorPlayer.hasWon(playedMovies));
    }

    /**
     * Tests integration with DirectorWinStrategy.
     * Verifies:
     * - Progress is tracked for director-based wins
     * - Win condition is met when required number of movies
     *   by the target director are played
     */
    @Test
    public void testDirectorWinStrategyIntegration() {
        // Test with DirectorWinStrategy
        Player directorPlayer = new Player("Director Player", 
            new DirectorWinStrategy("Director One", 2));
        
        directorPlayer.updateProgress(movieA);
        directorPlayer.updateProgress(movieB);
        
        List<Movie> playedMovies = Arrays.asList(movieA, movieB);
        assertTrue(directorPlayer.hasWon(playedMovies));
    }
}