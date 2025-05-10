import java.util.List;

/**
 * A win strategy implementation that tracks movies featuring a specific actor.
 * The player wins when they have played a specified number of movies featuring the target actor.
 */
public class ActorWinStrategy implements IWinStrategy {
    /** The name of the actor that the player needs to find in movies */
    private String targetActor;
    
    /** The number of movies featuring the target actor needed to win */
    private int requiredCount;
    
    /** The current count of movies featuring the target actor that have been played */
    private int currentCount;

    /**
     * Constructs a new ActorWinStrategy with the specified target actor and required count.
     *
     * @param targetActor The name of the actor that the player needs to find in movies
     * @param requiredCount The number of movies featuring the target actor needed to win
     */
    public ActorWinStrategy(String targetActor, int requiredCount) {
        this.targetActor = targetActor;
        this.requiredCount = requiredCount;
        this.currentCount = 0;
    }

    /**
     * Checks if the win condition has been met.
     * The player wins when they have played the required number of movies featuring the target actor.
     *
     * @param playedMovies The list of movies that have been played (not used in this implementation)
     * @return true if the current count of movies featuring the target actor is greater than or equal to the required count
     */
    @Override
    public boolean checkWinCondition(List<Movie> playedMovies) {
        return currentCount >= requiredCount;
    }

    /**
     * Updates the progress by checking if the given movie features the target actor.
     * If the target actor is found in the movie's cast, the current count is incremented.
     * Each movie is counted only once, even if the actor appears multiple times in the cast.
     *
     * @param movie The movie to check for the target actor
     */
    @Override
    public void updateProgress(Movie movie) {
        // Check if any actor in the movie's cast matches the target actor
        for (Person person : movie.getCast()) {
            if (person.getName().equalsIgnoreCase(targetActor)) {
                currentCount++;
                System.out.println("[DEBUG] " + targetActor + " movie found! Count: " + currentCount + "/" + requiredCount);
                break; // Count the movie only once even if actor appears multiple times
            }
        }
    }
    
    /**
     * Gets the current count of movies featuring the target actor that have been played.
     *
     * @return The current count of movies featuring the target actor
     */
    public int getCurrentCount() {
        return currentCount;
    }
    
    /**
     * Gets the name of the target actor that the player needs to find in movies.
     *
     * @return The name of the target actor
     */
    public String getTargetActor() {
        return targetActor;
    }
    
    /**
     * Gets the number of movies featuring the target actor needed to win.
     *
     * @return The required number of movies featuring the target actor
     */
    public int getRequiredCount() {
        return requiredCount;
    }

    @Override
    public String getDescription() {
        return String.format("Play %d movies with %s", requiredCount, targetActor);
    }
} 