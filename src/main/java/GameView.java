import java.util.List;

public class GameView {
    public void displayGameState(GameState state) {
        // Render game state to UI
    }

    public void displayAutocompleteSuggestions(List<String> suggestions) {
        // Render suggestions
    }

    public void displayError(String error) {
        // Render error message
        System.out.println(error);
    }

    public String getUserInput() {
        // Get input from user
        return "";
    }

    public void updateTimer(int seconds) {
        // Update timer display
    }

    public String render(GameState state) {
        StringBuilder sb = new StringBuilder();
        sb.append("Current Player: ").append(state.getCurrentPlayer().getName()).append("\n");
        sb.append("Round: ").append(state.getRoundCount()).append("\n");
        sb.append("Timer: ").append(state.getTimer()).append("s\n");
        sb.append("\nPlayers:\n");
        for (Player player : state.getPlayers()) {
            sb.append("- ").append(player.getName())
              .append(" (Progress: ").append(player.getProgress()).append("%)\n");
        }
        sb.append("\nPlayed Movies:\n");
        for (Movie movie : state.getPlayedMovies()) {
            sb.append("- ").append(movie.getTitle()).append(" (Year: ")
              .append(movie.getYear()).append(")\n");
        }
        return sb.toString();
    }
}
