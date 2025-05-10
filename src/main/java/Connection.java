/**
 * Represents a connection between two movies through a common person (actor or director).
 * This class is used to validate and track the relationships between movies in the game.
 */
public class Connection {
    /** The first movie in the connection */
    private Movie sourceMovie;
    
    /** The second movie in the connection */
    private Movie targetMovie;
    
    /** The person (actor or director) who connects the two movies */
    private Person connector;
    
    /** The type of connection, either "actor" or "director" */
    private String connectionType;

    /**
     * Constructs a new Connection between two movies through a common person.
     *
     * @param sourceMovie The first movie in the connection
     * @param targetMovie The second movie in the connection
     * @param connector The person (actor or director) connecting the two movies
     * @param connectionType The type of connection ("actor" or "director")
     */
    public Connection(Movie sourceMovie, Movie targetMovie, Person connector, String connectionType) {
        this.sourceMovie = sourceMovie;
        this.targetMovie = targetMovie;
        this.connector = connector;
        this.connectionType = connectionType;
    }

    /**
     * Validates whether the connection is valid based on the connection type.
     * For actor connections, checks if the connector appears in both movies' cast.
     * For director connections, checks if the connector appears in both movies' crew.
     *
     * @return true if the connection is valid, false otherwise
     */
    public boolean isValid() {
        // Basic validation: check if connector is in both movies in the specified role
        if (connectionType.equalsIgnoreCase("actor")) {
            return sourceMovie.getCast().contains(connector) && targetMovie.getCast().contains(connector);
        } else if (connectionType.equalsIgnoreCase("director")) {
            return sourceMovie.getCrew().contains(connector) && targetMovie.getCrew().contains(connector);
        }
        return false;
    }

    /**
     * Gets the source movie of the connection.
     *
     * @return The first movie in the connection
     */
    public Movie getSourceMovie() { 
        return sourceMovie; 
    }

    /**
     * Gets the target movie of the connection.
     *
     * @return The second movie in the connection
     */
    public Movie getTargetMovie() { 
        return targetMovie; 
    }

    /**
     * Gets the person connecting the two movies.
     *
     * @return The actor or director who connects the movies
     */
    public Person getConnector() { 
        return connector; 
    }

    /**
     * Gets the type of connection.
     *
     * @return The connection type ("actor" or "director")
     */
    public String getConnectionType() { 
        return connectionType; 
    }

    /**
     * Gets a human-readable description of how the movies are connected.
     * The description includes the connector's name and their role in both movies.
     *
     * @return A string describing the connection between the movies
     */
    public String getDescription() {
        return String.format("%s (%s)", connector.getName(), connectionType);
    }
}
