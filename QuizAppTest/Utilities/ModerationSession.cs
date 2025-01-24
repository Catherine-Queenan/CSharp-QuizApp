using System;

public class ModerationSession
{
    // Private data members
    private string moderator;
    private string sessionId;
    private string quizName;
    private DateTime createdTime;

    // Constructor
    public ModerationSession(string moderator, string sessionId, string quizName)
    {
        this.moderator = moderator;
        this.sessionId = sessionId;
        this.quizName = quizName;
        this.createdTime = DateTime.Now;
    }

    // Getters
    public string Moderator => moderator;
    public string SessionId => sessionId;
    public string QuizName => quizName;
    public DateTime CreatedTime => createdTime;
}