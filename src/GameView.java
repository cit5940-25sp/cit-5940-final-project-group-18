import java.util.List;

/**
 * Handles the display and user interface aspects of the movie connection game.
 * This class is responsible for rendering the game state, displaying messages,
 * and handling user input. It serves as the view component in the MVC pattern.
 */
public class GameView {
    /**
     * Displays the current state of the game, including played movies,
     * current player, and game progress.
     *
     * @param state The current state of the game to be displayed
     */
    public void displayGameState(GameState state) {
        // Render game state to UI
    }

    /**
     * Displays a list of movie title suggestions for autocomplete functionality.
     * These suggestions help players find valid movie titles more easily.
     *
     * @param suggestions List of movie title suggestions to display
     */
    public void displayAutocompleteSuggestions(List<String> suggestions) {
        // Render suggestions
    }

    /**
     * Displays an error message to the user.
     * Used for invalid moves, movie not found, or other game-related errors.
     *
     * @param error The error message to display
     */
    public void displayError(String error) {
        // Render error message
    }

    /**
     * Gets input from the user, typically a movie title.
     * This method should handle the user interface for input collection.
     *
     * @return The user's input as a string
     */
    public String getUserInput() {
        // Get input from user
        return "";
    }

    /**
     * Updates the display of the game timer.
     * This method should update the visual representation of the remaining time.
     *
     * @param seconds The number of seconds remaining in the current turn
     */
    public void updateTimer(int seconds) {
        // Update timer display
    }
}
