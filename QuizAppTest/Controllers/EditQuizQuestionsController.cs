using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [Route("api/edit")]
    [ApiController]
    public class EditQuizQuestionsController : Controller
    {
        private readonly ILogger<EditQuizQuestionsController> _logger;
        private readonly IRepository _repository;

        public EditQuizQuestionsController(ILogger<EditQuizQuestionsController> logger, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
        }

        [HttpGet("{quiz}/questions")]
        public IActionResult Get(string quiz)
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

            JsonArray questionsArray = [];

            //Check if quiz exists
            List<AClass> checkQuiz = _repository.select("quiz", "name=\"" + quiz + "\"");
            if (checkQuiz.Count < 1)
            {
                return Redirect("/createQuiz");
            }

            List<AClass> questions = _repository.select("question", "quiz_name=\"" + quiz + "\"");
            foreach (AClass question in questions)
            {
                questionsArray.Add(question.serialize());
            }

            jsonResponse["questions"] = questionsArray;
            return Json(jsonResponse);
        }
    }
}
