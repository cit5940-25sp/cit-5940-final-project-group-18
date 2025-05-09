import java.util.*;

/**
 * Implements a Trie data structure specifically designed for efficient movie title searches
 * and autocomplete functionality. The trie provides fast prefix-based lookups and is
 * case-insensitive to improve user experience.
 * 
 * Key features:
 * - Case-insensitive movie title storage and retrieval
 * - Efficient prefix-based searches
 * - Limited result set size for autocomplete suggestions
 * - Full word storage for quick retrieval
 * 
 * The trie structure enables O(m) search time where m is the length of the prefix,
 * making it ideal for real-time autocomplete functionality.
 */
public class MovieTrie {
    /**
     * The root node of the trie.
     * All movie titles are stored as paths starting from this root node.
     * Each character in a movie title is represented by a path through the trie.
     */
    private TrieNode root = new TrieNode();

    /**
     * Inserts a movie title into the trie.
     * The title is stored in a case-insensitive manner to improve search flexibility.
     * The original case of the title is preserved in the node's word field.
     *
     * @param word The movie title to insert into the trie
     */
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toLowerCase().toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEndOfWord = true;
        node.word = word; // Store the full word for easy retrieval
    }

    /**
     * Retrieves up to k movie titles that start with the given prefix.
     * The search is case-insensitive to improve user experience.
     * Results are returned in no particular order.
     *
     * @param prefix The prefix to search for (case-insensitive)
     * @param k The maximum number of suggestions to return
     * @return A list of movie titles that start with the given prefix, limited to k results
     */
    public List<String> getWordsWithPrefix(String prefix, int k) {
        List<String> results = new ArrayList<>();
        TrieNode node = root;
        for (char c : prefix.toLowerCase().toCharArray()) {
            node = node.children.get(c);
            if (node == null) return results;
        }
        dfs(node, results, k);
        return results;
    }

    /**
     * Performs a depth-first search to collect movie titles starting from a given node.
     * The search continues until either:
     * - k results are found
     * - all possible paths have been explored
     *
     * @param node The starting node for the search
     * @param results The list to store found movie titles
     * @param k The maximum number of results to collect
     */
    private void dfs(TrieNode node, List<String> results, int k) {
        if (results.size() >= k) return;
        if (node.isEndOfWord && node.word != null) {
            results.add(node.word);
        }
        for (TrieNode child : node.children.values()) {
            dfs(child, results, k);
            if (results.size() >= k) return;
        }
    }
}
