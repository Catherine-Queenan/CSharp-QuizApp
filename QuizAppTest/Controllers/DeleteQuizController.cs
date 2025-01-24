using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [Route("api/edit")]
    [ApiController]
    public class DeleteQuizController : Controller
    {
        private readonly ILogger<DeleteQuizController> _logger;
        private readonly IRepository _repository;
        public DeleteQuizController(ILogger<DeleteQuizController> logger, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
        }

        [HttpDelete("{quiz}/delete")]
        public IActionResult Delete(string quiz)
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

            //Check if quiz exists
            List<AClass> checkQuiz = _repository.select("quiz", "name=\"" + quiz + "\"");
            if (checkQuiz.Count < 1)
            {
                return Redirect("/createQuiz");
            }

            _repository.delete("quiz", "name = \"" + quiz + "\"");
            jsonResponse["status"] = "success";
            jsonResponse["message"] = "Quiz deleted successfully!";

            return Ok(jsonResponse);
        }
    }
}
