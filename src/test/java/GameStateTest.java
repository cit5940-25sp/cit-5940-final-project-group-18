import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

// Dummy win strategy for testing
class DummyWinStrategy implements IWinStrategy {
    @Override public boolean checkWinCondition(List<Movie> playedMovies) { return false; }
    @Override public void updateProgress(Movie movie) {}
    @Override public String getDescription() { return "Dummy Strategy"; }
}

// Dummy player with hasWon method
class TestPlayer extends Player {
    private boolean hasWonFlag = false;
    public TestPlayer(String name, IWinStrategy strategy) {
        super(name, strategy);
    }
    public void setHasWon(boolean flag) { this.hasWonFlag = flag; }
    public boolean hasWon(List<Movie> playedMovies) { return hasWonFlag; }
}

/**
 * Test suite for the GameState class in the movie connection game.
 * This class tests the core game state management functionality, including:
 * - Game initialization and field setup
 * - Movie move validation and processing
 * - Player turn management
 * - Win condition checking
 * - Timer management
 * - Game over state handling
 * - Defensive copying of collections
 * - Round counting
 * - Current movie tracking
 * - Connection tracking
 * 
 * Uses dummy implementations of IWinStrategy and Player for testing
 * specific scenarios without complex win condition logic.
 */
public class GameStateTest {
    /** The movie database used in tests */
    private MovieDatabase movieDB;
    
    /** Test players with controllable win conditions */
    private TestPlayer player1, player2;
    
    /** Test movies with predefined connections */
    private Movie movieA, movieB, movieC;
    
    /** The game state being tested */
    private GameState gameState;

    /**
     * Sets up the test environment before each test.
     * Creates:
     * - Test movies with shared actors and directors
     * - A movie database with the test movies
     * - Test players with dummy win strategies
     * - A game state with the test components
     */
    @Before
    public void setUp() {
        // Create people
        Person actor = new Person(1, "Actor One", "actor");
        Person director = new Person(2, "Director One", "director");

        // Create movies
        movieA = new Movie(1, "Movie A", 2000, Arrays.asList("Action"), Arrays.asList(actor), Arrays.asList(director));
        movieB = new Movie(2, "Movie B", 2001, Arrays.asList("Action"), Arrays.asList(actor), Arrays.asList(director));
        movieC = new Movie(3, "Movie C", 2002, Arrays.asList("Drama"), new ArrayList<>(), new ArrayList<>());

        // Setup MovieDatabase
        movieDB = new MovieDatabase();
        movieDB.addMovie(movieA);
        movieDB.addMovie(movieB);
        movieDB.addMovie(movieC);

        // Players
        player1 = new TestPlayer("Alice", new DummyWinStrategy());
        player2 = new TestPlayer("Bob", new DummyWinStrategy());

        // Game state
        gameState = new GameState(Arrays.asList(player1, player2), movieDB);
    }

    /**
     * Tests proper initialization of game state fields.
     * Verifies:
     * - Round count starts at 0
     * - First player is set correctly
     * - Played movies list is empty
     * - Player list is correctly initialized
     */
    @Test
    public void testConstructorInitializesFields() {
        assertEquals(0, gameState.getRoundCount());
        assertEquals(player1, gameState.getCurrentPlayer());
        assertTrue(gameState.getPlayedMovies().isEmpty());
        assertEquals(2, gameState.getPlayers().size());
    }

    /**
     * Tests processing of valid movie moves.
     * Verifies:
     * - First move is always valid
     * - Second move with connection is valid
     * - Movies are added to played list
     */
    @Test
    public void testMakeMoveValid() {
        boolean firstMove = gameState.makeMove(movieA);
        boolean secondMove = gameState.makeMove(movieB); // Should be valid (same actor/director)
        assertTrue(firstMove);
        assertTrue(secondMove);
        assertEquals(2, gameState.getPlayedMovies().size());
    }

    /**
     * Tests processing of invalid movie moves.
     * Verifies:
     * - First move is valid
     * - Second move without connection is invalid
     * - Only valid moves are added to played list
     */
    @Test
    public void testMakeMoveInvalid() {
        boolean firstMove = gameState.makeMove(movieA);
        boolean secondMove = gameState.makeMove(movieC); // No connection
        assertTrue(firstMove);
        assertFalse(secondMove);
        assertEquals(1, gameState.getPlayedMovies().size());
    }

    /**
     * Tests player turn switching functionality.
     * Verifies:
     * - Players alternate turns
     * - Turn order cycles correctly
     * - Current player updates after each move
     */
    @Test
    public void testPlayerSwitching() {
        Player firstPlayer = gameState.getCurrentPlayer();
        gameState.makeMove(movieA);
        Player secondPlayer = gameState.getCurrentPlayer();
        assertNotEquals(firstPlayer, secondPlayer);
        gameState.makeMove(movieB);
        Player thirdPlayer = gameState.getCurrentPlayer();
        assertEquals(firstPlayer, thirdPlayer); // Should cycle back
    }

