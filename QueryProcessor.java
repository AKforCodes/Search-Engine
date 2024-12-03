import java.sql.*;
import java.util.*;

public class QueryProcessor {

    public List<String> search(String query) {
        List<String> results = new ArrayList<>();
        String[] words = preprocess(query);
        Map<Integer, Integer> docScores = new HashMap<>();

        try (Connection conn = Database.getConnection()) {
            for (String word : words) {
                int wordId = getWordId(conn, word);
                if (wordId == -1) continue;  // Word not found
                String sql = "SELECT document_id, frequency FROM inverted_index WHERE word_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, wordId);
                    ResultSet rs = pstmt.executeQuery();
                    while (rs.next()) {
                        int docId = rs.getInt("document_id");
                        int freq = rs.getInt("frequency");
                        docScores.put(docId, docScores.getOrDefault(docId, 0) + freq);
                    }
                }
            }
            // Sort documents by score
            List<Map.Entry<Integer, Integer>> sortedDocs = new ArrayList<>(docScores.entrySet());
            sortedDocs.sort((a, b) -> b.getValue() - a.getValue());
            // Retrieve URLs
            String docSql = "SELECT url, title FROM documents WHERE id = ?";
            try (PreparedStatement docStmt = conn.prepareStatement(docSql)) {
                for (Map.Entry<Integer, Integer> entry : sortedDocs) {
                    docStmt.setInt(1, entry.getKey());
                    ResultSet rs = docStmt.executeQuery();
                    if (rs.next()) {
                        String url = rs.getString("url");
                        String title = rs.getString("title");
                        results.add(title + " - " + url);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return results;
    }

    private int getWordId(Connection conn, String word) throws SQLException {
        String sql = "SELECT id FROM words WHERE word = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, word);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        }
        return -1;
    }

    private String[] preprocess(String query) {
        query = query.toLowerCase();
        query = query.replaceAll("[^a-z0-9\\s]", "");
        return query.split("\\s+");
    }
}
