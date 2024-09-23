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

        .hidden {
            display: none;
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

        let answerCount = 2;
        function addAnswer() {
            if (answerCount == 3) {
                document.getElementById('addAnswerBtn').classList.add('hidden');
            }
            answerCount++;
            const answerDiv = document.createElement('div');
            answerDiv.classList.add(`answer`);
            answerDiv.id = `answer`;
            answerDiv.innerHTML = `
                <input type="text" name="answerText" placeholder="Answer ${answerCount}" required>
                <input type="radio" name="correctAnswer" value="${answerCount}"> Correct
            `;
            document.getElementById('answersContainer').appendChild(answerDiv);
        }

        // function addQuestion() {
        //     questionCount++;
        //     const questionDiv = document.createElement('div');
        //     questionDiv.classList.add('question');
        //     questionDiv.innerHTML = `
        //         <label for="questionText">Question Text:</label>
        //         <input type="text" name="questionText" required><br>
                
        //         <label for="questionType">Question Type:</label>
        //         <input type="text" name="questionType" required><br>

        //         <div class="answersContainer" id="answersContainer">
        //             <div class="answer">
        //                 <input type="text" name="answerText" placeholder="Answer 1" required>
        //                 <input type="radio" name="correctAnswer" value="1"> Correct
        //             </div>
        //             <button type="button" onclick="addAnswer()">Add Another Answer</button><br><br>
        //         </div>

        //         <label for="mediaFile">Upload Media (optional):</label>
        //         <input type="file" name="mediaFile" accept="image/*,video/*" /><br />
        //     `;
        //     document.getElementById('questionsContainer').appendChild(questionDiv);
        // }

    </script>
</head>

<body>
    <header>
        <form action="home">
            <button class="homeBtn" type="Submit">Home</button>
        </form>
        <form method="post">
            <input type="hidden" value="true" name="restart">
            <button class="restartBtn" type="Submit">Restart</button>
        </form>
    </header>
    <div class="container">
        <h1>Add Question to <%= request.getAttribute("quizName") %>
        </h1>

        <form method="post" enctype="multipart/form-data">
            <label for="questionText">Question Text:</label>
            <input type="text" id="questionText" name="questionText" required><br>

            <label for="questionType">Question Media Type:</label>
            <select class="mediaType" id="questionType" name="questionType">
                <option value="TEXT">None</option>
                <option value="VID">Video</option>
                <option value="IMG">Image</option>
                <option value="AUD">Audio</option>
                <!-- Add other question types as needed -->
            </select><br><br>

            <div id="imageAudioUploadQuestion" style="display: none;">
                <label for="mediaFile">File:</label>
                <input type="file" id="mediaFile" name="mediaFile" accept="audio/*,image/*" />
            <div id="audioStartEnd" style="display: none;">
                <label for="audioStart">Audio Start (seconds):</label>
                <input type="number" id="audioStart" name="audioStart" value="0"/>
                <label for="audioEnd">Audio End (seconds):</label>
                <input type="number" id="audioEnd" name="audioEnd" value="0"/>
            </div>
            </div>

            <div id="videoUrlQuestion" style="display: none;">
                <label for="videoUrl">YouTube Video URL:</label>
                <input type="text" id="videoUrl" name="videoUrl"><br><br>
                <label for="videoStart">Clip Start (seconds):</label>
                <input type="number" id="videoStart" name="videoStart" value="0"/>
                <label for="videoEnd">Clip End (seconds):</label>
                <input type="number" id="videoEnd" name="videoEnd" value="0"/>
            </div>
<!-- 
            <label for="answerTypeQ1">Answer Media Type:</label>
                    <select class="mediaType" id="answerTypeQ1A2" name="answerTypeQ1A2">
                        <option value="TEXT">None</option>
                        <option value="VID">Video</option>
                        <option value="IMG">Image</option>
                        <option value="AUD">Audio</option>
                    </select><br><br> -->
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
            <button id="addAnswerBtn" type="button" onclick="addAnswer()">Add Another Answer</button><br><br>
            <div id="questionsContainer"></div>

            <input type="hidden" name="quizName" value="<%= request.getAttribute(" quizName") %>">
            <button type="submit">Add Question</button>
        </form>
    </div>

    <script>
        let questionMedia = document.getElementById('questionType');
        questionMedia.addEventListener('change', function () {
                if (questionMedia.value === 'IMG' || questionMedia.value === 'AUD') {
                    document.getElementById(`imageAudioUploadQuestion`).style.display = 'block';
                    document.getElementById(`videoUrlQuestion`).style.display = 'none';
                    if(questionMedia.value === 'AUD'){
                        document.getElementById("audioStartEnd").style.display = 'block';
                    }
                } else if (questionMedia.value === 'VID') {
                    document.getElementById(`imageAudioUploadQuestion`).style.display = 'none';
                    document.getElementById(`videoUrlQuestion`).style.display = 'block';
                    document.getElementById("audioStartEnd").style.display = 'none';
                } else {
                    document.getElementById(`imageAudioUploadQuestion`).style.display = 'none';
                    document.getElementById(`videoUrlQuestion`).style.display = 'none';
                    document.getElementById("audioStartEnd").style.display = 'none';
                }
            });
    </script>
</body>

</html>