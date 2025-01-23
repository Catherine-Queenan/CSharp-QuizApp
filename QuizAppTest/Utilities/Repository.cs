using System.Linq;
using System.Security.Policy;
using System.Text.Json.Nodes;
using System.Text;
using System.Data;
using MySql.Data.MySqlClient;
using Org.BouncyCastle.Asn1.Ocsp;
using Microsoft.Extensions.Primitives;
using System.Runtime.InteropServices.JavaScript;

namespace QuizApp.Utilities
{
    public class Repository : IRepository
    {
        private IDbConnection? _connection;

        public void init(DatabaseUtil databaseUtil)
        {
            try
            {
                _connection = databaseUtil.GetConnection();
                _connection.Open();
            } catch(MySqlException ex)
            {
                
            }
        }


        public void close()
        {
            if (_connection != null)
            {
                _connection.Close();
            }
        }

        private void insertCategory(JsonObject entry)
        {
            if(_connection != null) 
            {
                // Insert Category
                var command = _connection.CreateCommand();
                command.CommandText = "INSERT INTO categories (name) VALUES (@name)";
                command.Parameters.Add(new MySqlParameter("@name", entry["name"]?.ToString()));
                command.ExecuteNonQuery();

                string? media_id = entry["id"]?.ToString();
                if (media_id != null && media_id != "")
                {

                    var mediaCommand = _connection.CreateCommand();
                    mediaCommand.CommandText = "INSERT INTO category_media (category_name, media_id) VALUES (@category_name, @media_id)";
                    mediaCommand.Parameters.Add(new MySqlParameter("@category_name", entry["name"]?.ToString()));
                    
                    byte[] media_idBytes = Convert.FromHexString(media_id);
                    mediaCommand.Parameters.Add(new MySqlParameter("@media_id", media_idBytes));
                    
                    mediaCommand.ExecuteNonQuery();
                }

            }
            
        }

        private void insertQuiz(JsonObject entry)
        {
            if (_connection != null)
            {
                // Insert Quiz

                var command = _connection.CreateCommand();
                command.CommandText = "INSERT INTO quizzes (name, category_name, description) VALUES (@name, @category_name, @description)";
                command.Parameters.Add(new MySqlParameter("@name", entry["name"]?.ToString()));
                command.Parameters.Add(new MySqlParameter("@category_name", entry["category_name"]?.ToString()));
                command.Parameters.Add(new MySqlParameter("@description", entry["description"]?.ToString()));
                command.ExecuteNonQuery();

                string? media_id = entry["id"]?.ToString();
                if (media_id != null && media_id != "")
                {
                    // Associate quiz with respective media
                    var mediaCommand = _connection.CreateCommand();
                    mediaCommand.CommandText = "INSERT INTO quiz_media (quiz_name, media_id) VALUES (@quiz_name, @media_id)";
                    mediaCommand.Parameters.Add(new MySqlParameter("@quiz_name", entry["name"]?.ToString()));
    
                    byte[] media_idBytes = Convert.FromHexString(media_id);
                    mediaCommand.Parameters.Add(new MySqlParameter("@media_id", media_idBytes));

                    mediaCommand.ExecuteNonQuery();
                }

            }
            
        }

        private void insertQuestion(JsonObject entry)
        {
            if (_connection != null)
            {
                // Insert Question
                var command = _connection.CreateCommand();
                command.CommandText = "INSERT INTO questions (id, quiz_name, question_text, question_type) VALUES (@id, @quiz_name, @question_text, @question_type)";

                string? id = entry["id"]?.ToString();
                byte[] idBytes = (id != null && id != "") ? Convert.FromHexString(id) : Array.Empty<byte>();
                command.Parameters.Add(new MySqlParameter("@id", idBytes));
                command.Parameters.Add(new MySqlParameter("@quiz_name", entry["quiz_name"]?.ToString()));
                command.Parameters.Add(new MySqlParameter("@question_text", entry["question_text"]?.ToString()));
                command.Parameters.Add(new MySqlParameter("@question_type", entry["question_type"]?.ToString()));
                command.ExecuteNonQuery();

                string? media_id = entry["media_id"]?.ToString();
                if (media_id != null && media_id != "")
                {
                    // Associate question with respective media
                    var mediaCommand = _connection.CreateCommand();
                    mediaCommand.CommandText = "INSERT INTO question_media (question_id, media_id) VALUES (@question_id, @media_id)";

                    mediaCommand.Parameters.Add(new MySqlParameter("@question_id", idBytes));
                    byte[] media_idBytes = Convert.FromHexString(media_id);
                    mediaCommand.Parameters.Add(new MySqlParameter("@media_id", media_idBytes));
                    mediaCommand.ExecuteNonQuery();
                }
            }

        }

