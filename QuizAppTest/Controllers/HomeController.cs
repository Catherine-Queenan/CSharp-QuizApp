using Microsoft.AspNetCore.Mvc;

namespace QuizApp.Controllers
{
    public class HomeController : Controller
    {
        [HttpGet("/home")]
        public IActionResult Index()
        {
            // Serve the main.jsp file
            return View("~/Views/main.jsp");
        }
    }
}
