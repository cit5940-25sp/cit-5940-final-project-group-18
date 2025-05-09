import java.util.List;
import java.util.ArrayList;

public class GenreWinStrategy implements IWinStrategy {
    private String targetGenre;
    private int requiredCount;
    private int currentCount;

    public GenreWinStrategy(String targetGenre, int requiredCount) {
        this.targetGenre = targetGenre;
        this.requiredCount = requiredCount;
        this.currentCount = 0;
    }

    public boolean checkWinCondition(List<Movie> playedMovies) {
        return currentCount >= requiredCount;
    }

    public void updateProgress(Movie movie) {
        if (movie.getGenres().contains(targetGenre)) {
            currentCount++;
            System.out.println("[DEBUG] " + targetGenre + " movie found! Count: " + currentCount + "/" + requiredCount);
        }
    }
    
    public int getCurrentCount() {
        return currentCount;
    }
    
    public String getTargetGenre() {
        return targetGenre;
    }
    
    public int getRequiredCount() {
        return requiredCount;
    }
}
