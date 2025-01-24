using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [Route("api/edit")]
    [ApiController]
    public class DeleteQuestionController : Controller
    {
        private readonly ILogger<DeleteQuestionController> _logger;
        private readonly IRepository _repository;
        public DeleteQuestionController(ILogger<DeleteQuestionController> logger, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
        }

        [HttpDelete("{quiz}/questions/{question}/delete")]
        public IActionResult Delete(string quiz, string question)
        {
            string? userRole = HttpContext.Session.GetString("USER_ROLE");

            if (string.IsNullOrEmpty(userRole))
            {
                return Redirect("/login");
            }

            if (userRole != "a")
            {
                return Unauthorized(new { Status = "Error", Message = "401 You are not authorized to access this page." });
            }

            JsonObject jsonResponse = new()
            {
                ["role"] = userRole
            };

            HttpContext.Session.Remove("questions");

            _repository.delete("question", question);
            jsonResponse["status"] = "success";
            jsonResponse["message"] = "Question deleted successfully!";

            return Ok(jsonResponse);
        }
    }
}
