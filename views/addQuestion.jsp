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
        input[type="text"], input[type="number"], select {
            width: calc(100% - 22px);
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
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
        .answer {
            margin-bottom: 10px;
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
        <form method="POST" action="addQuestion" enctype="multipart/form-data">
            <input type="hidden" name="quizName" value="<%= request.getAttribute("quizName") %>">
            <!-- <input type="hidden" name="quizName" value="${quizName}"> -->
            <!-- Question text -->
            <label for="questionText">Question:</label>
            <input type="text" name="questionText" required><br>

            <!-- Question Type (e.g., multiple-choice) -->
            <label for="questionType">Question Type:</label>
            <select name="questionType" required>
                <option value="multiple-choice">Multiple Choice</option>
                <option value="true-false">True/False</option>
            </select><br>

            <!-- Media type (optional) -->
            <label for="mediaType">Media Type:</label>
            <select name="mediaType">
                <option value="">None</option>
                <option value="image">Image</option>
                <option value="video">Video</option>
            </select><br>

            <!-- Media file upload -->
            <label for="mediaFile">Upload Media:</label>
            <input type="file" name="mediaFile" accept="image/*,video/*"><br>

            <!-- Answers -->
            <label for="answerText">Answers:</label>
            <div id="answersContainer">
                <div class="answer">
                    <input type="text" name="answerText" placeholder="Answer 1" required>
                    <input type="radio" name="correctAnswer" value="1" required> Correct
                </div>
                <div class="answer">
                    <input type="text" name="answerText" placeholder="Answer 2" required>
                    <input type="radio" name="correctAnswer" value="2"> Correct
                </div>
            </div>
            <button type="button" onclick="addAnswer()">Add Answer</button><br>

            <button type="submit">Submit Question</button>
        </form>
    </div>
</body>
</html>
