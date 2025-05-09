import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

public class PlayerTest {
    private Player player;
    private Movie movieA, movieB;
    private Person actor, director;

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

    @Test
    public void testPlayerInitialization() {
        assertEquals("Test Player", player.getName());
        assertNotNull(player.getWinStrategy());
    }

    @Test
    public void testUpdateProgress() {
        List<Movie> playedMovies = new ArrayList<>();
        playedMovies.add(movieA);
        player.updateProgress(movieA);
        assertFalse(player.hasWon(playedMovies)); // Dummy strategy always returns false
    }

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
