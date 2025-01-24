using System;
using System.Configuration;
using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [Route("api/edit")]
    [ApiController]
    public class EditQuizController : Controller
    {
        private readonly ILogger<EditQuizController> _logger;
        private readonly IRepository _repository;
        private readonly AClassFactory _classFactory;
        private readonly IConfiguration _configuration;

        public EditQuizController(ILogger<EditQuizController> logger, IConfiguration configuration, DatabaseUtil databaseUtil)
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

            AClass quiz = _repository.select("quiz", "name=\"" + quizName + "\"")[0];
            JsonObject quizJson = quiz.serialize();
            string mediaId = quizJson["media_id"]?.ToString() ?? "";
            if (mediaId != "")
            {
                AClass quizMedia = _repository.select("media", mediaId)[0];
                quizJson["media"] = quizMedia.serialize();
            }

            jsonResponse["quiz"] = quizJson;

            return Json(jsonResponse);
        }

        [HttpPost]
        public async Task<IActionResult> Post([FromForm] EditQuizRequest req)
        {
            string parameters = "description:==" + req.Description;
            string updateCriteria = "description";

            if (req.QuizMedia != null)
            {
                var fileName = Path.GetFileName(req.QuizMedia.FileName);
                var filePath = Path.Combine(Directory.GetCurrentDirectory(), @"wwwroot\uploads", fileName);

                using (var stream = new FileStream(filePath, FileMode.Create))
                {
                    await req.QuizMedia.CopyToAsync(stream);
                }

                string storedUrl = Path.Combine("uploads", req.QuizMedia.FileName);

                AClass quiz = _repository.select("quiz", "name=\"" + req.QuizName + "\"")[0];
                JsonObject quizJson = quiz.serialize();
                string mediaId = quizJson["media_id"]?.ToString() ?? "";

                if (mediaId != "")
                {
                    string mediaParams = "media_file_path:==" + storedUrl + ",,,media_filename:==" + req.QuizMedia.FileName;
                    AClass updateMedia = _classFactory.createAClass("media", mediaParams);
                    _repository.update(updateMedia, mediaId, "media_file_path,media_filename");
                }
                else
                {
                    Guid newGuid = Guid.NewGuid();
                    byte[] quizMediaId = newGuid.ToByteArray();
                    string quizMediaIdString = BitConverter.ToString(quizMediaId).Replace("-", "");

                    string mediaParams = "id:==" + quizMediaIdString + ",,,media_file_path:==" + storedUrl + ",,,media_filename:==" + req.QuizMedia.FileName;
                    AClass updateMedia = _classFactory.createAClass("media", mediaParams);
                    _repository.insert(updateMedia);

                    updateCriteria += ",media_id";
                    parameters += ",,,media_id:==" + quizMediaIdString;
                }
            }

            AClass updateQuiz = _classFactory.createAClass("quiz", parameters);
            _repository.update(updateQuiz, req.QuizName, updateCriteria);

            JsonObject jsonResponse = new()
            {
                ["message"] = "Quiz edited successfully!"
            };

            return Ok(jsonResponse);

        }
    }

    public class EditQuizRequest
    {
        public required string Title { get; set; }
        public required string QuizName { get; set; }
        public string? Description { get; set; }
        public IFormFile? QuizMedia { get; set; }

    }
}
