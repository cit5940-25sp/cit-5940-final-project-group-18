import java.util.List;

public interface IWinStrategy {
    boolean checkWinCondition(List<Movie> playedMovies);
    void updateProgress(Movie movie);
}
