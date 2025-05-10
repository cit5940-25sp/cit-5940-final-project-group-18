import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Controls the game flow and manages the game state, including turn management,
 * timer, and input processing.
 * This class coordinates between the game state, movie database, and view
 * components.
 */
public class GameController {
    /** The current state of the game */
    private GameState gameState;

    /** The database containing all available movies */
    private MovieDatabase movieDB;

    /** The view component for displaying game information */
    private GameView view;

    /** Flag indicating whether the game is over */
    private boolean gameOver;

    /** Timer for managing turn duration */
    private Timer timer;

    /** Task that runs on timer ticks */
    private TimerTask timerTask;

    /** Atomic boolean flag to track if the timer is running */
    private AtomicBoolean isTimerRunning = new AtomicBoolean(false);

    /** Object used for synchronization of timer operations */
    private final Object lockObject = new Object();

    /**
     * Constructs a new GameController with the specified game components.
     *
     * @param gameState The game state to manage
     * @param movieDB   The movie database to use
     * @param view      The view component for displaying game information
     */
    public GameController(GameState gameState, MovieDatabase movieDB, GameView view) {
        this.gameState = gameState;
        this.movieDB = movieDB;
        this.view = view;
        this.gameOver = false;
        this.timer = new Timer(true); // Use daemon timer
    }

    /**
     * Starts a new game by initializing the game state and starting the timer.
     * Resets the game over flag and timer, and displays the initial game state.
     */
    public void startGame() {
        gameOver = false;
        gameState.setGameOver(false);
        gameState.setTimer(30);
        startTimer();
        view.displayGameState(gameState);
        view.displayAutocompleteSuggestions(movieDB.getAutocompleteSuggestions("", 5));
    }

    /**
     * Starts or restarts the game timer.
     * The timer counts down from 30 seconds and handles timeout conditions.
     */
    private void startTimer() {
        // Cancel any existing timer task
        stopTimer();

        // Set flag to indicate timer is running
        isTimerRunning.set(true);

        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (!isTimerRunning.get()) {
                    return;
                }

                synchronized (lockObject) {
                    // Decrease timer by 1 second
                    int currentTime = gameState.getTimer();
                    int newTime = currentTime - 1;
                    gameState.setTimer(newTime);
                    
                    // Request view refresh to update timer display
                    view.displayGameState(gameState);

                    // Check if time has run out
                    if (newTime <= 0) {
                        isTimerRunning.set(false);

                        // Handle timeout - current player loses
                        Player currentPlayer = gameState.getCurrentPlayer();
                        gameState.nextPlayer(); // Move to the next player
                        Player winner = gameState.getCurrentPlayer(); // This is the winner

                        // Set game over
                        gameOver = true;
                        gameState.setGameOver(true);

                        // Display game results with timeout flag set to true
                        view.displayGameResults(winner, gameState.getPlayers(), true);

                        // Cancel this timer task
                        cancel();
                    }
                }
            }
        };

        // Schedule timer to run every second
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    /**
     * Stops the current timer and cleans up timer resources.
     */
    private void stopTimer() {
        isTimerRunning.set(false);
        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

    /**
     * Processes a player's input, validating the movie and updating the game state
     * accordingly.
     * Handles various conditions including invalid movies, invalid connections, and
     * win conditions.
     *
     * @param input The movie title input by the player
     */
    public void processInput(String input) {
        synchronized (lockObject) {
            // First check if game is already over
            if (gameOver || gameState.isGameOver()) {
                view.displayError("Game is over!");
                return;
            }

            // Process the movie input
            Movie movie = movieDB.findMovie(input);
            if (movie == null) {
                view.displayError("Movie not found: \"" + input + "\"");
                return;
            }

            boolean valid = gameState.makeMove(movie);
            if (!valid) {
                Movie lastMovie = gameState.getPlayedMovies().get(gameState.getPlayedMovies().size() - 1);
                Connection attemptedConnection = movieDB.validateConnection(lastMovie, movie);
                StringBuilder errorMessage = new StringBuilder("Invalid connection!");

                if (attemptedConnection != null) {
                    errorMessage
                            .append(String.format("\nAttempted connection: %s", attemptedConnection.getDescription()));

                    // Check if this specific connection has been used too many times
                    int usageCount = gameState.getConnectionUsageCount(attemptedConnection);
                    if (usageCount >= 3) {
                        errorMessage.append(String.format(
                                "\nThis specific connection (%s) has been used %d times (maximum 3 times allowed).",
                                attemptedConnection.getDescription(), usageCount));
                    }
                }

                view.displayError(errorMessage.toString());
                return;
            }

            // Only stop timer and proceed with game logic if we have a valid move
            stopTimer();

            // Update view with new game state
            view.displayGameState(gameState);
            view.clearError(); // Flush the error log each round

            // Check for win condition
            Player winner = gameState.checkWinCondition();
            if (winner != null) {
                gameOver = true;
                gameState.setGameOver(true);
                // Display game results with timeout flag set to false (indicating win by
                // condition)
                view.displayGameResults(winner, gameState.getPlayers(), false);
                return;
            }

            // Reset timer for next player
            gameState.setTimer(30);
            startTimer();

            // Update autocomplete suggestions
            view.displayAutocompleteSuggestions(movieDB.getAutocompleteSuggestions("", 5));
        }
    }

    /**
     * Ends the current player's turn and switches to the next player.
     * Resets the timer and updates the game state.
     */
    public void endTurn() {
        synchronized (lockObject) {
            if (gameOver || gameState.isGameOver()) {
                return; // Don't do anything if game is over
            }

            // Stop current timer
            stopTimer();

            // Move to next player
            gameState.nextPlayer();

            // Reset timer for next player
            gameState.setTimer(30);
            startTimer();

            // Update view
            view.displayGameState(gameState);
        }
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver || gameState.isGameOver();
    }
}