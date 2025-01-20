using System.Text.Json.Nodes;
using Microsoft.AspNetCore.Mvc;
using QuizApp.Utilities;

namespace QuizApp.Controllers
{
    [ApiController]
    [Route("api/play")]
    public class PlayQuizController : Controller
    {
        private readonly ILogger<PlayQuizController> _logger;
        private readonly IRepository _repository;

        public PlayQuizController(ILogger<PlayQuizController> logger, DatabaseUtil databaseUtil)
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
                return Redirect("/login");
            }

            JsonObject jsonResponse = new JsonObject();
            jsonResponse["role"] = userRole;

            string? quizName = HttpContext.Session.GetString("quiz");
            int currQuestion = HttpContext.Session.GetInt32("currQuestion") ?? 0;
            string? questions = HttpContext.Session.GetString("questions");

           

            if (string.IsNullOrEmpty(quizName) || questions == null)
            {
                return Redirect(Request.Headers["Referer"].ToString());
            }
             jsonResponse["quizName"] = quizName;
            JsonArray questionsArrayJSON = (JsonArray)(JsonArray.Parse(questions) ?? new JsonArray());

            string? autoplayEnabled = HttpContext.Session.GetString("autoplay");
            bool isAutoplayEnabled = bool.TryParse(autoplayEnabled, out bool result) ? result : false;
            jsonResponse["autoPlayEnabled"] = isAutoplayEnabled;

            if (questionsArrayJSON.Count < 1)
            {
                jsonResponse["currQuestion"] = currQuestion;
                jsonResponse["quizSize"] = 0;

                return Json(jsonResponse);
            }

            JsonNode? checkQuestion = questionsArrayJSON[currQuestion];
            if (checkQuestion != null) 
            {
                JsonObject question = (JsonObject)(checkQuestion ?? new JsonObject());
                question = BuildQuestionJson(question);
                jsonResponse["question"] = question.DeepClone();
            } else
            {
                jsonResponse["question"] = new JsonObject();
            }


            jsonResponse["currQuestion"] = currQuestion + 1;
            jsonResponse["quizSize"] = questionsArrayJSON.Count;

            return Json(jsonResponse);
        }

        [HttpPost]
        public IActionResult Post([FromForm] RestartRequest request)
        {
            string? userRole = HttpContext.Session.GetString("USER_ROLE");
            if (string.IsNullOrEmpty(userRole))
            {
                return Redirect("/login");
            }

            string? quizName = HttpContext.Session.GetString("quiz");
            int currQuestion = HttpContext.Session.GetInt32("currQuestion") ?? 0;
            string? questions = HttpContext.Session.GetString("questions");

            if (string.IsNullOrEmpty(quizName) || string.IsNullOrEmpty(questions))
            {
                return Redirect(Request.Headers["Referer"].ToString());
            }

            JsonArray questionsArrayJSON = (JsonArray)(JsonArray.Parse(questions) ?? new JsonArray());

            if (++currQuestion >= questionsArrayJSON.Count)
            {
                return Redirect("/end");
            }

            if (request.Restart != null)
            {
                ShuffleJsonArray(questionsArrayJSON);
                HttpContext.Session.SetInt32("currQuestion", 0);
                HttpContext.Session.SetString("questions", questionsArrayJSON.ToString());
            } else
            {
                HttpContext.Session.SetInt32("currQuestion", currQuestion);
            }
            return Redirect("/questions");
        }

        private void ShuffleJsonArray(JsonArray arr)
        {
            Random rand = new Random();
            int arrCount = arr.Count;
            for (int i = arrCount - 1; i > 0; i--)
            {
                int randIndex = rand.Next(0, i + 1);
                SwapArrayItems(arr, randIndex, i);
            }
        }

        private void SwapArrayItems(JsonArray arr, int a, int b)
        {
            var temp = arr[a];
            arr[a] = arr[b];
            arr[b] = temp;
        }

        private JsonObject BuildQuestionJson(JsonObject questionJSON)
        {
            JsonObject json = questionJSON;
            JsonObject? media = GetMedia(json);
            if(media != null)
            {
                json["media"] = media;
            }
            json["answers"] = BuildAnswerJson(json);

            return json;
        }

        private JsonObject? GetMedia(JsonObject json)
        {
            string? media_id = json["media_id"]?.ToString();

            if (!string.IsNullOrEmpty(media_id))
            {
                AClass media = _repository.select("media", media_id)[0];
                return media.serialize();
            }

            return null;
        }

        private JsonArray BuildAnswerJson(JsonObject questionJSON)
        {
            string criteria = questionJSON["id"] + ", ORDER BY rand()";
            List<AClass> answers = _repository.select("answer", criteria);
            JsonArray answersArray = new JsonArray();
            foreach (AClass answer in answers)
            {
                JsonObject json = answer.serialize();
                JsonObject? media = GetMedia(json);
                if(media != null)
                {
                    json["media"] = media;
                }
                
                answersArray.Add(json);
            }
            return answersArray;
        }
        
    }
}

public class RestartRequest
{
    public string Restart { get; set; }
}
