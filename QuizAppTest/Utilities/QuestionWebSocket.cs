using Microsoft.AspNetCore.Builder;
using Microsoft.AspNetCore.Http;
using Microsoft.Extensions.DependencyInjection;
using Newtonsoft.Json;
using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

public class QuestionWebSocketMiddleware
{
    private readonly RequestDelegate _next;
    private static readonly ConcurrentDictionary<string, UserSessionData> UserSessions = new();
    private static readonly ConcurrentDictionary<string, WebSocketQuestion> Questions = new();
    private static int QuestionIndex = 0;

    public QuestionWebSocketMiddleware(RequestDelegate next)
    {
        _next = next;
    }

    public async Task InvokeAsync(HttpContext context)
    {
        if (context.Request.Path == "/questionsws" && context.WebSockets.IsWebSocketRequest)
        {
            var webSocket = await context.WebSockets.AcceptWebSocketAsync();
            var sessionId = Guid.NewGuid().ToString();
            var userSession = new UserSessionData("Guest");
            UserSessions[sessionId] = userSession;

            await OnOpen(webSocket, sessionId);

            while (webSocket.State == System.Net.WebSockets.WebSocketState.Open)
            {
                var message = await ReceiveMessage(webSocket);
                if (!string.IsNullOrEmpty(message))
                {
                    await OnMessage(webSocket, message, sessionId);
                }
            }

            await OnClose(webSocket, sessionId);
        }
        else
        {
            await _next(context);
        }
    }

    private async Task OnOpen(System.Net.WebSockets.WebSocket webSocket, string sessionId)
    {
        Console.WriteLine($"Connection opened: {sessionId}");

        var userData = UserSessions[sessionId];
        userData.Questions.Clear();
        await SendCurrentQuestion(webSocket, userData);
    }

    private async Task OnMessage(System.Net.WebSockets.WebSocket webSocket, string message, string sessionId)
    {
        Console.WriteLine($"Received message: {message}");
        var userData = UserSessions[sessionId];

        if (message.StartsWith("["))
        {
            userData.CurrentQuestionIndex = 0;
            userData.Questions.Clear();

            var jsonArray = JsonConvert.DeserializeObject<List<WebSocketQuestion>>(message);
            userData.Questions.AddRange(jsonArray);

            await SendCurrentQuestion(webSocket, userData);
        }
        else
        {
            var jsonMessage = JsonConvert.DeserializeObject<dynamic>(message);

            if (jsonMessage.type == "answer")
            {
                var answer = (string)jsonMessage.answer;
                userData.AnswerCounts[answer] = userData.AnswerCounts.GetValueOrDefault(answer, 0) + 1;
                await BroadcastAnswerCounts();
            }
            else if (jsonMessage.type == "next")
            {
                if (userData.CurrentQuestionIndex < userData.Questions.Count - 1)
                {
                    IncrementQuestionForAllUsers();
                    await BroadcastCurrentQuestionToAll();
                }
                else
                {
                    ClearAllSessions();
                }
            }
        }
    }

    private async Task OnClose(System.Net.WebSockets.WebSocket webSocket, string sessionId)
    {
        QuestionIndex = 0;
        UserSessions.TryRemove(sessionId, out _);
        Console.WriteLine($"Connection closed: {sessionId}");
    }

    private async Task SendCurrentQuestion(System.Net.WebSockets.WebSocket webSocket, UserSessionData userData)
    {
        if (userData.CurrentQuestionIndex < userData.Questions.Count)
        {
            var currentQuestion = userData.Questions[userData.CurrentQuestionIndex];
            var json = JsonConvert.SerializeObject(new
            {
                question = currentQuestion.QuestionText,
                answers = currentQuestion.Answers,
                images = currentQuestion.Images,
                videos = currentQuestion.Videos,
                questionIndex = userData.CurrentQuestionIndex
            });

            await SendMessage(webSocket, json);
        }
        else
        {
            var json = JsonConvert.SerializeObject(new { type = "end" });
            await SendMessage(webSocket, json);
        }
    }

    private async Task BroadcastAnswerCounts()
    {
        var combinedCounts = GetCombinedAnswerCounts();
        var json = JsonConvert.SerializeObject(new { type = "answerCounts", counts = combinedCounts });
        await Broadcast(json);
    }

    private async Task Broadcast(string message)
    {
        foreach (var session in UserSessions)
        {
            // Simulate broadcast functionality; implement WebSocket group management for true broadcasting.
        }
    }

    private Dictionary<string, int> GetCombinedAnswerCounts()
    {
        return UserSessions.Values
            .SelectMany(session => session.AnswerCounts)
            .GroupBy(pair => pair.Key)
            .ToDictionary(group => group.Key, group => group.Sum(pair => pair.Value));
    }

    private void IncrementQuestionForAllUsers()
    {
        QuestionIndex++;
        foreach (var userData in UserSessions.Values)
        {
            userData.CurrentQuestionIndex++;
            userData.AnswerCounts.Clear();
        }
    }

    private async Task BroadcastCurrentQuestionToAll()
    {
        foreach (var session in UserSessions)
        {
            var userData = session.Value;
            // Broadcast current question logic
        }
    }

    private void ClearAllSessions()
    {
        UserSessions.Clear();
        QuestionIndex = 0;
        Console.WriteLine("All sessions cleared.");
    }

    private async Task<string> ReceiveMessage(System.Net.WebSockets.WebSocket webSocket)
    {
        var buffer = new byte[1024 * 4];
        var result = await webSocket.ReceiveAsync(new ArraySegment<byte>(buffer), CancellationToken.None);
        return Encoding.UTF8.GetString(buffer, 0, result.Count);
    }

    private async Task SendMessage(System.Net.WebSockets.WebSocket webSocket, string message)
    {
        var buffer = Encoding.UTF8.GetBytes(message);
        await webSocket.SendAsync(new ArraySegment<byte>(buffer), System.Net.WebSockets.WebSocketMessageType.Text, true, CancellationToken.None);
    }

    public class WebSocketQuestion
    {
        public string QuestionText { get; set; }
        public List<string> Answers { get; set; } = new();
        public List<string> Images { get; set; } = new();
        public List<string> Videos { get; set; } = new();
    }

    public class UserSessionData
    {
        public string Username { get; set; }
        public List<WebSocketQuestion> Questions { get; set; } = new();
        public int CurrentQuestionIndex { get; set; }
        public ConcurrentDictionary<string, int> AnswerCounts { get; set; } = new();

        public UserSessionData(string username)
        {
            Username = username;
        }
    }
}
