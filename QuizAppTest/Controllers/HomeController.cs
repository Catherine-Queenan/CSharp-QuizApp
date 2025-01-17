using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;
using QuizAppTest;

namespace QuizApp.Controllers
{
    [ApiController]
    [Route("api/[controller]")]
    public class HomeController : Controller
    {
        private readonly ILogger<HomeController> _logger;
        private readonly IRepository _repository;

        public HomeController(ILogger<HomeController> logger, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
        }

        [HttpGet]
        public IActionResult Get()
        {
            string? userRole = HttpContext.Session.GetString("USER_ROLE");

            if (string.IsNullOrEmpty(userRole))
            {
                return Redirect("/login.html");
            }

            JsonObject jsonResponse = new JsonObject();

            if(userRole == "a")
            {
                jsonResponse["role"] = "admin";
            } 
            else
            {
                jsonResponse["role"] = "gen";
            }

            if (string.IsNullOrEmpty(HttpContext.Session.GetString("questions")))
            {
                HttpContext.Session.SetString("questions", "");
                HttpContext.Session.SetString("currQuestion", "");
            }

            JsonArray categoriesArray = new();
            List<AClass> categories = _repository.select("category", "");

            foreach(AClass category in categories)
            {
                JsonObject categoryJSON = category.serialize();
                string categoryName = categoryJSON["name"]?.ToString() ?? "";

                string? media_id = categoryJSON["media_id"]?.ToString();
                if (!string.IsNullOrEmpty(media_id))
                {
                    AClass categoryMedia = _repository.select("media", media_id)[0];
                    categoryJSON["media"] = categoryMedia.serialize();  
                }

                categoriesArray.Add(categoryJSON);
            }

            jsonResponse["categories"] = categoriesArray;

            // Serve the main.jsp file
            return Json(jsonResponse);
        }
    }
}
