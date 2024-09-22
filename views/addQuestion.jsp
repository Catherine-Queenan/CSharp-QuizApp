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

        input[type="text"],
        input[type="radio"] {
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

        let questionCount = 1;
        function addAnswer() {
            const answerDiv = document.createElement('div');
            answerDiv.classList.add(`answerQ${questionCount}`);
            const answerCount = document.getElementById(`answerContainerQ${questionCount}`).querySelectorAll('input[name="answerText"]').length + 1;
            answerDiv.innerHTML = `
                <input type="text" name="answerText" placeholder="Answer ${answerCount}" required>
                <input type="radio" name="correctAnswer" value="${answerCount}"> Correct
            `;
            document.getElementById('answersContainer').appendChild(answerDiv);
        }

        function addQuestion() {
            questionCount++;
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
                    <button type="button" onclick="addAnswer()">Add Another Answer</button><br><br>
                </div>

                <label for="mediaFile">Upload Media (optional):</label>
                <input type="file" name="mediaFile" accept="image/*,video/*" /><br />
            `;
            document.getElementById('questionsContainer').appendChild(questionDiv);
        }

    </script>
</head>

<body>
    <div class="container">
        <h1>Add Question to <%= request.getAttribute("quizName") %>
        </h1>

        <form method="post" action="addQuestion" enctype="multipart/form-data">
            <label for="questionText">Question Text:</label>
            <input type="text" id="questionTextQ1" name="questionTextQ1" required><br>

            <label for="questionTypeQ1">Question Media Type:</label>
            <select class="mediaType" id="questionTypeQ1" name="questionTypeQ1">
                <option value="TEXT">None</option>
                <option value="VID">Video</option>
                <option value="IMG">Image</option>
                <option value="AUD">Audio</option>
                <!-- Add other question types as needed -->
            </select><br><br>

            <div id="imageAudioUploadQ1" style="display: none;">
                <label for="mediaFileQ1">File:</label>
                <input type="file" id="mediaFileQ1" name="mediaFileQ1" accept="audio/*,image/*" />
            </div>

            <div id="videoUrlQ1" style="display: none;">
                <label for="videoUrlQ1">YouTube Video URL:</label>
                <input type="text" id="videoUrlQ1" name="videoUrlQ1"><br><br>
            </div>

            <div id="answersContainerQ1">
                <div class="answerQ1">
                    <label for="answerTypeQ1A1">Answer Media Type:</label>
                    <select class="mediaType" id="answerTypeQ1A1" name="answerTypeQ1A1">
                        <option value="TEXT">None</option>
                        <option value="VID">Video</option>
                        <option value="IMG">Image</option>
                        <option value="AUD">Audio</option>
                        <!-- Add other question types as needed -->
                    </select><br><br>

                    <div id="imageAudioUploadQ1A1" style="display: none;">
                        <label for="imageAudioUploadQ1A1">File:</label>
                        <input type="file" id="imageAudioUploadQ1A1" name="mediaFileQ1A1" accept="audio/*,image/*" />
                    </div>

                    <div id="videoUrlQ1A1" style="display: none;">
                        <label for="videoUrlQ1A1">YouTube Video URL:</label>
                        <input type="text" id="videoUrlQ1A1" name="videoUrlQ1A1"><br><br>
                    </div>

                    <input type="text" name="answerTextQ1" placeholder="Answer 1" required>
                    <input type="radio" name="correctAnswerQ1" value="1"> Correct
                </div>
                <div class="answerQ1">
                    <label for="answerTypeQ1">Answer Media Type:</label>
                    <select class="mediaType" id="answerTypeQ1A2" name="answerTypeQ1A2">
                        <option value="TEXT">None</option>
                        <option value="VID">Video</option>
                        <option value="IMG">Image</option>
                        <option value="AUD">Audio</option>
                        <!-- Add other question types as needed -->
                    </select><br><br>

                    <div id="imageAudioUploadQ1A2" style="display: none;">
                        <label for="imageAudioUploadQ1A2">File:</label>
                        <input type="file" id="imageAudioUploadQ1A2" name="mediaFileQ1A2" accept="audio/*,image/*" />
                    </div>

                    <div id="videoUrlQ1A2" style="display: none;">
                        <label for="videoUrlQ1A2">YouTube Video URL:</label>
                        <input type="text" id="videoUrlQ1A2" name="videoUrlQ1A2"><br><br>
                    </div>

                    <input type="text" name="answerTextQ1" placeholder="Answer 2" required>
                    <input type="radio" name="correctAnswerQ1" value="2"> Correct
                </div>
                <!-- Add more answers dynamically if needed -->
                <button type="button" onclick="addAnswer()">Add Another Answer</button><br><br>

            </div>
            <div id="questionsContainer"></div>

            <input type="hidden" name="quizName" value="<%= request.getAttribute(" quizName") %>">
            <button type="submit">Submit All Questions</button>
        </form>
    </div>

    <script>
        document.getElementsByClassName('mediaType').array.forEach(element => {
            element.addEventListener('change', function () {
                let mediaId = element.id.split("Type")[1];
                if (this.value === 'image') {
                    document.getElementById(`imageAudioUpload${mediaId}`).style.display = 'block';
                    document.getElementById(`videoUrl${mediaId}`).style.display = 'none';
                } else if (this.value === 'video') {
                    document.getElementById(`imageAudioUpload${mediaId}`).style.display = 'none';
                    document.getElementById(`videoUrl${mediaId}`).style.display = 'block';
                } else {
                    document.getElementById(`imageAudioUpload${mediaId}`).style.display = 'none';
                    document.getElementById(`videoUrl${mediaId}`).style.display = 'none';
               }
            });

        });
    </script>
</body>

</html>