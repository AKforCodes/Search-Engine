public class Trie {
    private TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode current = root;
        for (char ch : word.toCharArray()) {
            current = current.children.computeIfAbsent(ch, c -> new TrieNode());
        }
        current.isWord = true;
    }

    public List<String> autocomplete(String prefix) {
        List<String> results = new ArrayList<>();
        TrieNode current = root;
        for (char ch : prefix.toCharArray()) {
            current = current.children.get(ch);
            if (current == null) {
                return results;
            }
        }
        findAllWords(current, prefix, results);
        return results;
    }

    private void findAllWords(TrieNode node, String prefix, List<String> results) {
        if (node.isWord) {
            results.add(prefix);
        }
        for (char ch : node.children.keySet()) {
            findAllWords(node.children.get(ch), prefix + ch, results);
        }
    }
}
