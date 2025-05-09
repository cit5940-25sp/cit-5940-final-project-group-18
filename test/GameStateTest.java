import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

// Dummy win strategy for testing
class DummyWinStrategy implements IWinStrategy {
    @Override public boolean checkWinCondition(List<Movie> playedMovies) { return false; }
    @Override public void updateProgress(Movie movie) {}
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

public class GameStateTest {
    private MovieDatabase movieDB;
    private TestPlayer player1, player2;
    private Movie movieA, movieB, movieC;
    private GameState gameState;

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

    @Test
    public void testConstructorInitializesFields() {
        assertEquals(0, gameState.getRoundCount());
        assertEquals(player1, gameState.getCurrentPlayer());
        assertTrue(gameState.getPlayedMovies().isEmpty());
        assertEquals(2, gameState.getPlayers().size());
        // movieDB is private, so we can't check directly, but we know it's used in makeMove
    }

    @Test
    public void testMakeMoveValid() {
        boolean firstMove = gameState.makeMove(movieA);
        boolean secondMove = gameState.makeMove(movieB); // Should be valid (same actor/director)
        assertTrue(firstMove);
        assertTrue(secondMove);
        assertEquals(2, gameState.getPlayedMovies().size());
    }

    @Test
    public void testMakeMoveInvalid() {
        boolean firstMove = gameState.makeMove(movieA);
        boolean secondMove = gameState.makeMove(movieC); // No connection
        assertTrue(firstMove);
        assertFalse(secondMove);
        assertEquals(1, gameState.getPlayedMovies().size());
    }

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

    @Test
    public void testCheckWinCondition() {
        TestPlayer winPlayer = new TestPlayer("Winner", new DummyWinStrategy());
        winPlayer.setHasWon(true);
        GameState state = new GameState(Arrays.asList(winPlayer, player1), movieDB);
        state.makeMove(movieA);
        assertEquals(winPlayer, state.checkWinCondition());
    }

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

    @Test
    public void testGameOverState() {
        gameState.setGameOver(true);
        assertTrue(gameState.isGameOver());
        
        // Test that moves are blocked when game is over
        assertFalse(gameState.makeMove(movieA));
        assertEquals(0, gameState.getPlayedMovies().size());
    }

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

    @Test
    public void testRoundCount() {
        assertEquals(0, gameState.getRoundCount());
        gameState.makeMove(movieA);
        assertEquals(1, gameState.getRoundCount());
        gameState.makeMove(movieB);
        assertEquals(2, gameState.getRoundCount());
    }

    @Test
    public void testCurrentMovieTracking() {
        assertNull(gameState.getCurrentMovie());
        gameState.makeMove(movieA);
        assertEquals(movieA, gameState.getCurrentMovie());
        gameState.makeMove(movieB);
        assertEquals(movieB, gameState.getCurrentMovie());
    }

    @Test
    public void testUsedConnections() {
        gameState.makeMove(movieA);
        gameState.makeMove(movieB);
        List<Connection> connections = gameState.getUsedConnections();
        assertEquals(1, connections.size());
        assertTrue(connections.get(0).isValid());
    }

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

    @Test
    public void testEmptyPlayerList() {
        GameState emptyState = new GameState(new ArrayList<>(), movieDB);
        assertNull(emptyState.getCurrentPlayer());
        assertTrue(emptyState.getPlayers().isEmpty());
    }
}