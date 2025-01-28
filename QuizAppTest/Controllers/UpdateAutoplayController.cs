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
        private readonly ILogger<UpdateAutoplayController> _logger;

        [HttpGet("{enabled}")]
        public IActionResult? Index(string enabled)
        {
            if (HttpContext.Session.GetString("USER_ROLE") == null)
            {
                return Redirect("/Login");
            }

            //bool isAutoplayEnabled = bool.TryParse(enabled, out bool result) && result;
            HttpContext.Session.SetString("autoplay", enabled);

            JsonObject jsonResponse = new()
            {
                ["status"] = "success"
            };
            return Json(jsonResponse);
        }
    }

}
