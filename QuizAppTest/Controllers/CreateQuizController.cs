using System.ComponentModel.DataAnnotations;
using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Hosting;
using Microsoft.AspNetCore.Http;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [Route("api/[controller]")]
    [ApiController]
    public class CreateQuizController : Controller
    {
        private readonly ILogger<CreateQuizController> _logger;
        private readonly IRepository _repository;
        private readonly AClassFactory _classFactory;
        private readonly IConfiguration _configuration;

        public CreateQuizController(ILogger<CreateQuizController> logger, IConfiguration configuration, DatabaseUtil databaseUtil)
        {
            _logger = logger;
            _repository = new Repository();
            _repository.init(databaseUtil);
            _classFactory = new AClassFactory();
            _configuration = configuration;
        }

        [HttpGet]
        public IActionResult Get()
        {
            string? userRole = HttpContext.Session.GetString("USER_ROLE");

            if (string.IsNullOrEmpty(userRole))
            {
                return Redirect("/login");
            }

            if(userRole != "a")
            {
                return Unauthorized(new { Status = "Error", Message = "401 You are not authorized to access this page." });
            }

            JsonObject jsonResponse = new JsonObject();
            jsonResponse["role"] = userRole;

            JsonArray categoriesArray = [];

            List<AClass> categories = _repository.select("category", "");
            foreach (AClass category in categories)
            {
                categoriesArray.Add(category.serialize());
            }
            jsonResponse["categories"] = categoriesArray;
            return Json(jsonResponse);
        }

        [HttpPost]
        public async Task<IActionResult> Post([FromForm] CreateQuizRequest req)
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

            string categoryName = req.CategoryName;

            if(req.CategoryName == "ADDANOTHERCATEGORY" && req.NewCategory != null)
            {
                categoryName = req.NewCategory;
                string insertCategory = "name:==" + categoryName;
                
                if(req.CategoryMedia != null)
                {
                    Guid newGuid = Guid.NewGuid();
                    byte[] categoryMediaId = newGuid.ToByteArray();
                    string categoryMediaIdString = BitConverter.ToString(categoryMediaId).Replace("-", "");
                    insertCategory += ",,,media_id:==" + categoryMediaIdString;

                    string filePath = Path.Combine(_configuration["UploadPath"] ?? "C:\\temp\\uploads", req.CategoryMedia.FileName);

                    using (var stream = new FileStream(filePath, FileMode.Create))
                    {
                        await req.CategoryMedia.CopyToAsync(stream);
                    }

                    string storedUrl = Path.Combine("./uploads", req.CategoryMedia.FileName);

                    string insertMedia = "id:==" + categoryMediaIdString
                                        + ",,,media_type:==IMG"
                                        + ",,,media_file_path:==" + storedUrl
                                        + ",,,media_filename:==" + req.CategoryMedia.FileName;

                    _repository.insert(_classFactory.createAClass("media", insertMedia));
                }

                _repository.insert(_classFactory.createAClass("category", insertCategory));
            }

            string insertQuiz = "name:==" + req.QuizName
                    + ",,,category_name:==" + categoryName
                    + ",,,description:==" + req.Description;

            if (req.QuizMedia != null)
            {
                Guid newGuid = Guid.NewGuid();
                byte[] quizMediaId = newGuid.ToByteArray();
                string quizMediaIdString = BitConverter.ToString(quizMediaId).Replace("-", "");

                insertQuiz += ",,,media_id:==" + quizMediaIdString;

                string filePath = Path.Combine(_configuration["UploadPath"] ?? "C:\\temp\\uploads", req.QuizMedia.FileName);

                using (var stream = new FileStream(filePath, FileMode.Create))
                {
                    await req.QuizMedia.CopyToAsync(stream);
                }

                string storedUrl = Path.Combine("./uploads", req.QuizMedia.FileName);

                string insertMedia = "id:==" + quizMediaIdString
                                        + ",,,media_type:==IMG"
                                        + ",,,media_file_path:==" + storedUrl
                                        + ",,,media_filename:==" + req.QuizMedia.FileName;

                _repository.insert(_classFactory.createAClass("media", insertMedia));
            }

            _repository.insert(_classFactory.createAClass("quiz", insertQuiz));

            JsonObject jsonResponse = new JsonObject();
            jsonResponse["message"] = "Quiz created successfully!\", \"categoryName\": \"" + categoryName + "\"";
            
            return Ok(jsonResponse);
        }
    }

    public class CreateQuizRequest
    {
        public required string QuizName { get; set; }
        public string? QuizImage { get; set; }
        public IFormFile? QuizMedia { get; set; }
        public required string CategoryName { get; set; }
        public string? NewCategory {  get; set; }
        public string? CategoryImage { get; set; }
        public IFormFile? CategoryMedia { get; set; }
        public string? Description {  get; set; }
    }
}
