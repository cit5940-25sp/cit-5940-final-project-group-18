import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.opencsv.exceptions.CsvValidationException;

public class MovieNameGame {
    private GameController controller;
    private GameView view;
    private MovieDatabase movieDB;
    private GameState gameState;

    public void startGame() throws IOException, CsvValidationException {
        initializeComponents();
        gameLoop();
    }

    private void initializeComponents() throws IOException, CsvValidationException {
        // Load movie database (update with actual CSV paths if needed)
        movieDB = new MovieDatabase("data/movies.csv", "data/credits.csv");

        // Setup players with the same win strategy
        List<Player> players = new ArrayList<>();
        // Both players need to play 3 Action movies to win
        players.add(new Player("Player 1", new GenreWinStrategy("Action", 3)));
        players.add(new Player("Player 2", new GenreWinStrategy("Action", 3)));

        // Initialize game state and view
        gameState = new GameState(players, movieDB);
        view = new GameView();

        // Initialize controller
        controller = new GameController(gameState, movieDB, view);
    }

    private void gameLoop() {
        Scanner scanner = new Scanner(System.in);
        controller.startGame();

        while (true) {
            view.displayGameState(gameState);
            System.out.print("Enter movie title: ");
            String input = scanner.nextLine();
            
            // Debug: Print the input
            System.out.println("[DEBUG] Input received: " + input);
            
            // Debug: Print current game state before processing
            System.out.println("[DEBUG] Current player: " + gameState.getCurrentPlayer().getName());
            System.out.println("[DEBUG] Number of movies played: " + gameState.getPlayedMovies().size());
            if (!gameState.getPlayedMovies().isEmpty()) {
                System.out.println("[DEBUG] Last movie played: " + gameState.getPlayedMovies().get(gameState.getPlayedMovies().size() - 1).getTitle());
            }

            controller.processInput(input);

            // Debug: Print game state after processing
            System.out.println("[DEBUG] After processing - Number of movies: " + gameState.getPlayedMovies().size());
            System.out.println("[DEBUG] Game over status: " + gameState.isGameOver());

            // Check for win condition
            Player winner = gameState.checkWinCondition();
            if (winner != null) {
                System.out.println("Winner: " + winner.getName());
                break;
            }
        }
        scanner.close();
    }

    public static void main(String[] args) throws IOException, CsvValidationException {
        new MovieNameGame().startGame();
    }
}
