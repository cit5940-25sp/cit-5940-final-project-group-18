import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Handles the display and user interface aspects of the movie connection game.
 * This class is responsible for rendering the game state, displaying messages,
 * and handling user input. It serves as the view component in the MVC pattern.
 * Uses Lanterna for terminal-based UI.
 */
public class GameView {
    private Terminal terminal;
    private Screen screen;
    private StringBuilder currentInput = new StringBuilder();
    private List<String> suggestions = List.of();
    private int cursorPosition = 0;
    private boolean running = true;
    private GameState gameState;
    private ScheduledExecutorService displayTimer;
    private String currentError = null;  // Add this field to store the current error message

    /**
     * Constructs a new GameView and initializes the terminal screen.
     * @throws IOException if there's an error initializing the terminal
     */
    public GameView() throws IOException {
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        
        displayTimer = Executors.newSingleThreadScheduledExecutor();
        displayTimer.scheduleAtFixedRate(() -> {
            if (gameState != null) {
                try {
                    displayGameState(gameState);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 0, 100, TimeUnit.MILLISECONDS);
    }

    /**
     * Sets the game state to be displayed.
     * @param gameState The game state to be displayed
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    /**
     * Displays the current state of the game, including played movies,
     * current player, and game progress.
     *
     * @param state The current state of the game to be displayed
     */
    public void displayGameState(GameState state) {
        try {
            screen.clear();
            
            // Display timer and current player at the top with a border
            String header = String.format("Round: %d | Time: %ds | Current Player: %s",
                    state.getRoundCount(), state.getTimer(), state.getCurrentPlayer().getName());            printString(0, 0, "╔" + "═".repeat(header.length() + 2) + "╗");
            printString(0, 1, "║ " + header + " ║");
            printString(0, 2, "╚" + "═".repeat(header.length() + 2) + "╝");

            // Display movie history with a section header
            int row = 4;
            printString(0, row++, "┌─ Movie History ──────────────────────────────┐");
            List<Movie> movies = state.getPlayedMovies();
            if (movies.isEmpty()) {
                printString(2, row++, "No movies played yet");
            } else {
                // Only show the last 5 movies
                int startIndex = Math.max(0, movies.size() - 5);
                for (int i = startIndex; i < movies.size(); i++) {
                    Movie movie = movies.get(i);
                    printString(2, row++, "• " + movie.getTitle());
                    
                    // Show connection to previous movie if it exists
                    if (i > startIndex) {
                        Movie prevMovie = movies.get(i - 1);
                        Connection connection = state.getMovieDatabase().validateConnection(prevMovie, movie);
                        if (connection != null && connection.isValid()) {
                            String connectionInfo = String.format("  ↳ Connected via: %s", connection.getDescription());
                            printString(2, row++, connectionInfo);
                        }
                    }
                }
            }
            printString(0, row++, "└" + "─".repeat(45) + "┘");

            // Display player status with a section header
            row += 1;
            printString(0, row++, "┌─ Player Status ──────────────────────────────┐");

            // Display win condition once (assuming all players have same win condition)
            String winCondition = String.format("Win Condition: %s", state.getPlayers().get(0).getWinStrategy().getDescription());
            printString(2, row++, "• " + winCondition);

            // Display each player's progress
            for (Player player : state.getPlayers()) {
                String status = String.format("%s: %d%%", player.getName(), player.getProgress());
                printString(2, row++, "• " + status);
            }
            printString(0, row++, "└" + "─".repeat(45) + "┘");

            // Display input area with a prompt
            row += 1;
            printString(0, row, "┌─ Enter Movie Title ──────────────────────────┐");
            row += 1;
            printString(2, row, "> " + currentInput.toString());

            // Display suggestions in a box
            if (!suggestions.isEmpty()) {
                row += 1;
                printString(0, row++, "┌─ Suggestions ───────────────────────────────┐");
                for (String suggestion : suggestions) {
                    printString(2, row++, "• " + suggestion);
                }
                printString(0, row++, "└" + "─".repeat(45) + "┘");
            }

            // Display error message if there is one
            if (currentError != null) {
                TerminalSize size = screen.getTerminalSize();
                row = size.getRows() - 3;
                printString(0, row, "┌─ Log ─────────────────────────────────────┐");
                // Split error message into lines and display each line
                String[] errorLines = currentError.split("\n");
                for (int i = 0; i < errorLines.length; i++) {
                    printString(2, row + 1 + i, "• " + errorLines[i]);
                }
                printString(0, row + errorLines.length + 1, "└" + "─".repeat(45) + "┘");
            }

            // Set cursor position for input
            screen.setCursorPosition(new TerminalPosition(cursorPosition + 2, row - suggestions.size() - 1));
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays a list of movie title suggestions for autocomplete functionality.
     * These suggestions help players find valid movie titles more easily.
     *
     * @param suggestions List of movie title suggestions to display
     */
    public void displayAutocompleteSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }

    /**
     * Displays an error message to the user.
     * Used for invalid moves, movie not found, or other game-related errors.
     *
     * @param error The error message to display
     */
    public void displayError(String error) {
        this.currentError = error;  // Store the error message
        try {
            displayGameState(gameState);  // Refresh the display with the new error
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets input from the user, typically a movie title.
     * This method handles the user interface for input collection using Lanterna.
     *
     * @return The user's input as a string
     */
    public String getUserInput() {
        try {
            while (running) {
                KeyStroke keyStroke = terminal.readInput();
                if (keyStroke != null) {
                    switch (keyStroke.getKeyType()) {
                        case Character:
                            handleCharacter(keyStroke.getCharacter());
                            // Update suggestions based on current input
                            List<String> newSuggestions = gameState.getMovieDatabase()
                                .getAutocompleteSuggestions(currentInput.toString(), 5);
                            displayAutocompleteSuggestions(newSuggestions);
                            displayGameState(gameState);
                            break;
                        case Backspace:
                            handleBackspace();
                            // Update suggestions after backspace too
                            newSuggestions = gameState.getMovieDatabase()
                                .getAutocompleteSuggestions(currentInput.toString(), 5);
                            displayAutocompleteSuggestions(newSuggestions);
                            displayGameState(gameState);
                            break;
                        case Enter:
                            String input = currentInput.toString();
                            currentInput = new StringBuilder();
                            cursorPosition = 0;
                            return input;
                        case EOF:
                        case Escape:
                            running = false;
                            return null;
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Updates the display of the game timer.
     * This method updates the visual representation of the remaining time.
     *
     * @param seconds The number of seconds remaining in the current turn
     */
    public void updateTimer(int seconds) {
        // Timer is now handled in displayGameState
    }

    /**
     * Handles character input from the user.
     * @param c The character to handle
     */
    private void handleCharacter(char c) {
        currentInput.insert(cursorPosition, c);
        cursorPosition++;
    }

    /**
     * Handles backspace input from the user.
     */
    private void handleBackspace() {
        if (cursorPosition > 0) {
            currentInput.deleteCharAt(cursorPosition - 1);
            cursorPosition--;
        }
    }

    /**
     * Prints a string at the specified position on the screen.
     * @param column The column to start printing at
     * @param row The row to print on
     * @param text The text to print
     */
    private void printString(int column, int row, String text) {
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            // Handle special characters
            if (c == '²') {
                c = '2'; // Replace squared with regular 2
            }
            screen.setCharacter(column + i, row,
                TextCharacter.fromCharacter(c)[0]
                    .withForegroundColor(TextColor.ANSI.WHITE)
                    .withBackgroundColor(TextColor.ANSI.BLACK));
        }
    }

    /**
     * Closes the terminal and screen.
     * @throws IOException if there's an error closing the terminal
     */
    public void close() throws IOException {
        if (displayTimer != null) {
            displayTimer.shutdown();
        }
        screen.close();
        terminal.close();
    }
}
