public class WinStrategyFactory {
    
    public static final String GENRE_STRATEGY = "genre";
    public static final String ACTOR_STRATEGY = "actor";
    public static final String DIRECTOR_STRATEGY = "director";
    
    /**
     * Creates a win strategy based on the specified type.
     * 
     * @param strategyType The type of strategy (genre, actor, director)
     * @param target The target value (genre name, actor name, director name)
     * @param requiredCount The number of matches required to win
     * @return An IWinStrategy implementation
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