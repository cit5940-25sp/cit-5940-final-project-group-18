import java.util.List;

public class ActorWinStrategy implements IWinStrategy {
    private String targetActor;
    private int requiredCount;
    private int currentCount;

    public ActorWinStrategy(String targetActor, int requiredCount) {
        this.targetActor = targetActor;
        this.requiredCount = requiredCount;
        this.currentCount = 0;
    }

    @Override
    public boolean checkWinCondition(List<Movie> playedMovies) {
        return currentCount >= requiredCount;
    }

    @Override
    public void updateProgress(Movie movie) {
        // Check if any actor in the movie's cast matches the target actor
        for (Person person : movie.getCast()) {
            if (person.getName().equalsIgnoreCase(targetActor)) {
                currentCount++;
                break; // Count the movie only once even if actor appears multiple times
            }
        }
    }
    
    public int getCurrentCount() {
        return currentCount;
    }
    
    public String getTargetActor() {
        return targetActor;
    }
    
    public int getRequiredCount() {
        return requiredCount;
    }
} 