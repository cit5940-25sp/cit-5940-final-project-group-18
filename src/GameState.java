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

    public GameState(List<Player> players, MovieDatabase movieDB) {
        this.players = players;
        this.roundCount = 0;
        this.movieDB = movieDB;
        if (!players.isEmpty()) {
            this.currentPlayer = players.get(0); // Start with the first player
        }
        // Initialize other fields...
    }

    public boolean makeMove(Movie movie) {
        Movie lastMovie = playedMovies.isEmpty() ? null : playedMovies.get(playedMovies.size() - 1);
        if (lastMovie != null) {
            Connection connection = movieDB.validateConnection(lastMovie, movie);
            if (connection == null || !connection.isValid()) {
                return false; // Invalid move
            }
            usedConnections.add(connection);
        }
        playedMovies.add(movie);
        currentPlayer.updateProgress(movie);
        nextPlayer(); // Move to the next player
        roundCount++;
        return true;
    }

    private void nextPlayer() {
        int currentIndex = players.indexOf(currentPlayer);
        currentPlayer = players.get((currentIndex + 1) % players.size());
    }

    public Player checkWinCondition() {
        for (Player p : players) {
            if (p.hasWon(playedMovies)) {
                return p;
            }
        }
        return null;
    }

    // Getters and setters...
    public List<Movie> getPlayedMovies() {
        return playedMovies;
    }

    public Movie getCurrentMovie() {
        return currentMovie;    
    }

    public List<Player> getPlayers() {
        return players;
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
        return usedConnections;
    }
}