        private void insertAnswer(JsonObject entry)
        {
            if (_connection != null)
            {
                // Insert Answer
                var command = _connection.CreateCommand();
                command.CommandText = "INSERT INTO answers (id, question_id, answer_text, is_correct, answer_type) VALUES (@id, @question_id, @answer_text, @is_correct, @answer_type)";
    
                string? id = entry["id"]?.ToString();
                byte[] idBytes = (id != null && id != "") ? Convert.FromHexString(id) : Array.Empty<byte>();
                command.Parameters.Add(new MySqlParameter("@id", idBytes));

                string? question_id = entry["question_id"]?.ToString();
                byte[] question_idBytes = question_id != null && question_id != "" ? Convert.FromHexString(question_id) : Array.Empty<byte>();
                command.Parameters.Add(new MySqlParameter("@question_id", question_idBytes));

                command.Parameters.Add(new MySqlParameter("@answer_text", entry["answer_text"]?.ToString()));

                string is_correct = entry["media_start"]?.ToString() ?? "0";
                command.Parameters.Add(new MySqlParameter("@is_correct", Int32.Parse(is_correct))); // Default to "0" if null
                command.Parameters.Add(new MySqlParameter("@answer_type", entry["answer_type"]?.ToString()));
                command.ExecuteNonQuery();

                string? media_id = entry["media_id"]?.ToString();
                if (media_id != null && media_id != "")
                {
                    // Associate answer with respective media
                    var mediaCommand = _connection.CreateCommand();
                    mediaCommand.CommandText = "INSERT INTO answer_media (answer_id, media_id) VALUES (@answer_id, @media_id)";
        
                    mediaCommand.Parameters.Add(new MySqlParameter("@answer_id", idBytes));
                    byte[] media_idBytes = Convert.FromBase64String(media_id);
                    mediaCommand.Parameters.Add(new MySqlParameter("@media_id", media_idBytes));
                    mediaCommand.ExecuteNonQuery();
                }
            }

        }

        private void insertMedia(JsonObject entry)
        {
            if (_connection != null)
            {
                // Insert Media
                var command = _connection.CreateCommand();
                command.CommandText = "INSERT INTO media (id, description, media_type, media_file_path, media_filename, media_start, media_end) VALUES (@id, @description, @media_type, @media_file_path, @media_filename, @media_start, @media_end)";

                string? id = entry["id"]?.ToString();
                byte[] idBytes = (id != null && id != "") ? Convert.FromHexString(id) : Array.Empty<byte>();
                command.Parameters.Add(new MySqlParameter("@id", idBytes));

                command.Parameters.Add(new MySqlParameter("@description", entry["description"]?.ToString()));
                command.Parameters.Add(new MySqlParameter("@media_type", entry["media_type"]?.ToString()));
                command.Parameters.Add(new MySqlParameter("@media_file_path", entry["media_file_path"]?.ToString()));
                command.Parameters.Add(new MySqlParameter("@media_filename", entry["media_filename"]?.ToString()));

                string media_start = entry["media_start"]?.ToString() ?? "0";
                string media_end = entry["media_end"]?.ToString() ?? "0";

                command.Parameters.Add(new MySqlParameter("@media_start", Int32.Parse(media_start))); // Default to "0" if null
                command.Parameters.Add(new MySqlParameter("@media_end", Int32.Parse(media_end))); // Default to "0" if null
                command.ExecuteNonQuery();
            }

        }

