import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextCharacter;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.input.KeyType;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handles the display and user interface aspects of the movie connection game.
 * This class is responsible for rendering the game state, displaying messages,
 * and handling user input. It serves as the view component in the MVC pattern.
 * Uses Lanterna for terminal-based UI with improved anti-flicker mechanisms.
 */
public class GameView {
    private Terminal terminal;
    private Screen screen;
    private StringBuilder currentInput = new StringBuilder();
    private StringBuilder currentInput2 = new StringBuilder();
    private List<String> suggestions = List.of();
    private int cursorPosition = 0;
    private boolean running = true;
    private GameState gameState;
    private String currentError = null;
    private boolean showingGameResults = false;
    private AtomicBoolean refreshRequested = new AtomicBoolean(false);
    private Thread refreshThread;

    /**
     * Constructs a new GameView and initializes the terminal screen.
     * Sets up a dedicated refresh thread to avoid screen flicker.
     * 
     * @throws IOException if there's an error initializing the terminal
     */
    public GameView() throws IOException {
        terminal = new DefaultTerminalFactory().createTerminal();
        screen = new TerminalScreen(terminal);
        screen.startScreen();

        // Instead of a scheduled executor, use a dedicated refresh thread
        refreshThread = new Thread(() -> {
            while (running) {
                try {
                    // Only refresh when explicitly requested
                    if (refreshRequested.get() && !showingGameResults && gameState != null) {
                        renderScreen();
                        refreshRequested.set(false);
                    }
                    Thread.sleep(20); // Short sleep to avoid CPU hogging
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    /**
     * Sets the game state to be displayed and requests a refresh.
     * 
     * @param gameState The game state to be displayed
     */
    public void setGameState(GameState gameState) {
        this.gameState = gameState;
        requestRefresh();
    }

    /**
     * Requests a screen refresh when changes occur.
     * The actual refresh will happen on the refresh thread.
     */
    private void requestRefresh() {
        refreshRequested.set(true);
    }

    /**
     * Renders the entire screen at once to prevent flickering.
     * This is called only by the refresh thread.
     */
    private synchronized void renderScreen() {
        try {
            screen.clear();

            // Only continue if we have a valid game state
            if (gameState == null) {
                screen.refresh();
                return;
            }

            // Display timer and current player at the top with a border
            String header = String.format("Round: %d | Time: %ds | Current Player: %s",
                    gameState.getRoundCount(), gameState.getTimer(), gameState.getCurrentPlayer().getName());
            printString(0, 0, "╔" + "═".repeat(header.length() + 2) + "╗");
            printString(0, 1, "║ " + header + " ║");
            printString(0, 2, "╚" + "═".repeat(header.length() + 2) + "╝");

            // Display movie history with a section header
            int row = 4;
            printString(0, row++, "┌─ Movie History ──────────────────────────────┐");
            List<Movie> movies = gameState.getPlayedMovies();
            if (movies.isEmpty()) {
                printString(2, row++, "No movies played yet");
            } else {
                TerminalSize size = screen.getTerminalSize();
                int maxLineLength = size.getColumns() - 4; // 2 for indent, 2 for bullet and space
                // Only show the last 5 movies
                int startIndex = Math.max(0, movies.size() - 5);
                for (int i = startIndex; i < movies.size(); i++) {
                    Movie movie = movies.get(i);
                    String genres = String.join(", ", movie.getGenres());
                    // Dynamically allocate width: title (max 20), year (4), rest for genres
                    int titleWidth = Math.min(20, Math.max(10, maxLineLength - 35)); // at least 10, at most 20
                    String title = String.format("%-" + titleWidth + "." + titleWidth + "s", movie.getTitle());
                    String year = String.format("%4d", movie.getYear());
                    String base = String.format("• %s (Year: %s, Genres: ", title, year);
                    int maxGenresLength = maxLineLength - base.length() - 1; // -1 for closing parenthesis
                    String genresDisplay = genres.length() > maxGenresLength && maxGenresLength > 3
                            ? genres.substring(0, maxGenresLength - 3) + "..."
                            : genres;
                    String movieInfo = base + genresDisplay + ")";
                    printString(2, row++, movieInfo);
                    // Show connection to previous movie if it exists
                    if (i > startIndex) {
                        Movie prevMovie = movies.get(i - 1);
                        Connection connection = gameState.getMovieDatabase().validateConnection(prevMovie, movie);
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
            String winCondition = String.format("Win Condition: %s",
                    gameState.getPlayers().get(0).getWinStrategy().getDescription());
            printString(2, row++, "• " + winCondition);

            // Display each player's progress
            for (Player player : gameState.getPlayers()) {
                String status = String.format("%s: %d%%", player.getName(), player.getProgress());
                printString(2, row++, "• " + status);
            }
            printString(0, row++, "└" + "─".repeat(45) + "┘");

            // Display input area with a prompt
            row += 1;
            printString(0, row, "┌─ Enter Movie Title ──────────────────────────┐");
            row += 1;
            printString(2, row, "> " + currentInput.toString());

            // Track the input row position for cursor positioning later
            int inputRow = row;

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
                row = size.getRows() - 5; // Position error at bottom of screen
                printString(0, row, "┌─ Log ─────────────────────────────────────┐");
                // Split error message into lines and display each line
                String[] errorLines = currentError.split("\n");
                for (int i = 0; i < Math.min(errorLines.length, 3); i++) { // Show up to 3 lines of error message
                    printString(2, row + 1 + i, "• " + errorLines[i]);
                }
                printString(0, row + Math.min(errorLines.length, 3) + 1, "└" + "─".repeat(45) + "┘");
            }

            // Set cursor position for input
            screen.setCursorPosition(new TerminalPosition(cursorPosition + 4, inputRow));

            // Single refresh at the end
            screen.refresh();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Displays the current state of the game by requesting a refresh.
     * This is a non-blocking call that just triggers the refresh thread.
     *
     * @param state The current state of the game to be displayed
     */
    public void displayGameState(GameState state) {
        // Don't update game state if we're showing results
        if (showingGameResults) {
            return;
        }

        this.gameState = state;
        requestRefresh();
    }

    /**
     * Displays a list of movie title suggestions for autocomplete functionality.
     * These suggestions help players find valid movie titles more easily.
     *
     * @param suggestions List of movie title suggestions to display
     */
    public void displayAutocompleteSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
        requestRefresh();
    }

    /**
     * Displays an error message to the user.
     * Used for invalid moves, movie not found, or other game-related errors.
     *
     * @param error The error message to display
     */
    public void displayError(String error) {
        this.currentError = error; // Store the error message
        System.out.println("ERROR LOG: " + error); // Debug log to console
        requestRefresh();
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
                    boolean needsRefresh = false;

                    switch (keyStroke.getKeyType()) {
                        case Character:
                            handleCharacter(keyStroke.getCharacter());
                            // Update suggestions based on current input
                            List<String> newSuggestions = gameState.getMovieDatabase()
                                    .getAutocompleteSuggestions(currentInput.toString(), 5);
                            suggestions = newSuggestions;
                            needsRefresh = true;
                            break;
                        case Backspace:
                            handleBackspace();
                            // Update suggestions after backspace too
                            newSuggestions = gameState.getMovieDatabase()
                                    .getAutocompleteSuggestions(currentInput.toString(), 5);
                            suggestions = newSuggestions;
                            needsRefresh = true;
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

                    if (needsRefresh) {
                        requestRefresh();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Handles character input from the user.
     * 
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
     * 
     * @param column The column to start printing at
     * @param row    The row to print on
     * @param text   The text to print
     */
    private void printString(int column, int row, String text) {
        try {
            TerminalSize size = screen.getTerminalSize();
            if (row >= size.getRows() || column >= size.getColumns()) {
                return; // Skip if out of bounds
            }

            // Limit text length to prevent going off screen
            int maxLength = size.getColumns() - column;
            String displayText = text.length() > maxLength ? text.substring(0, maxLength) : text;

            for (int i = 0; i < displayText.length(); i++) {
                if (column + i >= size.getColumns()) {
                    break; // Prevent writing outside terminal bounds
                }

                char c = displayText.charAt(i);
                // Handle special characters
                if (c == '²') {
                    c = '2'; // Replace squared with regular 2
                }
                screen.setCharacter(column + i, row,
                        TextCharacter.fromCharacter(c)[0]
                                .withForegroundColor(TextColor.ANSI.WHITE)
                                .withBackgroundColor(TextColor.ANSI.BLACK));
            }
        } catch (Exception e) {
            // Silently handle any unexpected rendering errors
            System.err.println("Error in printString: " + e.getMessage());
        }
    }

    /**
     * Closes the terminal and screen.
     * Cleanly stops the refresh thread.
     * 
     * @throws IOException if there's an error closing the terminal
     */
    public void close() throws IOException {
        running = false;
        try {
            if (refreshThread != null && refreshThread.isAlive()) {
                refreshThread.interrupt();
                refreshThread.join(1000); // Wait up to 1 second for thread to terminate
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        screen.close();
        terminal.close();
    }

    /**
     * Gets input from the user for winning strategy.
     * Displays a prompt for winning strategy input.
     * Uses currentInput2 for input collection.
     * 
     * @return The user's chosen winning strategy as a string.
     */
    public String getStrategyInput() {
        currentInput2 = new StringBuilder();
        try {
            // Clear screen once at beginning
            screen.clear();

            int row = 5; // Place the prompt near the top
            printString(0, row, "┌─Please choose your strategy first: Genre/Actor/Director────┐");
            row++;
            printString(2, row, "> " + currentInput2.toString());
            printString(0, row + 1, "└" + "─".repeat(60) + "┘");
            screen.refresh();

            while (true) {
                KeyStroke keyStroke = terminal.readInput();
                if (keyStroke != null) {
                    switch (keyStroke.getKeyType()) {
                        case Character:
                            currentInput2.append(keyStroke.getCharacter());
                            // Clear the line before printing new content
                            printString(2, row, "> " + " ".repeat(50));
                            printString(2, row, "> " + currentInput2.toString());
                            screen.refresh();
                            break;
                        case Backspace:
                            if (currentInput2.length() > 0) {
                                currentInput2.deleteCharAt(currentInput2.length() - 1);
                                // Clear the line before printing new content
                                printString(2, row, "> " + " ".repeat(50));
                                printString(2, row, "> " + currentInput2.toString());
                                screen.refresh();
                            }
                            break;
                        case Enter:
                            return currentInput2.toString();
                        default:
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Displays the final game results showing who won and lost.
     * Uses a dedicated display area similar to strategy selection.
     * 
     * @param winner    The winning player, or null if there's no winner
     * @param players   List of all players to show their final status
     * @param isTimeout True if the game ended due to timeout, false if it ended due
     *                  to win condition
     */
    public void displayGameResults(Player winner, List<Player> players, boolean isTimeout) {
        try {
            // Set flag to indicate we're showing results
            showingGameResults = true;

            // Clear screen once
            screen.clear();

            // Display a game over header
            int row = 2;
            printString(0, row++, "┌─ Game Over ─────────────────────────────────┐");

            // Display the winner if there is one
            if (winner != null) {
                printString(2, row++, "• Winner: " + winner.getName());
                if (isTimeout) {
                    printString(2, row++, "• Victory by TIMEOUT - other player ran out of time");
                } else {
                    printString(2, row++, "• Victory by completing the win condition:");
                    printString(2, row++, "  " + winner.getWinStrategy().getDescription());
                }
            } else {
                printString(2, row++, "• No winner - Game ended in a draw");
            }

            // Display final scores for all players
            row += 1;
            printString(0, row++, "┌─ Final Scores ─────────────────────────────┐");
            for (Player player : players) {
                String status = String.format("%s: %d%%", player.getName(), player.getProgress());
                printString(2, row++, "• " + status);
            }
            printString(0, row++, "└" + "─".repeat(45) + "┘");

            // Display a prompt to press ESC to exit
            row += 2;
            printString(0, row, "Press ESC to exit...");

            // Ensure the cursor is visible and in a good spot
            screen.setCursorPosition(new TerminalPosition(row + 1, row + 1));
            screen.refresh();

            // Wait for ESC key
            while (true) {
                KeyStroke keyStroke = terminal.readInput();
                if (keyStroke != null && keyStroke.getKeyType() == KeyType.Escape) {
                    break;
                }
            }

            // Reset the showingGameResults flag (though not really needed at this point)
            showingGameResults = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Clears the current error log.
     */
    public void clearError() {
        this.currentError = null;
        requestRefresh();
    }
}