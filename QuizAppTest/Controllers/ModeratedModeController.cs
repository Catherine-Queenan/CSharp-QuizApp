using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;
using System.Data;
using System.Text;
using MySql.Data.MySqlClient;
using QuizApp.Utilities;
using System.Data.Common;

namespace QuizApp.Controllers
{
    [Route("api/ModeratedMode")]
    [ApiController]
    public class ModeratedModeController : Controller
    {
        //private readonly DatabaseUtil _databaseUtil;
        //private readonly ModerationSessionManager _moderationSessionManager;

        //public ModeratedModeController(DatabaseUtil databaseUtil)
        //{
        //    _databaseUtil = databaseUtil;
        //    _moderationSessionManager = new ModerationSessionManager(_databaseUtil);
        //}

        private readonly DatabaseUtil _databaseUtil;
        private IDbConnection? _connection;
        private readonly ModerationSessionManager _moderationSessionManager;

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

        public ModeratedModeController(DatabaseUtil databaseUtil)
        {
            _databaseUtil = databaseUtil;
            init(databaseUtil);
            _moderationSessionManager = new ModerationSessionManager(_databaseUtil);

        }

        [HttpGet]
        public IActionResult Index(string quizName, string sessionId)
        {
            // Check if the user is logged in
            if (HttpContext.Session.GetString("USER_ID") == null)
            {
                return RedirectToAction("Login", "Account");
            }

            if (string.IsNullOrWhiteSpace(quizName))
            {
                return BadRequest("Missing quiz name");
            }

            string username = HttpContext.Session.GetString("USER_ID");
            string role = GetUserRoleFromDatabase(username);
            ViewData["Role"] = role;
            ViewData["UserName"] = username;

            if (!string.IsNullOrEmpty(sessionId))
            {
                try
                {
                    var modSession = _moderationSessionManager.GetModeratedSession(sessionId, quizName);
                    if (modSession != null)
                    {
                        string modSessionId = modSession.SessionId;
                        HttpContext.Session.SetString("ModSessionId", modSessionId);
                        ViewData["ModSessionId"] = modSessionId;
                    }
                    else
                    {
                        ViewData["ErrorMessage"] = "Failed to start moderation session.";
                    }
                }
                catch (FormatException)
                {
                    ViewData["ErrorMessage"] = "Invalid session ID format.";
                }
            }
            else
            {
                ViewData["ErrorMessage"] = "Session ID not provided in the URL.";
            }

            StringBuilder questionsHtml = new StringBuilder();

            //using (var connection = _databaseUtil.GetConnection())
            //{
            //    connection.Open();
            //    string query = @"SELECT q.id AS question_id, q.question_text, a.id AS answer_id, a.answer_text, a.is_correct, 
            //            qm.media_id AS question_media_id, am.media_id AS answer_media_id
            //         FROM questions q
            //         LEFT JOIN answers a ON q.id = a.question_id
            //         LEFT JOIN question_media qm ON q.id = qm.question_id
            //         LEFT JOIN answer_media am ON a.id = am.answer_id
            //         WHERE q.quiz_name = @QuizName"
            //    ;

            //    using (var command = _connection.CreateCommand())
            //    {
            //        command.Parameters.Add(new MySqlParameter("@QuizName", quizName));
            //        using (var reader = command.ExecuteReader())
            //        {
            //            string previousQuestionId = null;
            //            int questionNumber = 0;

            //            while (reader.Read())
            //            {
            //                string questionId = reader["question_id"].ToString();
            //                string questionText = reader["question_text"].ToString();
            //                string answerText = reader["answer_text"]?.ToString();
            //                bool isCorrect = Convert.ToBoolean(reader["is_correct"]);

            //                if (questionId != previousQuestionId)
            //                {
            //                    if (previousQuestionId != null)
            //                    {
            //                        questionsHtml.Append("</div>");
            //                    }

            //                    questionsHtml.Append("<div class='question'>")
            //                                 .Append("<p class='questionTitle'>").Append(questionText).Append("</p>");

            //                    //string mediaHtml = GetMediaHtml(reader["question_media_id"], connection);
            //                    //questionsHtml.Append(mediaHtml)
            //                    questionsHtml
            //                                 .Append("<div class='answers'>");

            //                    previousQuestionId = questionId;
            //                    questionNumber++;
            //                }

            //                string answerClass = isCorrect ? "answer correct" : "answer";
            //                questionsHtml.Append($"<p data-question='{questionNumber}' class='{answerClass}'>{answerText}</p>");
            //            }

            //            if (previousQuestionId != null)
            //            {
            //                questionsHtml.Append("</div>");
            //            }
            //        }
            //    }
            //}

            //ViewData["sessionId"] = sessionId;
            //ViewData["quizName"] = quizName;
            //ViewData["moderatorId"] = moderator;
            //ViewData["QuestionsHtml"] = questionsHtml.ToString();
            //return View("moderatedMode");
            return Redirect("/moderatedQuiz/quizName=" + quizName);
        }
        //public IActionResult Index(string quizName, string sessionId)
        //{
        //    //[HttpGet]
        //    //public IActionResult HandleRequest([FromQuery] string action, [FromQuery] string? sessionId, [FromQuery] string? quizName)
        //    //{
        //    // Check if the user is logged in
        //    if (HttpContext.Session.GetString("USER_ID") == null)
        //    {
        //        return Json(new { status = "error", message = "User not logged in" });
        //    }

