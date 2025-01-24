using Microsoft.AspNetCore.Mvc;
using Microsoft.AspNetCore.Http;

namespace QuizApp.Controllers
{
    [Route("QuizApp/home/session-status")]
    [ApiController]
    public class SessionStatusController : ControllerBase
    {
        [HttpGet]
        public IActionResult GetSessionStatus()
        {
            // Check if the user is logged in by verifying the "user" session key
            bool isLoggedIn = HttpContext.Session.GetString("user") != null;

            // Return the response as JSON
            return Ok(new { loggedIn = isLoggedIn });
        }
    }

}