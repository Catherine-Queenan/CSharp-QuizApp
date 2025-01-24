using Microsoft.AspNetCore.SignalR;
using Newtonsoft.Json;
using System.Collections.Concurrent;

public class QuestionHub : Hub
{
    private static ConcurrentDictionary<string, UserSessionData> userSessions = new();
    private static List<WebSocketQuestion> questions = new();
    private static int questionIndex = 0;

    public class WebSocketQuestion
    {
        public string QuestionText { get; set; }
        public List<string> Answers { get; set; }
        public List<string> Images { get; set; }
        public List<string> Videos { get; set; }

        public WebSocketQuestion(string questionText, List<string> answers, List<string> images, List<string> videos)
        {
            QuestionText = questionText;
            Answers = answers;
            Images = images;
            Videos = videos;
        }
    }

    public class UserSessionData
    {
        public string Username { get; set; }
        public List<WebSocketQuestion> Questions { get; set; } = new();
        public int CurrentQuestionIndex { get; set; } = 0;
        public ConcurrentDictionary<string, int> AnswerCounts { get; set; } = new();

        public UserSessionData(string username)
        {
            Username = username;
        }
    }

    public override async Task OnConnectedAsync()
    {
        var username = Context.User?.Identity?.Name ?? "Guest";

        Console.WriteLine($"Connection opened: {Context.ConnectionId} with user: {username}");

        var userData = new UserSessionData(username);
        userSessions[Context.ConnectionId] = userData;

        await SendCurrentQuestion(Context.ConnectionId);

        await base.OnConnectedAsync();
    }

    public override async Task OnDisconnectedAsync(Exception? exception)
    {
        if (userSessions.TryRemove(Context.ConnectionId, out _))
        {
            Console.WriteLine($"Connection closed: {Context.ConnectionId}");
        }

        await base.OnDisconnectedAsync(exception);
    }

    public async Task ReceiveMessage(string message)
    {
        Console.WriteLine($"Received message: {message}");
        var userData = userSessions[Context.ConnectionId];

        try
        {
            if (message.StartsWith("["))
            {
                // Parse questions and reset user session data
                var questionList = JsonConvert.DeserializeObject<List<WebSocketQuestion>>(message);
                userData.Questions.Clear();
                userData.AnswerCounts.Clear();
                userData.CurrentQuestionIndex = 0;

                if (questionList != null)
                {
                    userData.Questions.AddRange(questionList);
                }

                await SendCurrentQuestion(Context.ConnectionId);
            }
            else
            {
                // Handle "answer" or "next" message types
                var jsonMessage = JsonConvert.DeserializeObject<Dictionary<string, string>>(message);
                if (jsonMessage != null && jsonMessage.ContainsKey("type"))
                {
                    if (jsonMessage["type"] == "answer")
                    {
                        var answer = jsonMessage["answer"];
                        userData.AnswerCounts.AddOrUpdate(answer, 1, (key, oldValue) => oldValue + 1);
                        await BroadcastAnswerCounts();
                    }
                    else if (jsonMessage["type"] == "next")
                    {
                        if (userData.CurrentQuestionIndex < userData.Questions.Count - 1)
                        {
                            IncrementQuestionForAllUsers();
                            await BroadcastCurrentQuestionToAll();
                        }
                        else
                        {
                            await ClearAllSessions();
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine(ex.Message);
        }
    }

    private async Task SendCurrentQuestion(string connectionId)
    {
        var userData = userSessions[connectionId];
        if (userData.CurrentQuestionIndex < userData.Questions.Count)
        {
            var currentQuestion = userData.Questions[userData.CurrentQuestionIndex];

            var json = new
            {
                question = currentQuestion.QuestionText,
                answers = currentQuestion.Answers,
                images = currentQuestion.Images,
                videos = currentQuestion.Videos,
                questionIndex = userData.CurrentQuestionIndex
            };

            await Clients.Client(connectionId).SendAsync("ReceiveQuestion", json);
        }
        else if (userData.CurrentQuestionIndex == userData.Questions.Count - 1)
        {
            var json = new { type = "end" };
            await Clients.Client(connectionId).SendAsync("ReceiveMessage", json);
        }
    }

    private async Task BroadcastAnswerCounts()
    {
        var combinedCounts = GetCombinedAnswerCounts();
        var json = new
        {
            type = "answerCounts",
            counts = combinedCounts
        };

        await Clients.All.SendAsync("ReceiveAnswerCounts", json);
    }

    private Dictionary<string, int> GetCombinedAnswerCounts()
    {
        var combinedCounts = new ConcurrentDictionary<string, int>();
        foreach (var userData in userSessions.Values)
        {
            foreach (var kvp in userData.AnswerCounts)
            {
                combinedCounts.AddOrUpdate(kvp.Key, kvp.Value, (key, oldValue) => oldValue + kvp.Value);
            }
        }
        return combinedCounts.ToDictionary(kvp => kvp.Key, kvp => kvp.Value);
    }

    private void IncrementQuestionForAllUsers()
    {
        questionIndex++;
        foreach (var userData in userSessions.Values)
        {
            userData.CurrentQuestionIndex++;
            userData.AnswerCounts.Clear();
        }
    }

    private async Task BroadcastCurrentQuestionToAll()
    {
        foreach (var connectionId in userSessions.Keys)
        {
            await SendCurrentQuestion(connectionId);
        }
    }

    private async Task ClearAllSessions()
    {
        questionIndex = 0;
        userSessions.Clear();
        await Clients.All.SendAsync("SessionCleared");
        Console.WriteLine("All sessions cleared.");
    }
}
