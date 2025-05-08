import java.util.List;

public class Player {
    private String name;
    private IWinStrategy winStrategy;
    private int progress;

    public Player(String name, IWinStrategy winStrategy) {
        this.name = name;
        this.winStrategy = winStrategy;
        this.progress = 0;
    }

    public void updateProgress(Movie movie) {
        winStrategy.updateProgress(movie);
        // Update the progress for display purposes
        updateProgressBasedOnStrategy();
    }
    
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

    public String getName() {
        return name;
    }

    public IWinStrategy getWinStrategy() {
        return winStrategy;
    }

    public int getProgress() {
        return progress;
    }
    
    public boolean hasWon(List<Movie> playedMovies) {
        return winStrategy.checkWinCondition(playedMovies);
    }
}
