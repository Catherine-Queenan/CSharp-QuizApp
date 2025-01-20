using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using Microsoft.Win32;
using QuizApp.Utilities;
using QuizAppTest;

namespace QuizApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class ErrorController : Controller
    {
        private readonly ILogger<ErrorController> _logger;

        [HttpGet]
        public IActionResult Index()
        {
            string errorMessage = HttpContext.Session.GetString("errorMessage");

            ViewData["errorMessage"] = errorMessage;

            return View("Error");
        }
    }
}