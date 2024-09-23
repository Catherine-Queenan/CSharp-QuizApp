<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add a New Question</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

        .title {
            font-size: 40px;
            margin: 0;
        }

        .wrap {
            padding: 60px 0;
            justify-content: unset;
            overflow-y: scroll;
            -ms-overflow-style: none;  /* Internet Explorer 10+ */
            scrollbar-width: none;  /* Firefox */
            -webkit-scrollbar: none;
            z-index: -99;
        }

        .newQuestionForm {
            transform: scale(0.9);
            width: 60%;
            padding: 50px;
            border-radius: 15px;
            font-size: 18px;
            background-color: #45425A;
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .newQuestionForm label {
            font-size: 22px;
            margin-top: 10px;
        }

        .newQuestionForm input,
        .newQuestionForm select {
            border: 0;
            border-radius: 10px;
            padding: 15px 20px;
            font-size: 18px;
        }

        .addAnotherAnswerBtn,
        .addQuestionBtn {
            all: unset;
            margin-top: 20px;
            padding: 20px 50px;
            border-radius: 15px;
            font-size: 20px;
            color: rgb(244, 244, 244);
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #0C1B33;
            cursor: pointer;
            transition-duration: 0.3s;
        }

        .addAnotherAnswerBtn {
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #99c252;
            color: #0C1B33;
        }

        .addAnotherAnswerBtn:hover,
        .addQuestionBtn:hover {
            box-shadow: inset 5px 5px 5px rgba(1, 1, 1, 0.5);
        }
        
        :focus {
            outline: none;
        }

        .mediaType {
            margin-bottom: 20px;
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

        function addQuestion() {
            questionCount++;
            const questionDiv = document.createElement('div');
            questionDiv.classList.add('question');
            questionDiv.innerHTML = `
                <label for="questionText">Question Text:</label>
                <input type="text" name="questionText" required>
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
        <form action="logout">
            <button class="logoutBtn" type="Submit">Log Out</button>
        </form>
    </header>

    <div class="wrap">
        <div class="title cherry-cream-soda">
            Add Question to <%= request.getAttribute("quizName") %>
        </div>

        <form class="newQuestionForm" method="post" action="addQuestion" enctype="multipart/form-data">
            <label for="questionText">Question Text:</label>
            <input type="text" id="questionTextQ1" name="questionTextQ1" required>

            <label for="questionType">Question Media Type:</label>
            <select class="mediaType" id="questionType" name="questionType">
                <option value="TEXT">None</option>
                <option value="VID">Video</option>
                <option value="IMG">Image</option>
                <option value="AUD">Audio</option>
                <!-- Add other question types as needed -->
            </select>

            <div id="imageAudioUploadQuestion" style="display: none;">
                <label for="mediaFile">File:</label>
                <input type="file" id="mediaFile" name="mediaFile" accept="audio/*,image/*" />
            <div id="audioStartEnd" style="display: none;">
                <label for="audioStart">Audio Start (seconds):</label>
                <input type="number" id="audioStart" name="audioStart" value="0"/>
                <label for="audioEnd">Audio End (seconds):</label>
                <input type="number" id="audioEnd" name="audioEnd" value="0"/>
            </div>

            <div id="videoUrlQ1" style="display: none;">
                <label for="videoUrlQ1">YouTube Video URL:</label>
                <input type="text" id="videoUrlQ1" name="videoUrlQ1">
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
                <button class="addAnotherAnswerBtn" type="button" onclick="addAnswer()">Add Another Answer</button>

            </div>
            <button id="addAnswerBtn" type="button" onclick="addAnswer()">Add Another Answer</button><br><br>
            <div id="questionsContainer"></div>

            <input type="hidden" name="quizName" value="<%= request.getAttribute(" quizName") %>">
            <button class="addQuestionBtn" type="submit">Add Question</button>
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