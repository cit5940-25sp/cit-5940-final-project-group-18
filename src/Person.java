import java.util.Objects;

/**
 * Represents a person involved in a movie, such as an actor or crew member.
 * This class is used to track individuals who can create connections between movies
 * in the movie connection game.
 * 
 * The class implements proper equals() and hashCode() methods to ensure that
 * people with the same name are considered equal, regardless of their role or ID.
 * This is important for finding connections between movies based on shared people.
 * 
 * Note: The class is designed to be immutable, with all fields being final
 * and only accessible through getter methods.
 */
public class Person {
    /**
     * Unique identifier for the person in the database.
     * This ID is used for database operations but is not considered
     * in equality comparisons.
     */
    private int id;
    
    /**
     * The person's full name.
     * This is the primary field used for equality comparisons and
     * is used to identify connections between movies.
     */
    private String name;
    
    /**
     * The person's role in the movie (e.g., "actor", "director", "character name").
     * This provides context about how the person is involved in the movie
     * but is not considered in equality comparisons.
     */
    private String role;

    /**
     * Constructs a new Person with the specified attributes.
     *
     * @param id The unique identifier for the person
     * @param name The person's full name
     * @param role The person's role in the movie
     */
    public Person(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    /**
     * Compares this person with another object for equality.
     * Two people are considered equal if they have the same name,
     * regardless of their ID or role.
     *
     * @param o The object to compare with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Objects.equals(name, person.name);
    }

    /**
     * Generates a hash code for this person.
     * The hash code is based solely on the person's name to maintain
     * consistency with the equals() method.
     *
     * @return A hash code value for this person
     */
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    /**
     * Gets the unique identifier for this person.
     *
     * @return The person's ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the person's full name.
     *
     * @return The person's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the person's role in the movie.
     *
     * @return The person's role
     */
    public String getRole() {
        return role;
    }
}
