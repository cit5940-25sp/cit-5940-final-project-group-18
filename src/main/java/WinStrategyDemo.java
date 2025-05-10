import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A demonstration class to show how the Strategy pattern works in this game.
 */
public class WinStrategyDemo {
    
    public static void main(String[] args) {
        // Create some sample movie data
        List<Person> directors = new ArrayList<>();
        directors.add(new Person(1, "Christopher Nolan", "director"));
        
        List<Person> actors = new ArrayList<>();
        actors.add(new Person(2, "Leonardo DiCaprio", "actor"));
        actors.add(new Person(3, "Ellen Page", "actor"));
        
        List<String> genres = Arrays.asList("Sci-Fi", "Action", "Thriller");
        
        Movie inception = new Movie(
            1, 
            "Inception", 
            2010, 
            genres,
            actors,
            directors
        );
        
        // Create players with different win strategies
        Player player1 = new Player("Player 1", 
            WinStrategyFactory.createStrategy(
                WinStrategyFactory.GENRE_STRATEGY, 
                "Sci-Fi", 
                3 // Need 3 Sci-Fi movies to win
            )
        );
        
        Player player2 = new Player("Player 2", 
            WinStrategyFactory.createStrategy(
                WinStrategyFactory.ACTOR_STRATEGY, 
                "Leonardo DiCaprio", 
                2 // Need 2 DiCaprio movies to win
            )
        );
        
        Player player3 = new Player("Player 3", 
            WinStrategyFactory.createStrategy(
                WinStrategyFactory.DIRECTOR_STRATEGY, 
                "Christopher Nolan", 
                2 // Need 2 Nolan movies to win
            )
        );
        
        // Test the strategies
        System.out.println("Testing win strategies:");
        
        // Simulate making moves with the same movie for illustration
        updateAndPrintStatus(player1, inception);
        updateAndPrintStatus(player2, inception);
        updateAndPrintStatus(player3, inception);
        
        System.out.println("\nAfter second move with same movie:");
        updateAndPrintStatus(player1, inception);
        updateAndPrintStatus(player2, inception);
        updateAndPrintStatus(player3, inception);
        
        System.out.println("\nAfter third move with same movie:");
        updateAndPrintStatus(player1, inception);
        updateAndPrintStatus(player2, inception);
        updateAndPrintStatus(player3, inception);
    }
    
    private static void updateAndPrintStatus(Player player, Movie movie) {
        player.updateProgress(movie);
        System.out.println(player.getName() + " progress: " + player.getProgress() + "%");
        
        // Create a list with just this movie for the win condition check
        List<Movie> playedMovies = new ArrayList<>();
        playedMovies.add(movie);
        
        if (player.hasWon(playedMovies)) {
            System.out.println(player.getName() + " has won!");
        }
    }
} 