        private void insertWebSocketQuestion(JsonObject entry)
        {
            if (_connection != null)
            {
                // Insert WebsocketQuestion
                var command = _connection.CreateCommand();
                command.CommandText = "INSERT INTO QuestionsWithAnswers (questionId, questionText, answers, indexOfCorrect, quizName) VALUES (@questionId, @questionText, @answers, @indexOfCorrect, @quizName)";

                command.Parameters.Add(new MySqlParameter("@questionId", entry["questionId"]?.ToString()));
                command.Parameters.Add(new MySqlParameter("@questionText", entry["questionText"]?.ToString()));
                command.Parameters.Add(new MySqlParameter("@answers", entry["answers"]?.ToString()));

                string indexOfCorrect = entry["indexOfCorrect"]?.ToString() ?? "0";
                command.Parameters.Add(new MySqlParameter("@indexOfCorrect", Int32.Parse(indexOfCorrect))); // Default to "0" if null
                command.Parameters.Add(new MySqlParameter("@quizName", entry["quizName"]?.ToString()));
                command.ExecuteNonQuery();
            }

        }

        private string createConstructorParameters(Dictionary<String, Object> rs, string tableType)
        {
            StringBuilder parameters = new("");
                string prefix = "";
                foreach (var column in rs)
                {
                    string colName = column.Key;
                    parameters.Append(prefix).Append(colName).Append(":==");
                    prefix = ",,,";
                    if (colName == "id" || colName.Contains("_id"))
                    {
                        byte[]? id = (byte[])column.Value;
                        parameters.Append(BitConverter.ToString(id).Replace("-",""));
                    }
                    else
                    {
                        string? entry = column.Value.ToString();
                        parameters.Append(entry);
                    }
                }


                if(_connection != null)
                {
                    var command = _connection.CreateCommand();
                    switch (tableType)
                    {
                        case "category":
                            command.CommandText = "SELECT media_id FROM category_media WHERE category_name = @category_name";
                            command.Parameters.Add(new MySqlParameter("@category_name", rs["name"]));
                            break;
                        case "quiz":
                            command.CommandText = "SELECT media_id FROM quiz_media WHERE quiz_name = @quiz_name";
                            command.Parameters.Add(new MySqlParameter("@quiz_name", rs["name"]));
                            break;
                        case "question":
                            command.CommandText = "SELECT media_id FROM question_media WHERE question_id = @question_id";
                            command.Parameters.Add(new MySqlParameter("@question_id", rs["id"]));
                            break;
                        case "answer":
                            command.CommandText = "SELECT media_id FROM answer_media WHERE answer_id = @answer_id";
                            command.Parameters.Add(new MySqlParameter("@answer_id", rs["id"]));
                            break;
                        default:
                            command = null;
                            break;
                    }

                    if (command != null)
                    {
                        var mediaReader = command.ExecuteReader();

                        if (mediaReader.Read())
                        {
                            parameters.Append(prefix).Append("media_id:==").Append(BitConverter.ToString((byte[])mediaReader["media_id"]).Replace("-", ""));
                        }

                        mediaReader.Close();
                    }
                }
            

            return parameters.ToString();
        }

  
    public void insert(AClass entry)
        {
            string table = entry.getTableType();
            JsonObject newEntry = entry.serialize();
            switch (table)
            {
                case "category":
                    insertCategory(newEntry);
                    break;
                case "quiz":
                    insertQuiz(newEntry);
                    break;
                case "question":
                    insertQuestion(newEntry);
                    break;
                case "answer":
                    insertAnswer(newEntry);
                    break;
                case "media":
                    insertMedia(newEntry);
                    break;
                case "websocket":
                    insertWebSocketQuestion(newEntry);
                    break;
            }
        }


