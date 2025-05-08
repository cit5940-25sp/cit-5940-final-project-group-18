import java.util.List;

public class DirectorWinStrategy implements IWinStrategy {
    private String targetDirector;
    private int requiredCount;
    private int currentCount;

    public DirectorWinStrategy(String targetDirector, int requiredCount) {
        this.targetDirector = targetDirector;
        this.requiredCount = requiredCount;
        this.currentCount = 0;
    }

    @Override
    public boolean checkWinCondition(List<Movie> playedMovies) {
        return currentCount >= requiredCount;
    }

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
    
    public int getCurrentCount() {
        return currentCount;
    }
    
    public String getTargetDirector() {
        return targetDirector;
    }
    
    public int getRequiredCount() {
        return requiredCount;
    }
} 