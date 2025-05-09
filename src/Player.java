import java.util.List;

/**
 * Represents a player in the movie connection game.
 * Each player has a name, a win strategy, and tracks their progress towards winning.
 * 
 * The class supports different types of win strategies (Genre, Actor, Director)
 * and calculates progress as a percentage towards the win condition.
 * Progress is capped at 100% and is calculated based on the specific win strategy
 * being used.
 * 
 * The player's progress is updated whenever they successfully play a movie,
 * and the win condition is checked against the list of played movies.
 */
public class Player {
    /**
     * The player's name, used for identification and display purposes.
     */
    private String name;
    
    /**
     * The strategy that determines how this player can win the game.
     * Different strategies (Genre, Actor, Director) have different win conditions
     * and progress tracking mechanisms.
     */
    private IWinStrategy winStrategy;
    
    /**
     * The player's current progress towards winning, represented as a percentage.
     * This value is calculated based on the win strategy and is capped at 100.
     */
    private int progress;

    /**
     * Constructs a new player with the specified name and win strategy.
     * Initializes progress to 0.
     *
     * @param name The player's name
     * @param winStrategy The strategy that determines how this player can win
     */
    public Player(String name, IWinStrategy winStrategy) {
        this.name = name;
        this.winStrategy = winStrategy;
        this.progress = 0;
    }

    /**
     * Updates the player's progress based on a newly played movie.
     * This method:
     * 1. Updates the win strategy's progress
     * 2. Recalculates the player's overall progress percentage
     *
     * @param movie The movie that was just played
     */
    public void updateProgress(Movie movie) {
        winStrategy.updateProgress(movie);
        // Update the progress for display purposes
        updateProgressBasedOnStrategy();
    }
    
    /**
     * Calculates the player's progress percentage based on their win strategy.
     * The calculation varies depending on the type of win strategy:
     * - GenreWinStrategy: Progress based on number of movies in target genre
     * - ActorWinStrategy: Progress based on number of movies with target actor
     * - DirectorWinStrategy: Progress based on number of movies by target director
     * 
     * Progress is calculated as (current count / required count) * 100
     * and is capped at 100%.
     */
    private void updateProgressBasedOnStrategy() {
        if (winStrategy instanceof GenreWinStrategy) {
            GenreWinStrategy strategy = (GenreWinStrategy) winStrategy;
            progress = (int) (((double) strategy.getCurrentCount() / strategy.getRequiredCount()) * 100);
        } else if (winStrategy instanceof ActorWinStrategy) {
            ActorWinStrategy strategy = (ActorWinStrategy) winStrategy;
            progress = (int) (((double) strategy.getCurrentCount() / strategy.getRequiredCount()) * 100);
        } else if (winStrategy instanceof DirectorWinStrategy) {
            DirectorWinStrategy strategy = (DirectorWinStrategy) winStrategy;
            progress = (int) (((double) strategy.getCurrentCount() / strategy.getRequiredCount()) * 100);
        }
        
        // Cap progress at 100
        if (progress > 100) {
            progress = 100;
        }
    }

    /**
     * Gets the player's name.
     *
     * @return The player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the player's win strategy.
     *
     * @return The player's win strategy
     */
    public IWinStrategy getWinStrategy() {
        return winStrategy;
    }

    /**
     * Gets the player's current progress towards winning.
     *
     * @return The player's progress as a percentage (0-100)
     */
    public int getProgress() {
        return progress;
    }
    
    /**
     * Checks if the player has won the game based on their win strategy
     * and the list of played movies.
     *
     * @param playedMovies The list of movies that have been played in the game
     * @return true if the player has won, false otherwise
     */
    public boolean hasWon(List<Movie> playedMovies) {
        return winStrategy.checkWinCondition(playedMovies);
    }
}
