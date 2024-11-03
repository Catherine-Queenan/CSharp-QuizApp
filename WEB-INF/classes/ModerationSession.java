import java.time.LocalDateTime;

public class ModerationSession {
    // Private data members
    private String moderator;
    private String sessionId;
    private LocalDateTime createdTime;

    // Constructor
    public ModerationSession(String moderator, String sessionId) {
        this.moderator = moderator;
        this.sessionId = sessionId;
        this.createdTime = LocalDateTime.now();
    }

    // Getters
    public String getModerator() {
        return moderator;
    }

    public String getSessionId() {
        return sessionId;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }
}
