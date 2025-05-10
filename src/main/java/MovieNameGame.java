import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * The main game class that orchestrates the movie connection game.
 * This class is responsible for:
 * - Initializing game components (database, players, state, view, controller)
 * - Managing the main game loop
 * - Handling user input and game flow
 * - Coordinating between different game components
 * 
 * The game follows a simple flow:
 * 1. Initialize all components
 * 2. Start the game
 * 3. Enter the main game loop where players take turns
 * 4. Process player input and validate moves
 * 5. Check for win conditions
 * 6. End the game when a winner is determined
 */
public class MovieNameGame {
    /** Controller that manages game logic and player input processing */
    private GameController controller;
    
    /** View component responsible for displaying game state and messages */
    private GameView view;
    
    /** Database containing all movie information and connection logic */
    private MovieDatabase movieDB;
    
    /** Current state of the game, including players and played movies */
    private GameState gameState;

    /**
     * Starts the game by initializing components and entering the game loop.
     * This is the main entry point for the game.
     *
     * @throws IOException If there's an error reading the movie database files
     * @throws CsvValidationException If there's an error parsing the CSV files
     */
    public void startGame() throws IOException, CsvValidationException {
        initializeComponents();
        gameLoop();
    }

    /**
     * Initializes all game components:
     * - Loads the movie database from CSV files
     * - Creates players with their win strategies
     * - Sets up the game state
     * - Initializes the view and controller
     *
     * @throws IOException If there's an error reading the movie database files
     * @throws CsvValidationException If there's an error parsing the CSV files
     */
    private void initializeComponents() throws IOException, CsvValidationException {
        // Load movie database (update with actual CSV paths if needed)
        movieDB = new MovieDatabase("data/movies.csv", "data/credits.csv");

        // Setup players with the same win strategy
        List<Player> players = new ArrayList<>();
        players.add(new Player("Player 1", new DirectorWinStrategy("Christopher Nolan", 5)));
        players.add(new Player("Player 2", new DirectorWinStrategy("Christopher Nolan", 5)));

        // Initialize game state and view
        gameState = new GameState(players, movieDB);
        view = new GameView();

        // Initialize controller
        controller = new GameController(gameState, movieDB, view);
    }

    /**
     * Main game loop that handles:
     * - Displaying the current game state
     * - Getting and processing player input
     * - Validating moves and reprompting for invalid input
     * - Checking for win conditions
     * 
     * The loop continues until a player wins or the game is over.
     * Invalid moves are handled by reprompting the player while maintaining
     * the game state and timer.
     */
    private void gameLoop() {
        controller.startGame();
        view.setGameState(gameState);

        while (true) {
            view.displayGameState(gameState);
            String input = view.getUserInput();
            
            if (input == null) {
                break; // User pressed Escape
            }

            // Process input and check game state
            controller.processInput(input);
            
            if (gameState.isGameOver()) {
                // Force one more state update to ensure the win message is displayed
                view.displayGameState(gameState);

                // Add a small delay to ensure the message is visible
                try {
                    Thread.sleep(5000); // Wait 5 seconds before exiting
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                break;
            }
        }

        try {
            view.close(); // Clean up the terminal
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Main entry point for the application.
     * Creates and starts a new game instance.
     *
     * @param args Command line arguments (not used)
     * @throws IOException If there's an error reading the movie database files
     * @throws CsvValidationException If there's an error parsing the CSV files
     */
    public static void main(String[] args) throws IOException, CsvValidationException {
        new MovieNameGame().startGame();
    }
}