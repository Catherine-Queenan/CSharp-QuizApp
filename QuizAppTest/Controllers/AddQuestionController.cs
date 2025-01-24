using System.Text;
using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Extensions.Primitives;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class AddQuestionController : Controller
    {
        private readonly ILogger<AddQuestionController> _logger;
        private readonly IRepository _repository;
        private readonly AClassFactory _classFactory;
        private readonly IConfiguration _configuration;

        public AddQuestionController(ILogger<AddQuestionController> logger, IConfiguration configuration, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
            _classFactory = new AClassFactory();
            _configuration = configuration;
        }

        [HttpGet("{quizName}")]
        public IActionResult Get(string quizName)
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

            return Json(jsonResponse);
        }

        [HttpPost]
        public async Task<IActionResult> Post([FromForm] AddQuestionRequest req)
        {
            List<string> mediaIds = [];
            List<string> mediaUrls = [];
            List<string> mediaFilenames = [];

            int filesProcessed = 0;
            if (req.QuestionType == "VID" && req.VideoUrl != null)
            {
                mediaUrls.Add(req.VideoUrl[0]);
                mediaFilenames.Add("N/A");
                filesProcessed++;
            }

            if (req.MediaFile != null)
            {
                foreach (var file in req.MediaFile)
                {
                    var fileName = Path.GetFileName(file.FileName);

                    if ((req.QuestionType == "IMG" || req.QuestionType == "AUD" && filesProcessed == 0)
                        || (req.QuestionType == "TEXT" && req.AnswerType == "IMG" || req.AnswerType == "AUD")
                        || (filesProcessed > 0 && req.AnswerType == "IMG" || req.AnswerType == "AUD"))
                    {
                        var filePath = Path.Combine(Directory.GetCurrentDirectory(), @"wwwroot\uploads", fileName);

                        using (var stream = new FileStream(filePath, FileMode.Create))
                        {
                            await file.CopyToAsync(stream);
                            mediaUrls.Add(Path.Combine("uploads", file.FileName));
                            mediaFilenames.Add(file.FileName);
                            filesProcessed++;
                        }
                    }


                }
            }

            if (req.AnswerType == "VID" && req.VideoUrl != null)
            {
                mediaUrls.Add(req.VideoUrl[1]);
                mediaFilenames.Add("N/A");
                filesProcessed++;
            }

            for (int i = 0; i < filesProcessed; i++)
            {
                Guid newMediaGuid = Guid.NewGuid();
                byte[] mediaId = newMediaGuid.ToByteArray();
                string mediaIdString = BitConverter.ToString(mediaId).Replace("-", "");

                mediaIds.Add(mediaIdString);
                string mediaType = (req.QuestionType != "TEXT" && i == 0) ? req.QuestionType : req.AnswerType;

                StringBuilder criteria = new StringBuilder();
                criteria.Append("id:==").Append(mediaIdString)
                        .Append(",,,media_type:==").Append(mediaType)
                        .Append(",,,media_file_path:==").Append(mediaUrls[i])
                        .Append(",,,media_filename:==").Append(mediaFilenames[i]);

                if (i == 0 && req.QuestionType == "VID")
                {

                    if (req.VideoStart != null && req.VideoEnd != null)
                    {
                        criteria.Append(",,,media_start:==").Append(req.VideoStart[i])
                                .Append(",,,media_end:==").Append(req.VideoEnd[i]);
                    }
                } else if(req.QuestionType == "AUD" && i == 0)
                {
                    if (req.AudioStart != null && req.AudioEnd != null)
                    {
                        criteria.Append(",,,media_start:==").Append(req.AudioStart[i])
                                .Append(",,,media_end:==").Append(req.AudioEnd[i]);
                    }
                } else if(req.AnswerType == "VID")
                {
                    int indexTimes = i == 0 ? 1 : i;
                    if (req.VideoStart != null && req.VideoEnd != null)
                    {
                        criteria.Append(",,,media_start:==").Append(req.VideoStart[i])
                                .Append(",,,media_end:==").Append(req.VideoEnd[i]);
                    }

                } else
                {
                    int indexTimes = i == 0 ? 1 : i;
                    if (req.AudioStart != null && req.AudioEnd != null)
                    {
                        criteria.Append(",,,media_start:==").Append(req.AudioStart[i])
                                .Append(",,,media_end:==").Append(req.AudioEnd[i]);
                    }
                }

                _repository.insert(_classFactory.createAClass("media", criteria.ToString()));

            }

            Guid newQuestionGuid = Guid.NewGuid();
            byte[] questionId = newQuestionGuid.ToByteArray();
            string questionIdString = BitConverter.ToString(questionId).Replace("-", "");

            StringBuilder newQuestion = new StringBuilder();
            newQuestion.Append("id:==").Append(questionIdString)
                    .Append(",,,quiz_name:==").Append(req.QuizName)
                    .Append(",,,question_text:==").Append(req.QuestionText)
                    .Append(",,,question_type:==").Append(req.QuestionType);

            if(req.QuestionType != "TEXT")
            {
                newQuestion.Append(",,,media_id:==").Append(mediaIds[0]);
            }

            _repository.insert(_classFactory.createAClass("question", newQuestion.ToString()));

            int indexOfCorrect = 0;
            for (int i = 0; i < req.AnswerText.Length; i++)
            {
                Guid newAnswerGuid = Guid.NewGuid();
                byte[] answerId = newAnswerGuid.ToByteArray();
                string answerIdString = BitConverter.ToString(answerId).Replace("-", "");

                bool isCorrect = (req.CorrectAnswer == i + 1);
                if (isCorrect)
                {
                    indexOfCorrect = i;
                }

                StringBuilder newAnswer = new StringBuilder();
                newAnswer.Append("id:==").Append(answerIdString)
                        .Append(",,,question_id:==").Append(questionIdString)
                        .Append(",,,answer_text:==").Append(req.AnswerText[i])
                        .Append(",,,is_correct:==").Append((isCorrect ? 1 : 0))
                        .Append(",,,answer_type:==").Append(req.AnswerType);

                if(req.AnswerType == "IMG" || (req.AnswerType != "TEXT" && isCorrect))
                {
                    string answerMediaId = req.QuestionType == "TEXT" ? mediaIds[i+1] : mediaIds[i + 1];
                    newAnswer.Append(",,,media_id:==").Append(answerMediaId);
                }

                _repository.insert(_classFactory.createAClass("answer", newAnswer.ToString()));
            }

            JsonObject message = new JsonObject()
            {
                ["message"] = "Quiz edited successfully!"
            };

            return Ok(message);
        }
    }

    public class AddQuestionRequest
    {
        public required string[] AnswerText { get; set; }
        public required int CorrectAnswer { get; set; }
        public string? AddAnotherQuestion { get; set; }
        public required string AnswerType { get; set; }
        public required string QuizName { get; set; }
        public required string QuestionText { get; set; }
        public required string QuestionType { get; set; }

        // Media Info
        public IFormFileCollection? MediaFile { get; set; } // For file uploads
        public string[]? VideoUrl { get; set; }

        // Media start and end times
        public int[]? VideoStart { get; set; }
        public int[]? VideoEnd { get; set; }
        public int[]? AudioStart { get; set; }
        public int[]? AudioEnd { get; set; }
    }
}