        //    if (string.IsNullOrWhiteSpace(quizName))
        //    {
        //        return Json(new { status = "error", message = "Missing quiz name" });
        //    }

        //    string username = HttpContext.Session.GetString("USER_ID");
        //    string role = GetUserRoleFromDatabase(username);

        //    // Process moderation session
        //    if (!string.IsNullOrEmpty(sessionId))
        //    {
        //        try
        //        {
        //            var modSession = _moderationSessionManager.GetModeratedSession(sessionId, quizName);
        //            if (modSession != null)
        //            {
        //                string modSessionId = modSession.SessionId;
        //                HttpContext.Session.SetString("ModSessionId", modSessionId);
        //                return Json(new { status = "success", sessionId = modSessionId });
        //            }
        //            else
        //            {
        //                return Json(new { status = "error", message = "Failed to start moderation session" });
        //            }
        //        }
        //        catch (FormatException)
        //        {
        //            return Json(new { status = "error", message = "Invalid session ID format" });
        //        }
        //    }

        //    return Json(new { status = "error", message = "Session ID not provided" });
        //}

        [HttpGet("{quizName}")]
        public IActionResult Get(string quizName, string sessionId)
        {
            return Json(new { status = "error", message = "am I cooking or am I being cooked" });
        }

        [HttpPost]
        public IActionResult EndSession(string modSessionId)
        {
            if (!string.IsNullOrEmpty(modSessionId))
            {
                _moderationSessionManager.EndModeratedSession(modSessionId);
            }
            return RedirectToAction("Index");
        }

        private string GetMediaHtml(object mediaId)
        {
            if (mediaId == DBNull.Value) return string.Empty;

            string query = "SELECT media_file_path, media_type FROM media WHERE id = @MediaId";

            using (var connection = _databaseUtil.GetConnection())
            {
                connection.Open();

                using (var command = connection.CreateCommand())
                {
                    command.CommandText = query;
                    command.Parameters.Add(new MySqlParameter("@MediaId", mediaId));

                    using (var reader = command.ExecuteReader())
                    {
                        if (reader.Read())
                        {
                            string filePath = reader["media_file_path"].ToString();
                            string mediaType = reader["media_type"].ToString();

                            return mediaType switch
                            {
                                "IMG" => $"<img src='{filePath}' alt='Question Media' />",
                                "VID" => $"<video controls><source src='{filePath}' type='video/mp4'></video>",
                                "AUD" => $"<audio controls><source src='{filePath}' type='audio/mpeg'></audio>",
                                _ => string.Empty,
                            };
                        }
                    }
                }
            }

            return string.Empty;
        }

        private string GetUserRoleFromDatabase(string username)
        {
            using (var connection = _databaseUtil.GetConnection())
            {
                connection.Open();
                string query = "SELECT role FROM users WHERE username = @Username";

                using (var command = connection.CreateCommand())
                {
                    command.CommandText = query;
                    command.Parameters.Add(new MySqlParameter("@Username", username));

                    using (var reader = command.ExecuteReader())
                    {
                        return reader.Read() ? reader["role"].ToString() : null;
                    }
                }
            }
        }
    }
}
