import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;

public class GameControllerTest {
    private MovieDatabase movieDB;
    private GameViewStub view;
    private GameState gameState;
    private GameController controller;
    private Player player1, player2;
    private Movie movieA, movieB, movieC;

    // Enhanced stub for GameView to capture all interactions
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

    // Simple stub for IWinStrategy
    static class DummyWinStrategy implements IWinStrategy {
        @Override public boolean checkWinCondition(List<Movie> playedMovies) { return false; }
        @Override public void updateProgress(Movie movie) {}
    }

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

    @Test
    public void testStartGame() {
        controller.startGame();
        assertEquals(60, gameState.getTimer());
        assertFalse(controller.isGameOver());
        assertTrue(view.displayedStates.size() > 0);
        assertTrue(view.displayedSuggestions.size() > 0);
    }

    @Test
    public void testValidMove() {
        controller.processInput("Movie A");
        controller.processInput("Movie B"); // Should be valid (same actor/director)
        assertNull(view.lastError);
        assertEquals(2, gameState.getPlayedMovies().size());
        assertTrue(view.displayedStates.size() > 0);
    }

    @Test
    public void testInvalidMove() {
        controller.processInput("Movie A");
        controller.processInput("Movie C"); // No connection
        assertEquals("Invalid connection! The movie must be connected to the previous one by actor or director.", view.lastError);
    }

    @Test
    public void testMovieNotFound() {
        controller.processInput("Nonexistent Movie");
        assertEquals("Movie not found!", view.lastError);
    }

    @Test
    public void testTurnSwitching() {
        controller.processInput("Movie A");
        Player afterFirstMove = gameState.getCurrentPlayer();
        controller.processInput("Movie B");
        Player afterSecondMove = gameState.getCurrentPlayer();
        assertNotEquals(afterFirstMove, afterSecondMove);
    }

    @Test
    public void testEndTurn() {
        controller.processInput("Movie A");
        Player firstPlayer = gameState.getCurrentPlayer();
        controller.endTurn();
        Player secondPlayer = gameState.getCurrentPlayer();
        assertNotEquals(firstPlayer, secondPlayer);
        assertTrue(view.displayedStates.size() > 0);
    }

    @Test
    public void testTimeLimit() {
        controller.startGame();
        assertEquals(60, gameState.getTimer());
        
        // Simulate time passing
        for (int i = 0; i < 59; i++) {  // Only decrement 59 times
            controller.checkTimeLimit();
            assertEquals(59 - i, gameState.getTimer());  // Verify timer is decrementing
        }
        
        // One final check that should trigger game over
        controller.checkTimeLimit();
        
        assertTrue(controller.isGameOver());
        assertEquals("Time's up! Game Over!", view.lastError);
    }

    @Test
    public void testGameOverState() {
        controller.startGame();
        controller.processInput("Movie A");
        controller.processInput("Movie B");
        
        // Force game over by setting timer to 0
        gameState.setTimer(0);
        controller.checkTimeLimit();
        
        // Try to make moves after game over
        controller.processInput("Movie A");
        assertEquals("Game is over!", view.lastError);
        
        // Clear the displayed states before testing endTurn
        view.displayedStates.clear();
        
        controller.endTurn();
        assertTrue(view.displayedStates.isEmpty()); // No state updates after game over
    }

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
        
        assertTrue(winningController.isGameOver());
        assertTrue(view.lastError.contains("has won!"));
    }
}
