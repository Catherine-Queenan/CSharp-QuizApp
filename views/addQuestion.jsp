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
            const answerCount = document.querySelectorAll('input[name="answerText"]').length + 1;
            answerDiv.innerHTML = `
                <input type="text" name="answerText" placeholder="Answer ${answerCount}" required>
                <input type="radio" name="correctAnswer" value="${answerCount}"> Correct
            `;
            document.getElementById('answersContainer').appendChild(answerDiv);
        }

        function addQuestion() {
            const questionDiv = document.createElement('div');
            questionDiv.classList.add('question');
            questionDiv.innerHTML = `
                <label for="questionText">Question Text:</label>
                <input type="text" name="questionText" required><br>
                
                <label for="questionType">Question Type:</label>
                <input type="text" name="questionType" required><br>

                <div class="answersContainer" id="answersContainer">
                    <div class="answer">
                        <input type="text" name="answerText" placeholder="Answer 1" required>
                        <input type="radio" name="correctAnswer" value="1"> Correct
                    </div>
                </div>
                <button type="button" onclick="addAnswer()">Add Another Answer</button><br><br>
                <label for="mediaFile">Upload Media (optional):</label>
                <input type="file" name="mediaFile" accept="image/*,video/*" /><br />
            `;
            document.getElementById('questionsContainer').appendChild(questionDiv);
        }
    </script>
</head>
<body>
    <div class="container">
        <h1>Add Question to <%= request.getAttribute("quizName") %></h1>

        <form method="post" action="addQuestion" enctype="multipart/form-data">
            <label for="questionText">Question Text:</label>
            <input type="text" id="questionText" name="questionText" required><br>
            
            <label for="questionType">Question Type:</label>
        <select id="questionType" name="questionType">
            <option value="multipleChoice">Multiple Choice</option>
            <option value="trueFalse">True/False</option>
            <!-- Add other question types as needed -->
        </select><br><br>

        <label for="mediaType">Media Type:</label>
        <select id="mediaType" name="mediaType">
            <option value="none">None</option>
            <option value="image">Image</option>
            <option value="video">YouTube Video</option>
        </select><br><br>

        <div id="imageUrl" style="display: none;">
            <label for="imageUrl">Image URL:</label>
            <input type="text" id="imageUrl" name="imageUrl"><br><br>
        </div>
        <div id="videoUrl" style="display: none;">
            <label for="videoUrl">YouTube Video URL:</label>
            <input type="text" id="videoUrl" name="videoUrl"><br><br>
        </div>
        <!-- <label for="mediaFile">Upload Media (optional):</label>
        <input type="file" name="mediaFile" accept="image/*,video/*" /><br /> -->

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
            <button type="button" onclick="addQuestion()">Add Another Question</button><br><br>
            <button type="submit">Submit All Questions</button>        </form>
    </div>

    <script>
        document.getElementById('mediaType').addEventListener('change', function() {
            if (this.value === 'image') {
                document.getElementById('imageUrl').style.display = 'block';
                document.getElementById('videoUrl').style.display = 'none';
            } else if (this.value === 'video') {
                document.getElementById('imageUrl').style.display = 'none';
                document.getElementById('videoUrl').style.display = 'block';
            } else {
                document.getElementById('imageUrl').style.display = 'none';
                document.getElementById('videoUrl').style.display = 'none';
            }
        });
    </script>
</body>
</html>

