import java.util.*;

public class MovieTrie {
    private TrieNode root = new TrieNode();

    // Insert a word (movie title) into the trie
    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toLowerCase().toCharArray()) {
            node = node.children.computeIfAbsent(c, k -> new TrieNode());
        }
        node.isEndOfWord = true;
        node.word = word; // Store the full word for easy retrieval
    }

    // Get up to k words that start with the given prefix
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

    // Helper DFS to collect words
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
