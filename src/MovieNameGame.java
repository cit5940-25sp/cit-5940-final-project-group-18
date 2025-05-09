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
        // Both players need to play 3 Action movies to win
        players.add(new Player("Player 1", new GenreWinStrategy("Action", 3)));
        players.add(new Player("Player 2", new GenreWinStrategy("Action", 3)));

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

            // Keep asking for input until we get a valid movie or game is over
            while (!gameState.isGameOver()) {
                // Store the current number of movies before processing
                int moviesBefore = gameState.getPlayedMovies().size();
                System.out.println("[DEBUG] Movies before processing: " + moviesBefore);
                
                controller.processInput(input);
                
                // If the game is over after processing, break out
                if (gameState.isGameOver()) {
                    System.out.println("[DEBUG] Game over detected, breaking reprompt loop");
                    break;
                }
                
                // If the number of movies hasn't changed, the input was invalid
                // and we need to reprompt
                if (gameState.getPlayedMovies().size() == moviesBefore) {
                    System.out.println("[DEBUG] Invalid input detected - Movies after processing: " + gameState.getPlayedMovies().size());
                    System.out.println("[DEBUG] Reprompting for new input...");
                    System.out.print("Enter movie title: ");
                    input = scanner.nextLine();
                    System.out.println("[DEBUG] New input received: " + input);
                } else {
                    // Valid move was made, break out of the reprompt loop
                    System.out.println("[DEBUG] Valid move detected - Movies after processing: " + gameState.getPlayedMovies().size());
                    System.out.println("[DEBUG] Breaking reprompt loop");
                    break;
                }
            }

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