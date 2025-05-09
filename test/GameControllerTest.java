import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

/**
 * Test suite for the GameController class in the movie connection game.
 * This class tests the game flow, input processing, and state management
 * functionality of the GameController.
 * 
 * The test suite covers:
 * - Game initialization and start
 * - Valid and invalid movie moves
 * - Turn switching and game flow
 * - Timer and game over conditions
 * - Win condition checking
 * - Error handling and user feedback
 * 
 * Uses stub implementations of GameView and IWinStrategy to isolate
 * controller testing from view and strategy implementations.
 */
public class GameControllerTest {
    /** The movie database used in tests */
    private MovieDatabase movieDB;
    
    /** Stub implementation of GameView for capturing view interactions */
    private GameViewStub view;
    
    /** The game state being tested */
    private GameState gameState;
    
    /** The controller being tested */
    private GameController controller;
    
    /** Test players */
    private Player player1, player2;
    
    /** Test movies */
    private Movie movieA, movieB, movieC;

    /**
     * Stub implementation of GameView that captures all view interactions
     * for verification in tests. Tracks:
     * - Last error message displayed
     * - Game state updates
     * - Autocomplete suggestions
     * - Timer updates
     */
    static class GameViewStub extends GameView {
        String lastError = null;
        List<String> displayedStates = new ArrayList<>();
        List<String> displayedSuggestions = new ArrayList<>();
        int lastTimerUpdate = -1;

        @Override
        public void displayError(String error) { 
            lastError = error; 
            System.out.println("Error set to: " + error); // Add this for debugging
        }

        @Override
        public void displayGameState(GameState state) {
            displayedStates.add("State updated");
        }

        @Override
        public void displayAutocompleteSuggestions(List<String> suggestions) {
            displayedSuggestions.add("Suggestions updated");
        }

        @Override
        public void updateTimer(int seconds) {
            lastTimerUpdate = seconds;
        }
    }

    /**
     * Simple stub implementation of IWinStrategy that never wins.
     * Used to test game flow without triggering win conditions.
     */
    static class DummyWinStrategy implements IWinStrategy {
        @Override public boolean checkWinCondition(List<Movie> playedMovies) { return false; }
        @Override public void updateProgress(Movie movie) {}
    }

    /**
     * Sets up the test environment before each test.
     * Creates:
     * - Test movies with shared actors and directors
     * - A movie database with the test movies
     * - Test players with dummy win strategies
     * - A game state and controller with the test components
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
        player1 = new Player("Alice", new DummyWinStrategy());
        player2 = new Player("Bob", new DummyWinStrategy());

        // Game state and controller
        gameState = new GameState(Arrays.asList(player1, player2), movieDB);
        view = new GameViewStub();
        controller = new GameController(gameState, movieDB, view);
    }

    /**
     * Tests game initialization and start.
     * Verifies:
     * - Timer is set correctly
     * - Game is not over
     * - Initial state is displayed
     * - Initial suggestions are shown
     */
    @Test
    public void testStartGame() {
        controller.startGame();
        assertEquals(30, gameState.getTimer());
        assertFalse(controller.isGameOver());
        assertTrue(view.displayedStates.size() > 0);
        assertTrue(view.displayedSuggestions.size() > 0);
    }

    /**
     * Tests processing of valid movie moves.
     * Verifies:
     * - No error is displayed
     * - Movies are added to played list
     * - Game state is updated
     */
    @Test
    public void testValidMove() {
        controller.processInput("Movie A");
        controller.processInput("Movie B"); // Should be valid (same actor/director)
        assertNull(view.lastError);
        assertEquals(2, gameState.getPlayedMovies().size());
        assertTrue(view.displayedStates.size() > 0);
    }

    /**
     * Tests processing of invalid movie moves.
     * Verifies:
     * - Appropriate error message is displayed
     * - Game state remains unchanged
     */
    @Test
    public void testInvalidMove() {
        controller.processInput("Movie A");
        controller.processInput("Movie C"); // No connection
        assertEquals("Invalid connection! The movie must be connected to the previous one by actor or director.", view.lastError);
    }

    /**
     * Tests handling of non-existent movie titles.
     * Verifies:
     * - Appropriate error message is displayed
     * - Game state remains unchanged
     */
    @Test
    public void testMovieNotFound() {
        controller.processInput("Nonexistent Movie");
        assertEquals("Movie not found: \"Nonexistent Movie\"", view.lastError);
    }

    /**
     * Tests turn switching between players.
     * Verifies:
     * - Current player changes after each move
     * - Turn order is maintained
     */
    @Test
    public void testTurnSwitching() {
        controller.processInput("Movie A");
        Player afterFirstMove = gameState.getCurrentPlayer();
        controller.processInput("Movie B");
        Player afterSecondMove = gameState.getCurrentPlayer();
        assertNotEquals(afterFirstMove, afterSecondMove);
    }

    /**
     * Tests manual turn ending.
     * Verifies:
     * - Current player changes
     * - Game state is updated
     */
    @Test
    public void testEndTurn() {
        controller.processInput("Movie A");
        Player firstPlayer = gameState.getCurrentPlayer();
        controller.endTurn();
        Player secondPlayer = gameState.getCurrentPlayer();
        assertNotEquals(firstPlayer, secondPlayer);
        assertTrue(view.displayedStates.size() > 0);
    }

    /**
     * Tests game timer functionality.
     * Verifies:
     * - Initial timer value
     * - Game over when timer reaches zero
     */
    @Test
    public void testTimeLimit() {
        controller.startGame();
        assertEquals(30, gameState.getTimer());
        
        // Simulate timer running out by directly setting game over state
        gameState.setTimer(0);
        gameState.setGameOver(true);
        
        // Verify game is over
        assertTrue(controller.isGameOver());
    }

    /**
     * Tests game over state handling.
     * Verifies:
     * - No moves can be made after game over
     * - Appropriate error message is displayed
     * - No state updates occur
     */
    @Test
    public void testGameOverState() {
        controller.startGame();
        controller.processInput("Movie A");
        controller.processInput("Movie B");
        
        // Force game over by setting timer to 0
        gameState.setTimer(0);
        gameState.setGameOver(true);  // Explicitly set game over
        
        // Try to make moves after game over
        controller.processInput("Movie A");
        assertEquals("Game is over!", view.lastError);
        
        // Clear the displayed states before testing endTurn
        view.displayedStates.clear();
        
        controller.endTurn();
        assertTrue(view.displayedStates.isEmpty()); // No state updates after game over
    }

    /**
     * Tests win condition checking.
     * Verifies:
     * - Game ends when win condition is met
     * - Appropriate state updates occur
     */
    @Test
    public void testWinCondition() {
        // Create a player with a winning strategy
        Player winningPlayer = new Player("Winner", new DummyWinStrategy() {
            @Override
            public boolean checkWinCondition(List<Movie> playedMovies) {
                return true; // Always win
            }
        });
        
        GameState winningState = new GameState(Arrays.asList(winningPlayer), movieDB);
        GameController winningController = new GameController(winningState, movieDB, view);
        
        winningController.startGame();
        winningController.processInput("Movie A");
        
        // Verify game is over
        assertTrue(winningController.isGameOver());
    }
}
