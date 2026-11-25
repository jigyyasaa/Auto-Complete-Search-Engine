import java.util.*;

public class AutoCompleteSearchEngine {

    // Trie Node
    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        boolean isEndOfWord;
    }

    static TrieNode root = new TrieNode();

    // Store word frequencies
    static HashMap<String, Integer> frequency = new HashMap<>();

    // Validate lowercase letters
    static boolean isValid(String word) {
        if (word == null || word.isEmpty()) {
            return false;
        }

        for (int i = 0; i < word.length(); i++) {
            char ch = word.charAt(i);

            if (ch < 'a' || ch > 'z') {
                return false;
            }
        }

        return true;
    }

    // Insert word into Trie
    static void insertIntoTrie(String word) {
        TrieNode current = root;

        for (int i = 0; i < word.length(); i++) {
            int index = word.charAt(i) - 'a';

            if (current.children[index] == null) {
                current.children[index] = new TrieNode();
            }

            current = current.children[index];
        }

        current.isEndOfWord = true;
    }

    // Insert word with frequency
    static void insert(String word, int freq) {
        if (!isValid(word)) {
            System.out.println("Invalid word. Only letters a-z are allowed.");
            return;
        }

        if (freq <= 0) {
            System.out.println("Frequency must be positive.");
            return;
        }

        insertIntoTrie(word);
        frequency.put(word, freq);

        System.out.println("Word inserted: " + word + " (" + freq + ")");
    }

    // Find Trie node for a prefix
    static TrieNode searchPrefix(String prefix) {
        TrieNode current = root;

        for (int i = 0; i < prefix.length(); i++) {
            int index = prefix.charAt(i) - 'a';

            if (current.children[index] == null) {
                return null;
            }

            current = current.children[index];
        }

        return current;
    }

    // Collect all words below a Trie node
    static void collectWords(TrieNode node, StringBuilder word,
                             ArrayList<String> result) {

        if (node == null) {
            return;
        }

        if (node.isEndOfWord) {
            result.add(word.toString());
        }

        for (int i = 0; i < 26; i++) {
            if (node.children[i] != null) {
                word.append((char) ('a' + i));

                collectWords(node.children[i], word, result);

                word.deleteCharAt(word.length() - 1);
            }
        }
    }

    // Sort suggestions using insertion sort
    // Higher frequency first, alphabetical order for ties
    static void sortSuggestions(ArrayList<String> words) {

        for (int i = 1; i < words.size(); i++) {

            String key = words.get(i);
            int j = i - 1;

            while (j >= 0) {

                String current = words.get(j);

                int keyFreq = frequency.get(key);
                int currentFreq = frequency.get(current);

                boolean shouldMove =
                    currentFreq < keyFreq ||
                    (currentFreq == keyFreq &&
                     current.compareTo(key) > 0);

                if (!shouldMove) {
                    break;
                }

                words.set(j + 1, words.get(j));
                j--;
            }

            words.set(j + 1, key);
        }
    }

    // Suggest words starting with a prefix
    static void suggest(String prefix) {

        if (!isValid(prefix)) {
            System.out.println(
                "Invalid prefix. Only letters a-z are allowed."
            );
            return;
        }

        TrieNode node = searchPrefix(prefix);

        if (node == null) {
            System.out.println("No suggestions found.");
            return;
        }

        ArrayList<String> suggestions = new ArrayList<>();

        collectWords(node, new StringBuilder(prefix), suggestions);

        sortSuggestions(suggestions);

        if (suggestions.isEmpty()) {
            System.out.println("No suggestions found.");
            return;
        }

        System.out.print("Suggestions: ");

        for (int i = 0; i < suggestions.size(); i++) {

            String word = suggestions.get(i);

            System.out.print(
                word + " (" + frequency.get(word) + ")"
            );

            if (i < suggestions.size() - 1) {
                System.out.print(", ");
            }
        }

        System.out.println();
    }

    // Substring search (naive method)
    static boolean containsText(String word, String text) {

        if (text.length() > word.length()) {
            return false;
        }

        for (int i = 0; i <= word.length() - text.length(); i++) {

            int j = 0;

            while (j < text.length() &&
                   word.charAt(i + j) == text.charAt(j)) {
                j++;
            }

            if (j == text.length()) {
                return true;
            }
        }

        return false;
    }

    // Find all words containing given text
    static void contains(String text) {

        if (!isValid(text)) {
            System.out.println(
                "Invalid text. Only letters a-z are allowed."
            );
            return;
        }

        ArrayList<String> result = new ArrayList<>();

        for (String word : frequency.keySet()) {

            if (containsText(word, text)) {
                result.add(word);
            }
        }

        // Sort alphabetically
        for (int i = 1; i < result.size(); i++) {

            String key = result.get(i);
            int j = i - 1;

            while (j >= 0 &&
                   result.get(j).compareTo(key) > 0) {

                result.set(j + 1, result.get(j));
                j--;
            }

            result.set(j + 1, key);
        }

        if (result.isEmpty()) {
            System.out.println("No matching words found.");
            return;
        }

        System.out.println(
            "Words containing '" + text + "': "
            + String.join(", ", result)
        );
    }

    // Main method
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        // Initial dictionary
        insert("data", 5);
        insert("database", 9);
        insert("dating", 2);
        insert("dog", 7);

        int choice=-1;

        do {

            System.out.println("\n===== AUTO-COMPLETE SEARCH ENGINE =====");
            System.out.println("1. Insert Word");
            System.out.println("2. Suggest Words");
            System.out.println("5. Contains Search");
            System.out.println("0. Exit");

            System.out.print("Enter your choice: ");

            if (!sc.hasNextInt()) {
                System.out.println("Invalid choice. Enter a number.");
                sc.nextLine();
                continue;
            }

            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {

                case 1:
                    System.out.print("Enter word: ");
                    String word = sc.nextLine();

                    System.out.print("Enter frequency: ");

                    if (!sc.hasNextInt()) {
                        System.out.println("Invalid frequency.");
                        sc.nextLine();
                        break;
                    }

                    int freq = sc.nextInt();
                    sc.nextLine();

                    insert(word, freq);
                    break;

                case 2:
                    System.out.print("Enter prefix: ");
                    String prefix = sc.nextLine();

                    suggest(prefix);
                    break;

                case 5:
                    System.out.print("Enter text to search: ");
                    String text = sc.nextLine();

                    contains(text);
                    break;

                case 0:
                    System.out.println("Exiting Search Engine...");
                    break;

                default:
                    System.out.println("Invalid choice!");
            }

        } while (choice != 0);

        sc.close();
    }
}