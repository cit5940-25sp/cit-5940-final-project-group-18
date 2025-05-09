public class Connection {
    private Movie sourceMovie;
    private Movie targetMovie;
    private Person connector; // The person (actor/director) connecting the two movies
    private String connectionType; // e.g., "actor", "director"

    public Connection(Movie sourceMovie, Movie targetMovie, Person connector, String connectionType) {
        this.sourceMovie = sourceMovie;
        this.targetMovie = targetMovie;
        this.connector = connector;
        this.connectionType = connectionType;
    }

    public boolean isValid() {
        // Basic validation: check if connector is in both movies in the specified role
        if (connectionType.equalsIgnoreCase("actor")) {
            return sourceMovie.getCast().contains(connector) && targetMovie.getCast().contains(connector);
        } else if (connectionType.equalsIgnoreCase("director")) {
            return sourceMovie.getCrew().contains(connector) && targetMovie.getCrew().contains(connector);
        }
        return false;
    }

    // Getters
    public Movie getSourceMovie() { return sourceMovie; }
    public Movie getTargetMovie() { return targetMovie; }
    public Person getConnector() { return connector; }
    public String getConnectionType() { return connectionType; }
}
