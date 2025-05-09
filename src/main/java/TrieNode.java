import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    Map<Character, TrieNode> children = new HashMap<>();
    boolean isEndOfWord = false;
    // Optionally, you can store the full word or a reference to the Movie for richer suggestions
    String word = null;
}
