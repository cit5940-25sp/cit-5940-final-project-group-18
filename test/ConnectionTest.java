import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

public class ConnectionTest {
    private Movie movieA, movieB, movieC;
    private Person actor1, actor2, director1, director2;

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

    @Test
    public void testValidActorConnection() {
        Connection conn = new Connection(movieA, movieB, actor1, "actor");
        assertTrue(conn.isValid());
        assertEquals(movieA, conn.getSourceMovie());
        assertEquals(movieB, conn.getTargetMovie());
        assertEquals(actor1, conn.getConnector());
        assertEquals("actor", conn.getConnectionType());
    }

    @Test
    public void testValidDirectorConnection() {
        Connection conn = new Connection(movieA, movieB, director1, "director");
        assertTrue(conn.isValid());
        assertEquals(director1, conn.getConnector());
        assertEquals("director", conn.getConnectionType());
    }

    @Test
    public void testInvalidActorConnection() {
        Connection conn = new Connection(movieA, movieC, actor1, "actor");
        assertFalse(conn.isValid());
    }

    @Test
    public void testInvalidDirectorConnection() {
        Connection conn = new Connection(movieA, movieC, director1, "director");
        assertFalse(conn.isValid());
    }

    @Test
    public void testInvalidConnectionType() {
        Connection conn = new Connection(movieA, movieB, actor1, "invalid_type");
        assertFalse(conn.isValid());
    }
}
