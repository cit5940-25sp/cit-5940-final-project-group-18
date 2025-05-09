import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * JUnit tests for the win strategies implemented using the Strategy pattern.
 * This tests all three types of win strategies: Genre, Actor, and Director.
 */
public class WinStrategyTest {
    
    // Test data
    private Movie inceptionMovie;
    private Movie darkKnightMovie;
    private Movie titanicMovie;
    private List<Movie> emptyMovieList;
    private List<Movie> singleMovieList;
    private List<Movie> multiMovieList;
    
    @Before
    public void setUp() {
        // Set up test movies
        
        // Inception - Christopher Nolan, Leonardo DiCaprio, Sci-Fi
        List<Person> inceptionDirectors = new ArrayList<>();
        inceptionDirectors.add(new Person(1, "Christopher Nolan", "director"));
        
        List<Person> inceptionActors = new ArrayList<>();
        inceptionActors.add(new Person(2, "Leonardo DiCaprio", "actor"));
        inceptionActors.add(new Person(3, "Ellen Page", "actor"));
        
        List<String> inceptionGenres = Arrays.asList("Sci-Fi", "Action", "Thriller");
        
        inceptionMovie = new Movie(
            1, 
            "Inception", 
            2010, 
            inceptionGenres,
            inceptionActors,
            inceptionDirectors
        );
        
        // The Dark Knight - Christopher Nolan, Christian Bale, Action
        List<Person> darkKnightDirectors = new ArrayList<>();
        darkKnightDirectors.add(new Person(1, "Christopher Nolan", "director"));
        
        List<Person> darkKnightActors = new ArrayList<>();
        darkKnightActors.add(new Person(4, "Christian Bale", "actor"));
        darkKnightActors.add(new Person(5, "Heath Ledger", "actor"));
        
        List<String> darkKnightGenres = Arrays.asList("Action", "Crime", "Drama");
        
        darkKnightMovie = new Movie(
            2, 
            "The Dark Knight", 
            2008, 
            darkKnightGenres,
            darkKnightActors,
            darkKnightDirectors
        );
        
        // Titanic - James Cameron, Leonardo DiCaprio, Romance
        List<Person> titanicDirectors = new ArrayList<>();
        titanicDirectors.add(new Person(6, "James Cameron", "director"));
        
        List<Person> titanicActors = new ArrayList<>();
        titanicActors.add(new Person(2, "Leonardo DiCaprio", "actor"));
        titanicActors.add(new Person(7, "Kate Winslet", "actor"));
        
        List<String> titanicGenres = Arrays.asList("Romance", "Drama", "Disaster");
        
        titanicMovie = new Movie(
            3, 
            "Titanic", 
            1997, 
            titanicGenres,
            titanicActors,
            titanicDirectors
        );
        
        // Create movie lists for testing
        emptyMovieList = new ArrayList<>();
        
        singleMovieList = new ArrayList<>();
        singleMovieList.add(inceptionMovie);
        
        multiMovieList = new ArrayList<>();
        multiMovieList.add(inceptionMovie);
        multiMovieList.add(darkKnightMovie);
        multiMovieList.add(titanicMovie);
    }
    
    // ==================== GenreWinStrategy Tests ====================
    
    @Test
    public void testGenreWinStrategy_SingleMovieMatch() {
        GenreWinStrategy strategy = new GenreWinStrategy("Sci-Fi", 1);
        strategy.updateProgress(inceptionMovie);
        
        assertEquals(1, strategy.getCurrentCount());
        assertEquals(100, (int)(((double)strategy.getCurrentCount() / strategy.getRequiredCount()) * 100));
        assertTrue(strategy.checkWinCondition(singleMovieList));
    }
    
    @Test
    public void testGenreWinStrategy_NoMatch() {
        GenreWinStrategy strategy = new GenreWinStrategy("Horror", 1);
        strategy.updateProgress(inceptionMovie);
        
        assertEquals(0, strategy.getCurrentCount());
        assertFalse(strategy.checkWinCondition(singleMovieList));
    }
    
    @Test
    public void testGenreWinStrategy_MultipleMoviesRequired() {
        GenreWinStrategy strategy = new GenreWinStrategy("Action", 2);
        
        strategy.updateProgress(inceptionMovie);  // Has "Action" genre
        assertEquals(1, strategy.getCurrentCount());
        assertEquals(50, (int)(((double)strategy.getCurrentCount() / strategy.getRequiredCount()) * 100));
        assertFalse(strategy.checkWinCondition(singleMovieList));
        
        strategy.updateProgress(darkKnightMovie);  // Also has "Action" genre
        assertEquals(2, strategy.getCurrentCount());
        assertEquals(100, (int)(((double)strategy.getCurrentCount() / strategy.getRequiredCount()) * 100));
        assertTrue(strategy.checkWinCondition(multiMovieList));
    }
    
    // ==================== ActorWinStrategy Tests ====================
    
    @Test
    public void testActorWinStrategy_SingleMovieMatch() {
        ActorWinStrategy strategy = new ActorWinStrategy("Leonardo DiCaprio", 1);
        strategy.updateProgress(inceptionMovie);
        
        assertEquals(1, strategy.getCurrentCount());
        assertTrue(strategy.checkWinCondition(singleMovieList));
    }
    
