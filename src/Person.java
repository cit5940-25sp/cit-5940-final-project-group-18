import java.util.List;

public class Person {
    private int id;
    private String name;
    private String role;

    public Person(int id, String name, String role) {
        this.id = id;
        this.name = name;
        this.role = role;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return role;
    }

    public List<Movie> getMovies() {
        // TODO: Implement logic to return movies for this person
        return null;
    }
}
