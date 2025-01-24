﻿using System.Data;
using MySql.Data.MySqlClient;
using QuizApp.Utilities;

public class ModerationSessionManager
{
    private readonly DatabaseUtil _databaseUtil;
    private IDbConnection? _connection;


    public void init(DatabaseUtil databaseUtil)
    {
        try
        {
            _connection = databaseUtil.GetConnection();
            _connection.Open();
        }
        catch (MySqlException ex)
        {

        }
    }

    public ModerationSessionManager(DatabaseUtil databaseUtil)
    {
        _databaseUtil = databaseUtil;
        init(databaseUtil);
    }

    public string StartModeratedSession(string moderatorId, string quizName)
    {
        string sessionId = null;
        string insertSessionSQL = "INSERT INTO moderated_sessions (moderator_id, quiz_name) OUTPUT INSERTED.session_id VALUES (@moderatorId, @quizName)";

        try
        {
            using (var connection = _databaseUtil.GetConnection())
            {
                using (var command = _connection.CreateCommand())
                {
                    command.Parameters.Add(new MySqlParameter("@moderatorId", moderatorId));
                    command.Parameters.Add(new MySqlParameter("@quizName", quizName));
                    sessionId = command.ExecuteScalar()?.ToString();
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"An error occurred while starting the moderated session: {ex.Message}");
        }

        return sessionId;
    }

    public void EndModeratedSession(string sessionId)
    {
        string deleteSessionSQL = "DELETE FROM moderated_sessions WHERE session_id = @sessionId";

        try
        {
            using (var connection = _databaseUtil.GetConnection())
            {
                using (var command = _connection.CreateCommand())
                {
                    command.Parameters.Add(new MySqlParameter("@sessionId", int.Parse(sessionId)));
                    command.ExecuteNonQuery();
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"An error occurred while ending the moderated session: {ex.Message}");
        }
    }

    public List<ModerationSession> GetActiveSessions()
    {
        List<ModerationSession> sessions = new List<ModerationSession>();
        string query = "SELECT * FROM moderated_sessions";

        try
        {
            using (var connection = _databaseUtil.GetConnection())
            {
                using (var command = _connection.CreateCommand())
                {
                    using (var reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            string moderator = reader["moderator_id"].ToString();
                            string sessionId = reader["session_id"].ToString();
                            string quizName = reader["quiz_name"].ToString();

                            sessions.Add(new ModerationSession(moderator, sessionId, quizName));
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"An error occurred while retrieving active sessions: {ex.Message}");
        }

        return sessions;
    }

    public ModerationSession GetModeratedSession(string sessionId, string quizName)
    {
        ModerationSession session = null;
        string query = "SELECT * FROM moderated_sessions WHERE session_id = @sessionId AND quiz_name = @quizName";

        try
        {
            using (var connection = _databaseUtil.GetConnection())
            {
                using (var command = _connection.CreateCommand())
                {
                    command.Parameters.Add(new MySqlParameter("@sessionId", int.Parse(sessionId)));
                    command.Parameters.Add(new MySqlParameter("@quizName", quizName));

                    using (var reader = command.ExecuteReader())
                    {
                        if (reader.Read())
                        {
                            string moderator = reader["moderator_id"].ToString();
                            session = new ModerationSession(moderator, sessionId, quizName);
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"An error occurred while retrieving the moderated session: {ex.Message}");
        }

        return session;
    }
}