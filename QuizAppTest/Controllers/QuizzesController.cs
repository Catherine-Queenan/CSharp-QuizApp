using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;


namespace QuizApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class QuizzesController : Controller
    {
        private readonly ILogger<QuizzesController> _logger;
        private readonly IRepository _repository;

        public QuizzesController(ILogger<QuizzesController> logger, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
        }

        [HttpGet("{category}")]
        public IActionResult Get(string category)
        {
            string? userRole = HttpContext.Session.GetString("USER_ROLE");

            if (string.IsNullOrEmpty(userRole))
            {
                return Redirect("/login");
            }

            JsonObject jsonResponse = new JsonObject();

            if (userRole == "a")
            {
                jsonResponse["role"] = "admin";
            }
            else
            {
                jsonResponse["role"] = "gen";
            }

            JsonArray quizzesArray = new JsonArray();

            string criteria = "category_name = \"" + category + "\"";
            List<AClass> quizzes = _repository.select("quiz", criteria);

            foreach (AClass quiz in quizzes)
            {
                JsonObject quizJSON = quiz.serialize();
                string? media_id = quizJSON["media_id"]?.ToString();
                
                if (!string.IsNullOrEmpty(media_id))
                {
                    AClass quizMedia = _repository.select("media", media_id)[0];
                    quizJSON["media"] = quizMedia.serialize();
                }

                quizzesArray.Add(quizJSON);
            }

            jsonResponse["quizzes"] = quizzesArray;

            // Serve JSON of quizzes 
            return Json(jsonResponse);
        }

        [HttpPost]
        public IActionResult Post([FromForm] QuizRequest request)
        {
            string? userRole = HttpContext.Session.GetString("USER_ROLE");
            if (string.IsNullOrEmpty(userRole))
            {
                return Redirect("/login");
            }

            string criteria = "quiz_name = \"" + request.QuizName + "\" ORDER BY rand()";
            List<AClass> questions = _repository.select("question", criteria);
            JsonArray questionsArray = new JsonArray();

            foreach (AClass question in questions)
            {
                questionsArray.Add(question.serialize());
            }

            HttpContext.Session.SetString("quiz", request.QuizName);
            HttpContext.Session.SetString("questions", questionsArray.ToString());
            HttpContext.Session.SetInt32("currQuestion", 0);
            return Redirect("/play");
        }
    }
}

public class QuizRequest
{
    public string QuizName { get; set; }
}
