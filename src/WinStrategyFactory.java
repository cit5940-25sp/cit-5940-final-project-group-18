/**
 * Factory class for creating different types of win strategies in the movie connection game.
 * This class provides a centralized way to create win strategies based on their type,
 * target value, and required count.
 * 
 * The factory supports three types of win strategies:
 * - Genre-based: Win by playing movies of a specific genre
 * - Actor-based: Win by playing movies featuring a specific actor
 * - Director-based: Win by playing movies directed by a specific director
 * 
 * Each strategy type requires:
 * - A target value (genre name, actor name, or director name)
 * - A required count of matches to win
 * 
 * The factory ensures consistent strategy creation and provides clear error messages
 * for invalid strategy types.
 */
public class WinStrategyFactory {
    /**
     * Constant representing the genre-based win strategy.
     * Players win by playing movies of a specific genre.
     */
    public static final String GENRE_STRATEGY = "genre";
    
    /**
     * Constant representing the actor-based win strategy.
     * Players win by playing movies featuring a specific actor.
     */
    public static final String ACTOR_STRATEGY = "actor";
    
    /**
     * Constant representing the director-based win strategy.
     * Players win by playing movies directed by a specific director.
     */
    public static final String DIRECTOR_STRATEGY = "director";
    
    /**
     * Creates a win strategy based on the specified type, target, and required count.
     * The factory method handles the creation of appropriate strategy implementations
     * and validates the strategy type.
     * 
     * Strategy types are case-insensitive for better user experience.
     * 
     * @param strategyType The type of strategy to create (genre, actor, or director)
     * @param target The target value for the strategy:
     *              - For genre: the name of the genre
     *              - For actor: the name of the actor
     *              - For director: the name of the director
     * @param requiredCount The number of matches required to win:
     *                     - For genre: number of movies in the genre
     *                     - For actor: number of movies with the actor
     *                     - For director: number of movies by the director
     * @return An implementation of IWinStrategy appropriate for the specified type
     * @throws IllegalArgumentException if the strategy type is not recognized
     */
    public static IWinStrategy createStrategy(String strategyType, String target, int requiredCount) {
        if (strategyType.equalsIgnoreCase(GENRE_STRATEGY)) {
            return new GenreWinStrategy(target, requiredCount);
        } else if (strategyType.equalsIgnoreCase(ACTOR_STRATEGY)) {
            return new ActorWinStrategy(target, requiredCount);
        } else if (strategyType.equalsIgnoreCase(DIRECTOR_STRATEGY)) {
            return new DirectorWinStrategy(target, requiredCount);
        } else {
            throw new IllegalArgumentException("Unknown strategy type: " + strategyType);
        }
    }
} 