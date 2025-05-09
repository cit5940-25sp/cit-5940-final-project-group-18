import java.util.List;

/**
 * Interface defining the contract for win strategies in the movie connection game.
 * Implementations of this interface determine how a player can win the game by
 * tracking specific conditions (e.g., genres, actors, directors) in played movies.
 */
public interface IWinStrategy {
    /**
     * Checks if the win condition has been met based on the played movies.
     * This method should evaluate whether the player has achieved their specific
     * win condition (e.g., playing enough movies of a certain genre, with a certain
     * actor, or directed by a certain director).
     *
     * @param playedMovies The list of movies that have been played in the game
     * @return true if the win condition has been met, false otherwise
     */
    boolean checkWinCondition(List<Movie> playedMovies);

    /**
     * Updates the progress towards the win condition based on a newly played movie.
     * This method should analyze the given movie and update any internal tracking
     * of progress towards the win condition (e.g., counting movies of a specific
     * genre, tracking appearances of a specific actor, etc.).
     *
     * @param movie The movie that was just played
     */
    void updateProgress(Movie movie);
}
