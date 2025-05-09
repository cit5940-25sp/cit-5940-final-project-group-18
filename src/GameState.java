import java.util.ArrayList;
import java.util.List;

public class GameState {
    private MovieDatabase movieDB;
    private List<Movie> playedMovies = new ArrayList<>();
    private Movie currentMovie;
    private List<Player> players = new ArrayList<>();
    private Player currentPlayer;
    private int roundCount;
    private int timer;
    private List<Connection> usedConnections = new ArrayList<>();
    private boolean gameOver;

    public GameState(List<Player> players, MovieDatabase movieDB) {
        this.players = new ArrayList<>(players); // Defensive copy
        this.roundCount = 0;
        this.movieDB = movieDB;
        this.gameOver = false;
        if (!players.isEmpty()) {
            this.currentPlayer = this.players.get(0); // Start with the first player
        }
    }

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
        currentPlayer.updateProgress(movie);
        nextPlayer(); // Move to the next player
        roundCount++;
        return true;
    }

    public void nextPlayer() {
        if (gameOver) {
            return;
        }
        int currentIndex = players.indexOf(currentPlayer);
        currentPlayer = players.get((currentIndex + 1) % players.size());
    }

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

    public void setTimer(int seconds) {
        this.timer = seconds;
    }

    public void decrementTimer() {
        if (timer > 0) {
            timer--;
        }
        if (timer <= 0) {
            gameOver = true;
        }
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    // Getters
    public List<Movie> getPlayedMovies() {
        return new ArrayList<>(playedMovies); // Defensive copy
    }

    public Movie getCurrentMovie() {
        return currentMovie;    
    }

    public List<Player> getPlayers() {
        return new ArrayList<>(players); // Defensive copy
    }   

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getRoundCount() {
        return roundCount;
    }

    public int getTimer() {
        return timer;
    }

    public List<Connection> getUsedConnections() {
        return new ArrayList<>(usedConnections); // Defensive copy
    }

    public MovieDatabase getMovieDatabase() {
        return movieDB;
    }
}
