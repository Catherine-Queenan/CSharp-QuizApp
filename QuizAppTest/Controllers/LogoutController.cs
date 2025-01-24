using Microsoft.AspNetCore.Mvc;

namespace QuizApp.Controllers
{
    [ApiController]
    [Route("logout")]
    public class LogoutController : ControllerBase
    {
        [HttpGet]
        public IActionResult Logout()
        {
            // Invalidate the current session
            HttpContext.Session.Clear();

            // Optionally, remove the authentication token cookie (if needed)
            // Response.Cookies.Append("token", "", new CookieOptions { MaxAge = TimeSpan.Zero, HttpOnly = true, Secure = true, Path = "/" });

            // Set response headers to prevent caching
            Response.Headers["Cache-Control"] = "no-cache, no-store, must-revalidate";
            Response.Headers["Pragma"] = "no-cache";
            Response.Headers["Expires"] = "0";

            // Prepare JSON response
            var jsonResponse = new
            {
                status = "success",
                message = "Logged out successfully"
            };

            // Return JSON response
            return Ok(jsonResponse);
        }
    }
}
