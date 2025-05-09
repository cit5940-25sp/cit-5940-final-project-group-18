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

// Subject interface
public interface Subject {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObservers();
}

// Observer interface
public interface Observer {
    void update();
}

// GameState implements Subject
public class GameState implements Subject {
    private List<Observer> observers = new ArrayList<>();
    // ... existing code ...
    public void registerObserver(Observer o) { observers.add(o); }
    public void removeObserver(Observer o) { observers.remove(o); }
    public void notifyObservers() { for (Observer o : observers) o.update(); }
    // Call notifyObservers() whenever state changes
}
