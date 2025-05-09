import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

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
        movieDB = new MovieDatabase("movies.csv", "credits.csv");

        // Setup players (sample strategies)
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player 1", new GenreWinStrategy("Action", 3)));
        players.add(new Player("Player 2", new ActorWinStrategy("Tom Hanks", 2)));

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

            controller.processInput(input);

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
