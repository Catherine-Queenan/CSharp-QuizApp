using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [Route("api/edit")]
    [ApiController]
    public class EditQuestionController : Controller
    {
        private readonly ILogger<EditQuestionController> _logger;
        private readonly IRepository _repository;
        private readonly AClassFactory _classFactory;
        private readonly IConfiguration _configuration;

        public EditQuestionController(ILogger<EditQuestionController> logger, IConfiguration configuration, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
            _classFactory = new AClassFactory();
            _configuration = configuration;
        }

        [HttpGet("{quizName}/questions/{question}")]
        public IActionResult Get(string quizName, string question)
        {
            string? userRole = HttpContext.Session.GetString("USER_ROLE");

            if (string.IsNullOrEmpty(userRole))
            {
                return Redirect("/login");
            }

            if (userRole != "a")
            {
                return Unauthorized(new { Status = "Error", Message = "401 You are not authorized to access this page." });
            }

            JsonObject jsonResponse = new()
            {
                ["role"] = userRole
            };

            AClass editQuestion = _repository.select("question", ("id = 0x" + question))[0];
            JsonObject editQuestionJson = editQuestion.serialize();

            string? questionMediaId = editQuestionJson["media_id"]?.ToString();
            if (!string.IsNullOrEmpty(questionMediaId))
            {
                AClass questionMedia = _repository.select("media", questionMediaId)[0];
                editQuestionJson["media"] = questionMedia.serialize();
            }

            JsonArray answersArray = [];
            jsonResponse["question"] = editQuestionJson;
            string? questionId = editQuestionJson["id"]?.ToString();
            if (!string.IsNullOrEmpty(questionId))
            {

                List<AClass> answers = _repository.select("answer", questionId);

                foreach (AClass answersItem in answers)
                {
                    JsonObject answerJson = answersItem.serialize();
                    string? answerMediaId = answerJson["media_id"]?.ToString();
                    if (!string.IsNullOrEmpty(answerMediaId))
                    {
                        AClass answerMedia = _repository.select("media", answerMediaId)[0];
                        answerJson["media"] = answerMedia.serialize();
                    }

                    answersArray.Add(answerJson);
                }
            }

            jsonResponse["answers"] = answersArray;

            return Json(jsonResponse);
        }

        [HttpPost("questions/{question}")]
        public async Task<IActionResult> Post(string question, [FromForm] EditQuestionRequest req)
        {
            string parameters = "question_text:==" + req.QuestionText + ",,,question_type:==" + req.QuestionType;
            AClass editQuestion = _repository.select("question", ("id = 0x" + question))[0];
            JsonObject editQuestionJson = editQuestion.serialize();

            string newMediaId = "";
            string? mediaId = editQuestionJson["media_id"]?.ToString();
            if (!string.IsNullOrEmpty(mediaId))
            {
                Guid newMediaGuid = Guid.NewGuid();
                byte[] newMediaIdBytes = newMediaGuid.ToByteArray();
                string newMediaIdString = BitConverter.ToString(newMediaIdBytes).Replace("-", "");

                newMediaId = "id:==" + newMediaIdString + ",,,";
                parameters = parameters + ",,,media_id:==" + newMediaIdString;
            }

            AClass? updateMedia = null;
            if (req.QuestionType == "AUD" || req.QuestionType == "IMG")
            {
                if (req.MediaFile != null)
                {
                    var fileName = Path.GetFileName(req.MediaFile.FileName);
                    var filePath = Path.Combine(Directory.GetCurrentDirectory(), @"wwwroot\uploads", fileName);

                    using (var stream = new FileStream(filePath, FileMode.Create))
                    {
                        await req.MediaFile.CopyToAsync(stream);
                    }

                    string storedUrl = Path.Combine("uploads", fileName);

                    string mediaParams = newMediaId + "media_file_path:==" + storedUrl + ",,,media_filename:==" + fileName
                            + ",,,media_start:==" + req.AudioStart + ",,,media_end:==" + req.AudioEnd + ",,,media_type:=="
                            + req.QuestionType;
                    updateMedia = _classFactory.createAClass("media", mediaParams);
                }
            }
            else if (req.QuestionType == "VID")
            {
                string mediaParams = newMediaId + "media_file_path:==" + req.VideoUrl + ",,,media_filename:==N/A" + ",,,media_start:=="
                        + req.VideoStart + ",,,media_end:==" + req.VideoEnd + ",,,media_type:==" + req.QuestionType;

                updateMedia = _classFactory.createAClass("media", mediaParams);

            }

            AClass updateQuestion = _classFactory.createAClass("question", parameters);
            if (updateMedia != null && req.QuestionType != "TEXT")
            {
                if (string.IsNullOrEmpty(mediaId))
                {
                    _repository.insert(updateMedia);
                    _repository.update(updateQuestion, question, "question_text,question_type,media_id");
                }
                else
                {
                    _repository.update(updateMedia, mediaId,
                "media_file_path,media_filename,media_start,media_end,media_type");
                    _repository.update(updateQuestion, question, "question_text,question_type");
                }
            } else
            {
                _repository.update(updateQuestion, question, "question_text,question_type");
            }

            JsonObject jsonResponse = new JsonObject();
            jsonResponse["message"] = "Question edited successfully!";

            return Ok(jsonResponse);
        }
    }
    public class EditQuestionRequest
    {
        public required string QuizName { get; set; }
        public required string QuestionText { get; set; }
        public required string QuestionType { get; set; }

        // Media Info
        public IFormFile? MediaFile { get; set; } // For file uploads
        public string? VideoUrl { get; set; }

        // Media start and end times
        public int? VideoStart { get; set; }
        public int? VideoEnd { get; set; }
        public int? AudioStart { get; set; }
        public int? AudioEnd { get; set; }
    }
}
