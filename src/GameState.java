import java.util.ArrayList;
import java.util.List;

/**
 * Represents the current state of the movie connection game.
 * This class manages the game's core state including played movies, players, turns, and connections.
 */
public class GameState {
    /** The database containing all available movies */
    private MovieDatabase movieDB;
    
    /** List of movies that have been played in the game */
    private List<Movie> playedMovies = new ArrayList<>();
    
    /** The most recently played movie */
    private Movie currentMovie;
    
    /** List of players participating in the game */
    private List<Player> players = new ArrayList<>();
    
    /** The player whose turn it currently is */
    private Player currentPlayer;
    
    /** The number of rounds played in the game */
    private int roundCount;
    
    /** The current timer value in seconds */
    private int timer;
    
    /** List of connections that have been used between movies */
    private List<Connection> usedConnections = new ArrayList<>();
    
    /** Flag indicating whether the game is over */
    private boolean gameOver;

    /**
     * Constructs a new GameState with the specified players and movie database.
     *
     * @param players The list of players participating in the game
     * @param movieDB The movie database to use for the game
     */
    public GameState(List<Player> players, MovieDatabase movieDB) {
        this.players = new ArrayList<>(players); // Defensive copy
        this.roundCount = 0;
        this.movieDB = movieDB;
        this.gameOver = false;
        if (!players.isEmpty()) {
            this.currentPlayer = this.players.get(0); // Start with the first player
        }
    }

    /**
     * Attempts to make a move with the specified movie.
     * Validates the connection with the previous movie and updates the game state if valid.
     *
     * @param movie The movie to play
     * @return true if the move was valid and successful, false otherwise
     */
    public boolean makeMove(Movie movie) {
        if (gameOver) {
            return false;
        }

        Movie lastMovie = playedMovies.isEmpty() ? null : playedMovies.get(playedMovies.size() - 1);
        if (lastMovie != null) {
            Connection connection = movieDB.validateConnection(lastMovie, movie);
            if (connection == null || !connection.isValid()) {
                return false; // Invalid move
            }
            usedConnections.add(connection);
        }
        
        playedMovies.add(movie);
        currentMovie = movie;
        
        // Update the current player's progress before switching players
        currentPlayer.updateProgress(movie);
        
        // Debug: Print player scores after each move
        System.out.println("\n[DEBUG] Current Scores:");
        for (Player player : players) {
            System.out.println("[DEBUG] " + player.getName() + ": " + player.getProgress() + "%");
        }
        System.out.println();
        
        nextPlayer(); // Move to the next player
        roundCount++;
        return true;
    }

    /**
     * Advances to the next player's turn.
     * Does nothing if the game is over.
     */
    public void nextPlayer() {
        if (gameOver) {
            return;
        }
        int currentIndex = players.indexOf(currentPlayer);
        currentPlayer = players.get((currentIndex + 1) % players.size());
    }

    /**
     * Checks if any player has met their win condition.
     *
     * @return The winning player if one exists, null otherwise
     */
    public Player checkWinCondition() {
        if (gameOver) {
            return null;
        }
        for (Player p : players) {
            if (p.hasWon(playedMovies)) {
                gameOver = true;
                return p;
            }
        }
        return null;
    }

    /**
     * Sets the game timer to the specified number of seconds.
     *
     * @param seconds The number of seconds to set the timer to
     */
    public void setTimer(int seconds) {
        this.timer = seconds;
    }

    /**
     * Decrements the game timer by one second.
     * Sets the game to over if the timer reaches zero.
     */
    public void decrementTimer() {
        if (timer > 0) {
            timer--;
        }
        if (timer <= 0) {
            gameOver = true;
        }
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * Sets the game over state.
     *
     * @param gameOver The new game over state
     */
    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    /**
     * Gets a defensive copy of the list of played movies.
     *
     * @return A new list containing all played movies
     */
    public List<Movie> getPlayedMovies() {
        return new ArrayList<>(playedMovies); // Defensive copy
    }

    /**
     * Gets the most recently played movie.
     *
     * @return The current movie
     */
    public Movie getCurrentMovie() {
        return currentMovie;    
    }

    /**
     * Gets a defensive copy of the list of players.
     *
     * @return A new list containing all players
     */
    public List<Player> getPlayers() {
        return new ArrayList<>(players); // Defensive copy
    }   

    /**
     * Gets the current player.
     *
     * @return The player whose turn it is
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Gets the next player in the turn order.
     *
     * @return The player who will play next
     */
    public Player getNextPlayer() {
        int currentIndex = players.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % players.size();
        return players.get(nextIndex);
    }

    /**
     * Gets the current round count.
     *
     * @return The number of rounds played
     */
    public int getRoundCount() {
        return roundCount;
    }

    /**
     * Gets the current timer value.
     *
     * @return The number of seconds remaining
     */
    public int getTimer() {
        return timer;
    }

    /**
     * Gets a defensive copy of the list of used connections.
     *
     * @return A new list containing all used connections
     */
    public List<Connection> getUsedConnections() {
        return new ArrayList<>(usedConnections); // Defensive copy
    }

    /**
     * Gets the movie database used in the game.
     *
     * @return The movie database
     */
    public MovieDatabase getMovieDatabase() {
        return movieDB;
    }
}
