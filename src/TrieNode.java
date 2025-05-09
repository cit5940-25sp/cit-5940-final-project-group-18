import java.util.HashMap;
import java.util.Map;

/**
 * Represents a node in a Trie data structure used for efficient prefix-based movie title searches.
 * Each node in the trie represents a character in a movie title, with children nodes representing
 * possible next characters. This structure enables fast autocomplete functionality by allowing
 * quick traversal of all possible completions of a given prefix.
 * 
 * The trie structure is particularly useful for:
 * - Fast prefix-based searches
 * - Efficient storage of movie titles with common prefixes
 * - Quick retrieval of all movies starting with a given prefix
 */
public class TrieNode {
    /**
     * Maps characters to their corresponding child nodes.
     * Each character in a movie title is represented by a path through these children.
     * The HashMap provides O(1) access to child nodes.
     */
    Map<Character, TrieNode> children = new HashMap<>();
    
    /**
     * Indicates whether this node represents the end of a complete movie title.
     * When true, this node marks the completion of a valid movie title in the trie.
     */
    boolean isEndOfWord = false;
    
    /**
     * Stores the complete word (movie title) at this node.
     * This is useful for:
     * - Quick retrieval of the complete title when a prefix matches
     * - Avoiding the need to reconstruct the word by traversing back up the trie
     * - Providing richer autocomplete suggestions
     * 
     * Note: This field is optional and can be null for intermediate nodes
     * that are part of longer words but don't represent complete titles themselves.
     */
    String word = null;
}
