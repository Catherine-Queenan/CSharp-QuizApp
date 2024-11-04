import java.time.LocalDateTime;

public class ModerationSession {
    // Private data members
    private String moderator;
    private String sessionId;
    private String quizName;
    private LocalDateTime createdTime;

    // Constructor
    public ModerationSession(String moderator, String sessionId, String quizName) {
        this.moderator = moderator;
        this.sessionId = sessionId;
        this.quizName = quizName;
        this.createdTime = LocalDateTime.now();
    }

    // Getters
    public String getModerator() {
        return moderator;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getQuizName() {
        return quizName;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
}
