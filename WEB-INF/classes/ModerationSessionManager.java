import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

// Assuming you have a DatabaseConnection class for managing your DB connections
public class ModerationSessionManager {

        // Method to start a moderated session
        public static String startModeratedSession(String moderatorId, String quizName) {
            String sessionId = null;
            String insertSessionSQL = "INSERT INTO moderated_sessions (moderator_id, quiz_name) VALUES (?, ?)";

            try (Connection connection = DatabaseUtil.getConnection();
                 PreparedStatement preparedStatement = connection.prepareStatement(insertSessionSQL, PreparedStatement.RETURN_GENERATED_KEYS)) {
                
                preparedStatement.setString(1, moderatorId); 
                preparedStatement.setString(2, quizName);
                int affectedRows = preparedStatement.executeUpdate();
    
                // Retrieve the generated session ID
                if (affectedRows > 0) {
                    try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                        if (generatedKeys.next()) {
                            sessionId = String.valueOf(generatedKeys.getInt(1)); // Assuming session_id is of type INT
                        }
                    }
                }
            } catch (SQLException e) {
                System.err.println("An error occurred while starting the moderated session: " + e.getMessage());
                e.printStackTrace();
            }
    
            if (sessionId != null) {
                return sessionId; // Return the newly created session ID
            } else {
                System.out.println("Failed to generate a session ID");
                return null; // Return null if the session ID is not generated
            }    
        }
    

    // Method to end a moderated session
    public static void endModeratedSession(String sessionId) {
        String deleteSessionSQL = "DELETE FROM moderated_sessions WHERE session_id = ?";

        try (Connection connection = DatabaseUtil.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteSessionSQL)) {
            
            preparedStatement.setInt(1, Integer.parseInt(sessionId)); // Assuming session_id is of type INT
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (log them, rethrow, etc.)
        }
    }

    public static List<ModerationSession> getActiveSessions() throws SQLException {
        List<ModerationSession> sessions = new ArrayList<>();
        String query = "SELECT * FROM moderated_sessions"; // Adjust this query according to your schema
    
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String moderator = rs.getString("moderator_id");  // Assuming the column name is 'moderator'
                String sessionId = rs.getString("session_id"); // Assuming the column name is 'session_id'
                String quizName = rs.getString("quiz_name"); // Assuming the column name is 'quiz_name'
                // Create and add ModeratedSession objects to the list
                ModerationSession session = new ModerationSession(moderator, sessionId, quizName);
                sessions.add(session);
            }
        }
        return sessions;
    }

    public static ModerationSession getModeratedSession(String sessionId, String quizName) {
        ModerationSession session = null;
        String query = "SELECT * FROM moderated_sessions WHERE session_id = ? AND quiz_name = ?"; // Adjust this query according to your schema
    
        try (Connection conn = DatabaseUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(sessionId)); // Assuming session_id is of type INT
            stmt.setString(2, quizName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String moderator = rs.getString("moderator_id");  // Assuming the column name is 'moderator'
                    // Create a ModeratedSession object
                    session = new ModerationSession(moderator, sessionId, quizName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions (log them, rethrow, etc.)
        }
        return session;
    }
}
