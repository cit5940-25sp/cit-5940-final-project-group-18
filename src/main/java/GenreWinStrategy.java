import java.util.List;

/**
 * A win strategy implementation that tracks movies of a specific genre.
 * The player wins when they have played a specified number of movies of the target genre.
 */
public class GenreWinStrategy implements IWinStrategy {
    /** The genre that the player needs to find in movies */
    private String targetGenre;
    
    /** The number of movies of the target genre needed to win */
    private int requiredCount;
    
    /** The current count of movies of the target genre that have been played */
    private int currentCount;

    /**
     * Constructs a new GenreWinStrategy with the specified target genre and required count.
     *
     * @param targetGenre The genre that the player needs to find in movies
     * @param requiredCount The number of movies of the target genre needed to win
     */
    public GenreWinStrategy(String targetGenre, int requiredCount) {
        this.targetGenre = targetGenre;
        this.requiredCount = requiredCount;
        this.currentCount = 0;
    }

    /**
     * Checks if the win condition has been met.
     * The player wins when they have played the required number of movies of the target genre.
     *
     * @param playedMovies The list of movies that have been played (not used in this implementation)
     * @return true if the current count of movies of the target genre is greater than or equal to the required count
     */
    public boolean checkWinCondition(List<Movie> playedMovies) {
        return currentCount >= requiredCount;
    }

    /**
     * Updates the progress by checking if the given movie is of the target genre.
     * If the movie's genres list contains the target genre, the current count is incremented.
     *
     * @param movie The movie to check for the target genre
     */
    public void updateProgress(Movie movie) {
        if (movie.getGenres().contains(targetGenre)) {
            currentCount++;
            System.out.println("[DEBUG] " + targetGenre + " movie found! Count: " + currentCount + "/" + requiredCount);
        }
    }
    
    /**
     * Gets the current count of movies of the target genre that have been played.
     *
     * @return The current count of movies of the target genre
     */
    public int getCurrentCount() {
        return currentCount;
    }
    
    /**
     * Gets the target genre that the player needs to find in movies.
     *
     * @return The target genre
     */
    public String getTargetGenre() {
        return targetGenre;
    }
    
    /**
     * Gets the number of movies of the target genre needed to win.
     *
     * @return The required number of movies of the target genre
     */
    public int getRequiredCount() {
        return requiredCount;
    }

    @Override
    public String getDescription() {
        return String.format("Play %d %s movies", requiredCount, targetGenre);
    }
}
