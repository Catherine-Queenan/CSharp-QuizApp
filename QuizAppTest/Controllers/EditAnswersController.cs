using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [Route("api/edit")]
    [ApiController]
    public class EditAnswersController : ControllerBase
    {
        private readonly ILogger<EditAnswersController> _logger;
        private readonly IRepository _repository;
        private readonly AClassFactory _classFactory;
        private readonly IConfiguration _configuration;

        public EditAnswersController(ILogger<EditAnswersController> logger, IConfiguration configuration, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
            _classFactory = new AClassFactory();
            _configuration = configuration;
        }

        [HttpDelete("{quizName}/answers/{answer}")]
        public IActionResult Delete(string quizName, string answer)
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

            _repository.delete("answer", answer);

            jsonResponse["status"] = "success";
            jsonResponse["message"] = "Question deleted successfully.";

            return Ok(jsonResponse);
        }

        [HttpPost("answers/{question}/add")]
        public async Task<IActionResult> Post(string question, [FromForm] AddAnswerRequest req)
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

            Guid newAnswerGuid = Guid.NewGuid();
            byte[] newAnswerIdBytes = newAnswerGuid.ToByteArray();
            string newAnswerIdString = BitConverter.ToString(newAnswerIdBytes).Replace("-", "");

            List<AClass> answers = _repository.select("answer", question);

            string? mediaId = null;
            if(req.AnswerType == "IMG")
            {
                if (req.MediaFile != null)
                {
                    Guid mediaGuid = Guid.NewGuid();
                    byte[] mediaIdBytes = mediaGuid.ToByteArray();
                    string mediaIdString = BitConverter.ToString(mediaIdBytes).Replace("-", "");

                    var fileName = Path.GetFileName(req.MediaFile.FileName);
                    var filePath = Path.Combine(Directory.GetCurrentDirectory(), @"wwwroot\uploads", fileName);

                    using (var stream = new FileStream(filePath, FileMode.Create))
                    {
                        await req.MediaFile.CopyToAsync(stream);
                    }

                    string storedUrl = Path.Combine("uploads", fileName);

                    string mediaParams = "id:==" + mediaIdString + "media_file_path:==" + storedUrl + ",,,media_filename:==" + fileName;
                    AClass updateMedia = _classFactory.createAClass("media", mediaParams);
                    _repository.insert(updateMedia);
                }
            }
            if(req.CorrectAnswer != null)
            {
                AClass currentCorrect = null;
                foreach(AClass answer in answers)
                {
                    JsonObject answerJson = answer.serialize();
                    int? isCorrect = answerJson["is_correct"]?.GetValue<int>();
                    if (isCorrect != null && isCorrect > 0)
                    {
                        currentCorrect = answer;
                    }
                }

                if (currentCorrect != null)
                {
                    JsonObject answerJson = currentCorrect.serialize();
                    ((Answer)currentCorrect).setCorrect(0);

                    string? answerId = answerJson["id"]?.ToString();
                    if (!string.IsNullOrEmpty(answerId)) 
                    {
                        _repository.update(currentCorrect, answerId, "is_correct");
                    }

                    if(req.AnswerType == "AUD" || req.AnswerType == "VID")
                    {
                        mediaId = answerJson["media_id"]?.ToString();
                    }
                    
                }
            }

            string parameters = "id:==" + newAnswerIdString +
                    ",,,answer_text:==" + req.AnswerText + ",,,answer_type:==" + req.AnswerType +
                    ",,,is_correct:==" + (req.CorrectAnswer != null ? 1 : 0) + ",,,question_id:==" + question;
            if (mediaId != null)
            {
                parameters += ",,,media_id:==" + mediaId;
            }

            AClass newAnswer = _classFactory.createAClass("answer", parameters);
            _repository.insert(newAnswer);

            JsonObject jsonResponse = new JsonObject();
            jsonResponse["message"] = "Answer added successfully!";

            return Ok(jsonResponse);
        }
    }

    public class AddAnswerRequest
    {
        public required string AnswerText { get; set; }
        public int? CorrectAnswer { get; set; }
        public required string AnswerType { get; set; }
        public IFormFile? MediaFile { get; set; } // For file uploads
    }
}
