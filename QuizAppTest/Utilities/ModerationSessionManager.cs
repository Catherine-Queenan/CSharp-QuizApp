using System.Data;
using System.Text.Json.Nodes;
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
        { }
    }

    public ModerationSessionManager(DatabaseUtil databaseUtil)
    {
        _databaseUtil = databaseUtil;
        init(databaseUtil);
    }

    public string StartModeratedSession(string moderatorId, string quizName)
    {
        string sessionId = null;
        string insertSessionSQL = "INSERT INTO moderated_sessions (moderator_id, quiz_name) VALUES (@moderatorId, @quizName);";
        string getLastInsertIdSQL = "SELECT LAST_INSERT_ID();";

        try
        {
            using (var connection = _databaseUtil.GetConnection())
            {
                connection.Open(); // Ensure the connection is open
                using (var command = connection.CreateCommand())
                {
                    // Insert the session into the database
                    command.CommandText = insertSessionSQL;
                    command.Parameters.Add(new MySqlParameter("@moderatorId", moderatorId));
                    command.Parameters.Add(new MySqlParameter("@quizName", quizName));
                    command.ExecuteNonQuery();

                    // Retrieve the last inserted ID
                    command.CommandText = getLastInsertIdSQL;
                    command.Parameters.Clear(); // Clear parameters since it's a new command
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
                using (var command = connection.CreateCommand())
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
                connection.Open(); // Ensure the connection is open
                using (var command = connection.CreateCommand())
                {
                    command.CommandText = query;

                    using (var reader = command.ExecuteReader())
                    {
                        while (reader.Read())
                        {
                            string moderator = reader["moderator_id"].ToString();
                            string sessionId = reader["session_id"].ToString();
                            string quizName = reader["quiz_name"].ToString();

                            var session = new ModerationSession(moderator, sessionId, quizName);
                            sessions.Add(session);
                            Console.WriteLine($"Fetched: {session}");
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine($"An error occurred while retrieving active sessions: {ex.Message}\n{ex.StackTrace}");
        }

        return sessions;
    }

    public ModerationSession GetModeratedSession(string sessionId, string quizName)
    {
        ModerationSession session = null;
        string query = "SELECT * FROM moderated_sessions WHERE session_id = @sessionId AND quiz_name = @quizName";

        //string query = "SELECT * FROM moderated_sessions";
        try
        {
            using (var connection = _databaseUtil.GetConnection())
            {
                connection.Open(); // Ensure the connection is open
                using (var command = connection.CreateCommand())
                {
                    command.CommandText = query;
                    command.Parameters.Add(new MySqlParameter("@sessionId", sessionId));
                    command.Parameters.Add(new MySqlParameter("@quizName", quizName));

                    using (var reader = command.ExecuteReader())
                    {
                        if (reader.Read())
                        {
                            string moderator = reader["moderator_id"].ToString();
                            //string sessionId = reader["session_id"].ToString();
                            //string quizName = reader["quiz_name"].ToString();

                            session = new ModerationSession(moderator, sessionId, quizName);
                            //sessions.Add(session);
                            Console.WriteLine($"Fetched: {session}");
                        }
                    }
                }
            }
        }

        //try
        //{
        //    // Log the input values for debugging
        //    Console.WriteLine($"Session ID: {sessionId}, Quiz Name: {quizName}");

        //    using (var connection = _databaseUtil.GetConnection())
        //    {
        //        using (var command = connection.CreateCommand())
        //        {
        //            command.CommandText = query;

        //            // Adjust sessionId to be a string if it's not an integer
        //            command.Parameters.Add(new MySqlParameter("@sessionId", sessionId));
        //            command.Parameters.Add(new MySqlParameter("@quizName", quizName));

        //            using (var comToGetModeratorId = connection.CreateCommand())
        //            {
        //                string moderatorIdFromDB = "SELECT moderator_id FROM moderated_sessions WHERE session_id = @sessionId AND quiz_name = @quizName;";
        //                comToGetModeratorId.CommandText = moderatorIdFromDB;
        //                comToGetModeratorId.Parameters.Add(new MySqlParameter("@sessionId", sessionId));
        //                comToGetModeratorId.Parameters.Add(new MySqlParameter("@quizName", quizName));

        //                using(var reader = comToGetModeratorId.ExecuteReader())
        //                {
        //                    if (reader.Read())
        //                    {
        //                        string moderator = reader["moderator_id"].ToString();
        //                        session = new ModerationSession(moderator, sessionId, quizName);
        //                    }
        //                }
        //            }

        //            //using (var reader = command.ExecuteReader())
        //            //{
        //            //    if (reader.Read())
        //            //    {
        //            //        var rawModerator = reader["moderator_id"];
        //            //        Console.WriteLine($"Raw Moderator Value: {rawModerator}");

        //            //        string moderator = rawModerator != DBNull.Value ? rawModerator.ToString() : "Unknown";
        //            //        session = new ModerationSession(moderator, sessionId, quizName);

        //            //        Console.WriteLine($"Moderator: {moderator}, Session ID: {sessionId}, Quiz Name: {quizName}");
        //            //    }
        //            //    else
        //            //    {
        //            //        Console.WriteLine("No session found matching the criteria.");
        //            //    }
        //            //}
        //        }
        //    }
        //}
        catch (Exception ex)
        {
            Console.WriteLine($"An error occurred while retrieving the moderated session: {ex.Message}");
        }

        return session;
    }


}
