import java.util.List;

public class GameController {
    private GameState gameState;
    private MovieDatabase movieDB;
    private GameView view;
    private boolean gameOver;

    public GameController(GameState gameState, MovieDatabase movieDB, GameView view) {
        this.gameState = gameState;
        this.movieDB = movieDB;
        this.view = view;
        this.gameOver = false;
    }

    public void startGame() {
        gameOver = false;
        gameState.setGameOver(false); // Reset game state
        gameState.setTimer(60); // Set initial timer to 60 seconds
        view.displayGameState(gameState);
        view.displayAutocompleteSuggestions(movieDB.getAutocompleteSuggestions("", 5));
    }

    public void processInput(String input) {
        if (gameOver || gameState.isGameOver()) {
            view.displayError("Game is over!");
            return;
        }

        Movie movie = movieDB.findMovie(input);
        if (movie == null) {
            view.displayError("Movie not found!");
            return;
        }

        boolean valid = gameState.makeMove(movie);
        if (!valid) {
            view.displayError("Invalid connection! The movie must be connected to the previous one by actor or director.");
            return;
        }

        // Update view with new game state
        view.displayGameState(gameState);
        
        // Check for win condition
        Player winner = gameState.checkWinCondition();
        if (winner != null) {
            gameOver = true;
            gameState.setGameOver(true);
            view.displayError("Game Over! " + winner.getName() + " has won!");
            return;
        }

        // Update autocomplete suggestions
        view.displayAutocompleteSuggestions(movieDB.getAutocompleteSuggestions("", 5));
    }

    public void endTurn() {
        if (gameOver || gameState.isGameOver()) {
            return;  // Don't do anything if game is over
        }
        gameState.nextPlayer();
        view.displayGameState(gameState);
    }

    public void checkTimeLimit() {
        if (gameOver || gameState.isGameOver()) {
            return;
        }

        int remainingTime = gameState.getTimer();
        if (remainingTime <= 0) {
            gameOver = true;
            gameState.setGameOver(true);
            view.displayError("Time's up! Game Over!");
            return;
        }

        gameState.decrementTimer();
        if (gameState.getTimer() <= 0) {  // Check again after decrementing
            gameOver = true;
            gameState.setGameOver(true);
            view.displayError("Time's up! Game Over!");
            return;
        }
        view.updateTimer(gameState.getTimer());
    }

    public boolean isGameOver() {
        return gameOver || gameState.isGameOver();
    }
}
