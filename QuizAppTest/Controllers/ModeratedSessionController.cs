using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

[Route("api/[controller]")]
[ApiController]
public class ModeratedSessionController : ControllerBase
{
    private readonly DatabaseUtil _databaseUtil;
    private readonly ModerationSessionManager moderationSessionManager;

    public ModeratedSessionController(DatabaseUtil databaseUtil)
    {
        _databaseUtil = databaseUtil;
        moderationSessionManager = new ModerationSessionManager(_databaseUtil); 
    }

    [HttpGet]
    public IActionResult HandleRequest([FromQuery] string action, [FromQuery] string sessionId, [FromQuery] string quizName)
    {
        try
        {
            switch (action?.ToLower())
            {
                case "startmoderatedsession":
                    string moderatorId = HttpContext.Session.GetString("USER_ID");
                    if (string.IsNullOrEmpty(moderatorId)) return Unauthorized("User not logged in.");

                    string modSessionId = moderationSessionManager.StartModeratedSession(moderatorId, quizName);
                    return Ok(new { status = "success", sessionId = modSessionId });

                case "endmoderatedsession":
                    moderationSessionManager.EndModeratedSession(sessionId);
                    return Ok(new { status = "success", message = "Session ended successfully." });

                case "getactivesessions":
                    var sessions = moderationSessionManager.GetActiveSessions();
                    return Ok(new { status = "success", sessions });

                case "getmoderatedsession":
                    var session = moderationSessionManager.GetModeratedSession(sessionId, quizName);
                    return Ok(new { status = "success", session });

                default:
                    return BadRequest(new { status = "error", message = "Invalid action." });
            }
        }
        catch (Exception ex)
        {
            return StatusCode(500, new { status = "error", message = ex.Message });
        }
    }
}