    @Test
    public void testActorWinStrategy_NoMatch() {
        ActorWinStrategy strategy = new ActorWinStrategy("Brad Pitt", 1);
        strategy.updateProgress(inceptionMovie);
        
        assertEquals(0, strategy.getCurrentCount());
        assertFalse(strategy.checkWinCondition(singleMovieList));
    }
    
    @Test
    public void testActorWinStrategy_MultipleMoviesRequired() {
        ActorWinStrategy strategy = new ActorWinStrategy("Leonardo DiCaprio", 2);
        
        strategy.updateProgress(inceptionMovie);  // Has DiCaprio
        assertEquals(1, strategy.getCurrentCount());
        assertEquals(50, (int)(((double)strategy.getCurrentCount() / strategy.getRequiredCount()) * 100));
        assertFalse(strategy.checkWinCondition(singleMovieList));
        
        strategy.updateProgress(titanicMovie);  // Also has DiCaprio
        assertEquals(2, strategy.getCurrentCount());
        assertEquals(100, (int)(((double)strategy.getCurrentCount() / strategy.getRequiredCount()) * 100));
        assertTrue(strategy.checkWinCondition(multiMovieList));
    }
    
    // ==================== DirectorWinStrategy Tests ====================
    
    @Test
    public void testDirectorWinStrategy_SingleMovieMatch() {
        DirectorWinStrategy strategy = new DirectorWinStrategy("Christopher Nolan", 1);
        strategy.updateProgress(inceptionMovie);
        
        assertEquals(1, strategy.getCurrentCount());
        assertTrue(strategy.checkWinCondition(singleMovieList));
    }
    
    @Test
    public void testDirectorWinStrategy_NoMatch() {
        DirectorWinStrategy strategy = new DirectorWinStrategy("Steven Spielberg", 1);
        strategy.updateProgress(inceptionMovie);
        
        assertEquals(0, strategy.getCurrentCount());
        assertFalse(strategy.checkWinCondition(singleMovieList));
    }
    
    @Test
    public void testDirectorWinStrategy_MultipleMoviesRequired() {
        DirectorWinStrategy strategy = new DirectorWinStrategy("Christopher Nolan", 2);
        
        strategy.updateProgress(inceptionMovie);  // Directed by Nolan
        assertEquals(1, strategy.getCurrentCount());
        assertEquals(50, (int)(((double)strategy.getCurrentCount() / strategy.getRequiredCount()) * 100));
        assertFalse(strategy.checkWinCondition(singleMovieList));
        
        strategy.updateProgress(darkKnightMovie);  // Also directed by Nolan
        assertEquals(2, strategy.getCurrentCount());
        assertEquals(100, (int)(((double)strategy.getCurrentCount() / strategy.getRequiredCount()) * 100));
        assertTrue(strategy.checkWinCondition(multiMovieList));
    }
    
    // ==================== WinStrategyFactory Tests ====================
    
    @Test
    public void testWinStrategyFactory_CreateGenreStrategy() {
        IWinStrategy strategy = WinStrategyFactory.createStrategy(
            WinStrategyFactory.GENRE_STRATEGY, "Action", 2);
        
        assertTrue(strategy instanceof GenreWinStrategy);
        GenreWinStrategy genreStrategy = (GenreWinStrategy) strategy;
        
        assertEquals("Action", genreStrategy.getTargetGenre());
        assertEquals(2, genreStrategy.getRequiredCount());
    }
    
    @Test
    public void testWinStrategyFactory_CreateActorStrategy() {
        IWinStrategy strategy = WinStrategyFactory.createStrategy(
            WinStrategyFactory.ACTOR_STRATEGY, "Leonardo DiCaprio", 2);
        
        assertTrue(strategy instanceof ActorWinStrategy);
        ActorWinStrategy actorStrategy = (ActorWinStrategy) strategy;
        
        assertEquals("Leonardo DiCaprio", actorStrategy.getTargetActor());
        assertEquals(2, actorStrategy.getRequiredCount());
    }
    
    @Test
    public void testWinStrategyFactory_CreateDirectorStrategy() {
        IWinStrategy strategy = WinStrategyFactory.createStrategy(
            WinStrategyFactory.DIRECTOR_STRATEGY, "Christopher Nolan", 2);
        
        assertTrue(strategy instanceof DirectorWinStrategy);
        DirectorWinStrategy directorStrategy = (DirectorWinStrategy) strategy;
        
        assertEquals("Christopher Nolan", directorStrategy.getTargetDirector());
        assertEquals(2, directorStrategy.getRequiredCount());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWinStrategyFactory_InvalidStrategyType() {
        WinStrategyFactory.createStrategy("invalid_type", "target", 1);
    }
    
    // ==================== Integration with Player Tests ====================
    
    @Test
    public void testPlayerWithStrategy() {
        // Create a player with a genre strategy
        IWinStrategy strategy = new GenreWinStrategy("Sci-Fi", 1);
        Player player = new Player("Test Player", strategy);
        
        // Update progress and check win condition
        player.updateProgress(inceptionMovie);
        
        assertEquals(100, player.getProgress());
        assertTrue(player.hasWon(singleMovieList));
    }
} 