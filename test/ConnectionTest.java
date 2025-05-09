import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

/**
 * Test suite for the Connection class in the movie connection game.
 * This class tests the validation and functionality of connections between movies
 * based on shared actors and directors.
 * 
 * The test suite covers:
 * - Valid actor connections between movies
 * - Valid director connections between movies
 * - Invalid connections (non-shared actors/directors)
 * - Invalid connection types
 * 
 * Each test case uses a carefully constructed set of movies and people to
 * verify specific connection scenarios.
 */
public class ConnectionTest {
    /** First movie in test scenarios */
    private Movie movieA;
    
    /** Second movie in test scenarios */
    private Movie movieB;
    
    /** Third movie in test scenarios */
    private Movie movieC;
    
    /** First actor for testing connections */
    private Person actor1;
    
    /** Second actor for testing connections */
    private Person actor2;
    
    /** First director for testing connections */
    private Person director1;
    
    /** Second director for testing connections */
    private Person director2;

    /**
     * Sets up the test environment before each test.
     * Creates a set of movies and people with specific relationships:
     * - movieA: Action movie with actor1 and director1
     * - movieB: Action movie with actor1, actor2, and director1
     * - movieC: Drama movie with actor2 and director2
     * 
     * This setup allows testing various connection scenarios:
     * - Valid connections through shared actors
     * - Valid connections through shared directors
     * - Invalid connections through non-shared people
     */
    @Before
    public void setUp() {
        // Create people
        actor1 = new Person(1, "Actor One", "actor");
        actor2 = new Person(2, "Actor Two", "actor");
        director1 = new Person(3, "Director One", "director");
        director2 = new Person(4, "Director Two", "director");

        // Create movies
        movieA = new Movie(1, "Movie A", 2000, 
            Arrays.asList("Action"), 
            Arrays.asList(actor1), 
            Arrays.asList(director1));
        
        movieB = new Movie(2, "Movie B", 2001, 
            Arrays.asList("Action"), 
            Arrays.asList(actor1, actor2), 
            Arrays.asList(director1));
        
        movieC = new Movie(3, "Movie C", 2002, 
            Arrays.asList("Drama"), 
            Arrays.asList(actor2), 
            Arrays.asList(director2));
    }

    /**
     * Tests a valid connection between movies through a shared actor.
     * Verifies that:
     * - The connection is valid
     * - The source and target movies are correct
     * - The connecting actor is correct
     * - The connection type is "actor"
     */
    @Test
    public void testValidActorConnection() {
        Connection conn = new Connection(movieA, movieB, actor1, "actor");
        assertTrue(conn.isValid());
        assertEquals(movieA, conn.getSourceMovie());
        assertEquals(movieB, conn.getTargetMovie());
        assertEquals(actor1, conn.getConnector());
        assertEquals("actor", conn.getConnectionType());
    }

    /**
     * Tests a valid connection between movies through a shared director.
     * Verifies that:
     * - The connection is valid
     * - The connecting director is correct
     * - The connection type is "director"
     */
    @Test
    public void testValidDirectorConnection() {
        Connection conn = new Connection(movieA, movieB, director1, "director");
        assertTrue(conn.isValid());
        assertEquals(director1, conn.getConnector());
        assertEquals("director", conn.getConnectionType());
    }

    /**
     * Tests an invalid connection between movies through a non-shared actor.
     * Verifies that the connection is marked as invalid when the actor
     * is not present in both movies.
     */
    @Test
    public void testInvalidActorConnection() {
        Connection conn = new Connection(movieA, movieC, actor1, "actor");
        assertFalse(conn.isValid());
    }

    /**
     * Tests an invalid connection between movies through a non-shared director.
     * Verifies that the connection is marked as invalid when the director
     * is not present in both movies.
     */
    @Test
    public void testInvalidDirectorConnection() {
        Connection conn = new Connection(movieA, movieC, director1, "director");
        assertFalse(conn.isValid());
    }

    /**
     * Tests an invalid connection with an unrecognized connection type.
     * Verifies that the connection is marked as invalid when the connection type
     * is not "actor" or "director".
     */
    @Test
    public void testInvalidConnectionType() {
        Connection conn = new Connection(movieA, movieB, actor1, "invalid_type");
        assertFalse(conn.isValid());
    }
}
