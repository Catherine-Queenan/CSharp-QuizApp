using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;
using Microsoft.Win32;
using QuizApp.Utilities;
using QuizAppTest;

namespace QuizApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class UpdateAutoplayController : Controller
    {
        private readonly ILogger<ErrorController> _logger;

        [HttpGet]
        public IActionResult Index(string enabled)
        {
            if (HttpContext.Session.GetString("user") == null)
            {
                return RedirectToAction("Index", "Login");
            }

            bool isAutoplayEnabled = bool.TryParse(enabled, out bool result) && result;
            HttpContext.Session.SetString("autoplay", isAutoplayEnabled.ToString());

            return RedirectToAction("Index", "Play");
        }
    }
}
