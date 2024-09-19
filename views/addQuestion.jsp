<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Question</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            padding: 20px;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 600px;
            margin: auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 10px;
        }
        input[type="text"], input[type="radio"] {
            width: calc(100% - 22px);
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        input[type="radio"] {
            width: auto;
            margin-right: 10px;
        }
        .answer {
            margin-bottom: 10px;
        }
        button {
            padding: 10px 20px;
            background-color: #5cb85c;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #4cae4c;
        }
        .button-link {
            display: inline-block;
            padding: 10px 20px;
            background-color: #0275d8;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .button-link:hover {
            background-color: #025aa5;
        }
    </style>
    <script>
        function addAnswer() {
            const answerDiv = document.createElement('div');
            answerDiv.classList.add('answer');
            answerDiv.innerHTML = `
                <input type="text" name="answerText" placeholder="Answer" required>
                <input type="radio" name="correctAnswer" value="${document.querySelectorAll('input[name="answerText"]').length + 1}"> Correct
            `;
            document.getElementById('answersContainer').appendChild(answerDiv);
        }
    </script>
</head>
<body>
    <div class="container">
        <h1>Add Question to <%= request.getAttribute("quizName") %></h1>

        <form method="post" action="addQuestion">
            <label for="questionText">Question Text:</label>
            <input type="text" id="questionText" name="questionText" required><br>
            
            <label for="questionType">Question Type:</label>
            <input type="text" id="questionType" name="questionType" required><br>

            <div id="answersContainer">
                <div class="answer">
                    <input type="text" name="answerText" placeholder="Answer 1" required>
                    <input type="radio" name="correctAnswer" value="1"> Correct
                </div>
                <div class="answer">
                    <input type="text" name="answerText" placeholder="Answer 2" required>
                    <input type="radio" name="correctAnswer" value="2"> Correct
                </div>
                <!-- Add more answers dynamically if needed -->
            </div>
            <button type="button" onclick="addAnswer()">Add Another Answer</button><br><br>

            <input type="hidden" name="quizName" value="<%= request.getAttribute("quizName") %>">
            <button type="submit">Add Question</button>
        </form>
    </div>
</body>
</html>
