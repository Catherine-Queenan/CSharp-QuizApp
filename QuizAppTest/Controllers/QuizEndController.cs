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

            HttpContext.Session.SetInt32("currQuestion", 0);
            HttpContext.Session.SetString("role", userRole);

            return Redirect("/play");
        }
    }

    public class QuizEndRequest
    {
        public string? SessionId { get; set; }
    }
}