    /**
     * Tests win condition checking.
     * Verifies:
     * - Win condition is detected
     * - Correct player is identified as winner
     */
    @Test
    public void testCheckWinCondition() {
        TestPlayer winPlayer = new TestPlayer("Winner", new DummyWinStrategy());
        winPlayer.setHasWon(true);
        GameState state = new GameState(Arrays.asList(winPlayer, player1), movieDB);
        state.makeMove(movieA);
        assertEquals(winPlayer, state.checkWinCondition());
    }

    /**
     * Tests timer management functionality.
     * Verifies:
     * - Timer can be set
     * - Timer decrements correctly
     * - Game over when timer reaches zero
     */
    @Test
    public void testTimerManagement() {
        gameState.setTimer(60);
        assertEquals(60, gameState.getTimer());
        
        gameState.decrementTimer();
        assertEquals(59, gameState.getTimer());
        
        // Test timer expiration
        for (int i = 0; i < 59; i++) {
            gameState.decrementTimer();
        }
        assertTrue(gameState.isGameOver());
    }

    /**
     * Tests game over state handling.
     * Verifies:
     * - Game over state can be set
     * - Moves are blocked when game is over
     * - Played movies list remains unchanged
     */
    @Test
    public void testGameOverState() {
        gameState.setGameOver(true);
        assertTrue(gameState.isGameOver());
        
        // Test that moves are blocked when game is over
        assertFalse(gameState.makeMove(movieA));
        assertEquals(0, gameState.getPlayedMovies().size());
    }

    /**
     * Tests defensive copying of collections.
     * Verifies:
     * - Modifications to returned lists don't affect internal state
     * - All collection getters return defensive copies
     */
    @Test
    public void testDefensiveCopies() {
        // Test that returned lists are defensive copies
        List<Movie> movies = gameState.getPlayedMovies();
        movies.add(movieA); // Should not affect internal state
        assertTrue(gameState.getPlayedMovies().isEmpty());
        
        List<Player> players = gameState.getPlayers();
        players.add(new TestPlayer("New Player", new DummyWinStrategy())); // Should not affect internal state
        assertEquals(2, gameState.getPlayers().size());
        
        List<Connection> connections = gameState.getUsedConnections();
        connections.add(new Connection(movieA, movieB, new Person(1, "Test", "actor"), "actor")); // Should not affect internal state
        assertTrue(gameState.getUsedConnections().isEmpty());
    }

    /**
     * Tests round counting functionality.
     * Verifies:
     * - Round count starts at 0
     * - Round count increments with each move
     */
    @Test
    public void testRoundCount() {
        assertEquals(0, gameState.getRoundCount());
        gameState.makeMove(movieA);
        assertEquals(1, gameState.getRoundCount());
        gameState.makeMove(movieB);
        assertEquals(2, gameState.getRoundCount());
    }

    /**
     * Tests current movie tracking.
     * Verifies:
     * - Current movie is null initially
     * - Current movie updates with each move
     */
    @Test
    public void testCurrentMovieTracking() {
        assertNull(gameState.getCurrentMovie());
        gameState.makeMove(movieA);
        assertEquals(movieA, gameState.getCurrentMovie());
        gameState.makeMove(movieB);
        assertEquals(movieB, gameState.getCurrentMovie());
    }

    /**
     * Tests connection tracking.
     * Verifies:
     * - Connections are recorded for valid moves
     * - Connections are valid
     */
    @Test
    public void testUsedConnections() {
        gameState.makeMove(movieA);
        gameState.makeMove(movieB);
        List<Connection> connections = gameState.getUsedConnections();
        assertEquals(1, connections.size());
        assertTrue(connections.get(0).isValid());
    }

    /**
     * Tests win condition checking with game over state.
     * Verifies:
     * - Win condition is detected
     * - Game over state is set
     * - Subsequent win checks return null
     */
    @Test
    public void testWinConditionWithGameOver() {
        TestPlayer winPlayer = new TestPlayer("Winner", new DummyWinStrategy());
        winPlayer.setHasWon(true);
        GameState state = new GameState(Arrays.asList(winPlayer, player1), movieDB);
        
        state.makeMove(movieA);
        Player winner = state.checkWinCondition();
        assertEquals(winPlayer, winner);
        assertTrue(state.isGameOver());
        
        // Test that subsequent win checks return null
        assertNull(state.checkWinCondition());
    }

    /**
     * Tests handling of empty player list.
     * Verifies:
     * - Current player is null
     * - Player list is empty
     */
    @Test
    public void testEmptyPlayerList() {
        GameState emptyState = new GameState(new ArrayList<>(), movieDB);
        assertNull(emptyState.getCurrentPlayer());
        assertTrue(emptyState.getPlayers().isEmpty());
    }
}