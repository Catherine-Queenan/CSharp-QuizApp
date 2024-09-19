<!DOCTYPE html>
<html>
<head>
    <title>Create Quiz</title>
</head>
<body>
    <h1>Create a New Quiz</h1>
    <% if (request.getAttribute("error") != null) { %>
        <p style="color:red;"><%= request.getAttribute("error") %></p>
    <% } %>
    <form action="createQuiz" method="post">
        <label for="quizName">Quiz Name:</label>
        <input type="text" id="quizName" name="quizName" required />
        <input type="submit" value="Create Quiz" />
    </form>

    
</body>
</html>