        private void updateQuiz(JsonObject updatedEntry, string pKey, string[] values)
        {
            if(_connection !=  null) 
            {
                StringBuilder query = new("UPDATE quizzes SET ");
                foreach (string col in values)
                {
                    query.Append(col).Append("= @").Append(col).Append(",");
                }
                query.Remove(query.Length - 1, 1);
                query.Append(" WHERE name = @name");
                var command = _connection.CreateCommand();
                command.CommandText = query.ToString();
                for (int i = 0; i < values.Length; i++)
                {
                    command.Parameters.Add(new MySqlParameter("@" + values[i], updatedEntry[values[i]]?.ToString()));
                }
                command.Parameters.Add(new MySqlParameter("@name", pKey));
                command.ExecuteNonQuery();
            }
            
        }

    public void update(AClass update, string pKey, string values)
        {
            string table = update.getTableType();
            JsonObject updatedEntry = update.serialize();
            string[] changeColumns = values.Split(",");
            switch (table)
            {
                case "quiz":
                    updateQuiz(updatedEntry, pKey, changeColumns);
                    break;
            }
        }

    public void delete(string tableType, string criteria)
        {
            if(_connection != null)
            {
                var command = _connection.CreateCommand();
                byte[]? id = null;
                switch (tableType)
                {
                    case "category":
                        command.CommandText = "DELETE FROM categories WHERE " + criteria;
                        break;
                    case "quiz":
                        command.CommandText = "DELETE FROM quizzes WHERE " + criteria;
                        break;
                    case "question":
                        id = Convert.FromHexString(criteria);
                        command.CommandText = "DELETE FROM questions WHERE id = @criteria ";
                        break;
                    case "answer":
                        command.CommandText = "DELETE FROM answers WHERE " + criteria;
                        break;
                    case "media":
                        command.CommandText = "DELETE FROM media WHERE " + criteria;
                        break;
                }

                if (id != null)
                {
                    command.Parameters.Add(new MySqlParameter("@criteria", id));
                }

                command.ExecuteNonQuery();
            }
            

        }


    public List<AClass> select(string tableType, string criteria)
        {
            // SELECT columns FROM table WHERE criteria
            AClassFactory factory = new();
            List<AClass> selectedEntries = [];

            if (_connection != null)
            {

                var command = _connection.CreateCommand();
                byte[]? id = null;
                string where = (criteria == "") ? criteria : "WHERE " + criteria;
                switch (tableType)
                {
                    case "category":
                        command.CommandText = "SELECT * FROM categories " + where;
                        break;
                    case "quiz":
                        command.CommandText = "SELECT * FROM quizzes " + where;
                        break;
                    case "question":
                        command.CommandText = "SELECT * FROM questions " + where;
                        break;
                    case "answer":
                        command.CommandText = "SELECT * FROM answers WHERE question_id = @criteria " + criteria.Split(",")[1];
                        id = Convert.FromHexString(criteria.Split(",")[0]);
                        break;
                    case "media":
                        id = Convert.FromHexString(criteria);
                        command.CommandText = "SELECT * FROM media WHERE id = @criteria";
                        break;
                }
               
                if (id != null)
                {
                    command.Parameters.Add(new MySqlParameter("@criteria", id));
                }

                var reader = command.ExecuteReader();
                var resultSet = ReadResults(reader);
                reader.Close();
                foreach(var result in resultSet)
                {
                    string parameters = createConstructorParameters(result, tableType);
                    AClass cat = factory.createAClass(tableType, parameters);
                    selectedEntries.Add(cat);
                }
            }

            return selectedEntries;

        }

        static List<Dictionary<string, object>> ReadResults(IDataReader reader)
        {
            var entries = new List<Dictionary<string, object>>();

            while (reader.Read())
            {
                var entry = new Dictionary<string, object>();
                for (int i = 0; i < reader.FieldCount; i++)
                {
                    entry[reader.GetName(i)] = reader.GetValue(i);
                }
                entries.Add(entry);
            }

            return entries;
        }
    }
}

    
