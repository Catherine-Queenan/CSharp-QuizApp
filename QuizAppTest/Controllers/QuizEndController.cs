using System.Security;
using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [ApiController]
    [Route("[controller]")]
    public class QuizEndController : Controller
    {
        private readonly ILogger<QuizEndController> _logger;
        private readonly IRepository _repository;

        public QuizEndController(ILogger<QuizEndController> logger, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
        }

        public IActionResult Index([FromQuery] QuizEndRequest req)
        {
            string? userRole = HttpContext.Session.GetString("USER_ROLE");

            if (string.IsNullOrEmpty(userRole))
            {
                return Redirect("/login");
            }

            ViewData["Role"] = userRole;
            ViewData["SessionId"] = req.SessionId; // Example session ID
            return View("Index");
        }

        [HttpPost]
        public IActionResult Post()
        {
            string? userRole = HttpContext.Session.GetString("USER_ROLE");

            if (string.IsNullOrEmpty(userRole))
            {
                return Redirect("/login");
            }
            string? questions = HttpContext.Session.GetString("questions");
            if (string.IsNullOrEmpty(questions))
            {
                return Redirect("/quizzes");
            }
            JsonArray questionsArrayJSON = (JsonArray)(JsonArray.Parse(questions) ?? new JsonArray());

            ShuffleJsonArray(questionsArrayJSON);
            HttpContext.Session.SetString("questions", questionsArrayJSON.ToString());
            HttpContext.Session.SetInt32("currQuestion", 0);
            HttpContext.Session.SetString("role", userRole);

            return Redirect("/play");
        }

        private void ShuffleJsonArray(JsonArray arr)
        {
            Random rand = new Random();
            int arrCount = arr.Count;
            for (int i = arrCount - 1; i > 0; i--)
            {
                int randIndex = rand.Next(0, i + 1);
                SwapArrayItems(arr, randIndex, i);
            }
        }

        private void SwapArrayItems(JsonArray arr, int a, int b)
        {
            var temp = arr[a]?.DeepClone();
            arr[a] = arr[b]?.DeepClone();
            arr[b] = temp;
        }
    }



    public class QuizEndRequest
    {
        public string? SessionId { get; set; }
    }
}
