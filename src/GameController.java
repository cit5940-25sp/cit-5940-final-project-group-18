import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class GameController {
    private GameState gameState;
    private MovieDatabase movieDB;
    private GameView view;
    private boolean gameOver;
    private Timer timer;
    private TimerTask timerTask;
    private AtomicBoolean isTimerRunning = new AtomicBoolean(false);
    private final Object lockObject = new Object(); // Synchronization object

    public GameController(GameState gameState, MovieDatabase movieDB, GameView view) {
        this.gameState = gameState;
        this.movieDB = movieDB;
        this.view = view;
        this.gameOver = false;
        this.timer = new Timer(true); // Use daemon timer
    }

    public void startGame() {
        gameOver = false;
        gameState.setGameOver(false);
        gameState.setTimer(30);
        startTimer();
        view.displayGameState(gameState);
        view.displayAutocompleteSuggestions(movieDB.getAutocompleteSuggestions("", 5));
    }

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

                    // Print time remaining
                    System.out.println("\r[Timer] Time remaining: " + newTime + " seconds");

                    // Check if time has run out
                    if (newTime <= 0) {
                        isTimerRunning.set(false);

                        // Handle timeout - determine winner
                        Player currentPlayer = gameState.getCurrentPlayer();
                        gameState.nextPlayer(); // Move to the next player
                        Player winner = gameState.getCurrentPlayer(); // This is the winner

                        // Set game over
                        gameOver = true;
                        gameState.setGameOver(true);

                        // Announce the winner due to timeout
                        System.out.println("\n==== TIME'S UP! ====");
                        System.out.println(winner.getName() + " has WON because " +
                                currentPlayer.getName() + " ran out of time!");
                        System.out.println("===================\n");

                        // Cancel this timer task
                        cancel();
                    } else if (newTime <= 5) {
                        // Warning for last 5 seconds
                        System.out.println("\r[Timer] WARNING! " + newTime + " seconds remaining!");
                        System.out.print("Enter movie title: "); // Reprint the prompt
                    }
                }
            }
        };

        // Schedule timer to run every second
        timer.scheduleAtFixedRate(timerTask, 1000, 1000);
    }

    private void stopTimer() {
        isTimerRunning.set(false);
        if (timerTask != null) {
            timerTask.cancel();
            timer.purge();
        }
    }

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
                view.displayError(
                        "Invalid connection! The movie must be connected to the previous one by actor or director.");
                // Show scores even for invalid moves
                System.out.println("\nCurrent Scores:");
                for (Player player : gameState.getPlayers()) {
                    System.out.println(player.getName() + ": " + player.getProgress() + "%");
                }
                System.out.println();
                return;
            }

            // Valid move was made, stop the current timer
            stopTimer();

            // Update view with new game state
            view.displayGameState(gameState);

            // Check for win condition
            Player winner = gameState.checkWinCondition();
            if (winner != null) {
                gameOver = true;
                gameState.setGameOver(true);
                System.out.println("\n==== GAME OVER ====");
                System.out.println(winner.getName() + " has WON by achieving their win condition!");
                System.out.println("===================\n");
                return;
            }

            // Reset timer for next player
            gameState.setTimer(30);
            startTimer();

            // Update autocomplete suggestions
            view.displayAutocompleteSuggestions(movieDB.getAutocompleteSuggestions("", 5));
        }
    }

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

    public boolean isGameOver() {
        return gameOver || gameState.isGameOver();
    }
}