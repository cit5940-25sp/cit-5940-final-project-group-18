import java.util.List;

/**
 * A win strategy implementation that tracks movies directed by a specific director.
 * The player wins when they have played a specified number of movies directed by the target director.
 */
public class DirectorWinStrategy implements IWinStrategy {
    /** The name of the director that the player needs to find in movies */
    private String targetDirector;
    
    /** The number of movies directed by the target director needed to win */
    private int requiredCount;
    
    /** The current count of movies directed by the target director that have been played */
    private int currentCount;

    /**
     * Constructs a new DirectorWinStrategy with the specified target director and required count.
     *
     * @param targetDirector The name of the director that the player needs to find in movies
     * @param requiredCount The number of movies directed by the target director needed to win
     */
    public DirectorWinStrategy(String targetDirector, int requiredCount) {
        this.targetDirector = targetDirector;
        this.requiredCount = requiredCount;
        this.currentCount = 0;
    }

    /**
     * Checks if the win condition has been met.
     * The player wins when they have played the required number of movies directed by the target director.
     *
     * @param playedMovies The list of movies that have been played (not used in this implementation)
     * @return true if the current count of movies directed by the target director is greater than or equal to the required count
     */
    @Override
    public boolean checkWinCondition(List<Movie> playedMovies) {
        return currentCount >= requiredCount;
    }

    /**
     * Updates the progress by checking if the given movie was directed by the target director.
     * If the target director is found in the movie's crew with the role "director", the current count is incremented.
     * Each movie is counted only once, even if the director appears multiple times in the crew.
     *
     * @param movie The movie to check for the target director
     */
    @Override
    public void updateProgress(Movie movie) {
        // Check if any person in the crew is a director matching the target director
        for (Person person : movie.getCrew()) {
            if (person.getRole().equalsIgnoreCase("director") &&
                person.getName().equalsIgnoreCase(targetDirector)) {
                currentCount++;
                break; // Count the movie only once
            }
        }
    }
    
    /**
     * Gets the current count of movies directed by the target director that have been played.
     *
     * @return The current count of movies directed by the target director
     */
    public int getCurrentCount() {
        return currentCount;
    }
    
    /**
     * Gets the name of the target director that the player needs to find in movies.
     *
     * @return The name of the target director
     */
    public String getTargetDirector() {
        return targetDirector;
    }
    
    /**
     * Gets the number of movies directed by the target director needed to win.
     *
     * @return The required number of movies directed by the target director
     */
    public int getRequiredCount() {
        return requiredCount;
    }

    /**
     * Gets a human-readable description of the win condition.
     * This method should return a string that clearly explains what the player
     * needs to do to win (e.g., "Play 3 Action movies" or "Play 2 movies with Tom Hanks").
     *
     * @return A string describing the win condition
     */
    @Override
    public String getDescription() {
        return String.format("Play %d movies directed by %s", requiredCount, targetDirector);
    }
} 