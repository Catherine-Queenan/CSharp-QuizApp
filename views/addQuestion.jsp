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
                <input type="text" name="questionText" required>
                
                <label for="questionType">Question Type:</label>
                <input type="text" name="questionType" required>

                <div class="answersContainer" id="answersContainer">
                    <div class="answer">
                        <input type="text" name="answerText" placeholder="Answer 1" required>
                        <input type="radio" name="correctAnswer" value="1"> Correct
                    </div>
                    <button type="button" onclick="addAnswer()">Add Another Answer</button>
                </div>

                <label for="mediaFile">Upload Media (optional):</label>
                <input type="file" name="mediaFile" accept="image/*,video/*" /><br />
            `;
            document.getElementById('questionsContainer').appendChild(questionDiv);
        }

    </script>
</head>
<body>
    <header>
        <form action="home">
            <button class="homeBtn" type="Submit">Home</button>
        </form>
    </header>

    <div class="wrap">
        <div class="title cherry-cream-soda">
            Add Question to <%= request.getAttribute("quizName") %>
        </div>

        <form class="newQuestionForm" method="post" action="addQuestion" enctype="multipart/form-data">
            <label for="questionText">Question Text:</label>
            <input type="text" id="questionTextQ1" name="questionTextQ1" required>

            <label for="questionTypeQ1">Question Media Type:</label>
            <select class="mediaType" id="questionTypeQ1" name="questionTypeQ1">
                <option value="TEXT">None</option>
                <option value="VID">Video</option>
                <option value="IMG">Image</option>
                <option value="AUD">Audio</option>
                <!-- Add other question types as needed -->
            </select>

            <div id="imageAudioUploadQ1" style="display: none;">
                <label for="mediaFileQ1">File:</label>
                <input type="file" id="mediaFileQ1" name="mediaFileQ1" accept="audio/*,image/*" />
            </div>

            <div id="videoUrlQ1" style="display: none;">
                <label for="videoUrlQ1">YouTube Video URL:</label>
                <input type="text" id="videoUrlQ1" name="videoUrlQ1">
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
                    </select>

                    <div id="imageAudioUploadQ1A1" style="display: none;">
                        <label for="imageAudioUploadQ1A1">File:</label>
                        <input type="file" id="imageAudioUploadQ1A1" name="mediaFileQ1A1" accept="audio/*,image/*" />
                    </div>

                    <div id="videoUrlQ1A1" style="display: none;">
                        <label for="videoUrlQ1A1">YouTube Video URL:</label>
                        <input type="text" id="videoUrlQ1A1" name="videoUrlQ1A1">
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
                    </select>

                    <div id="imageAudioUploadQ1A2" style="display: none;">
                        <label for="imageAudioUploadQ1A2">File:</label>
                        <input type="file" id="imageAudioUploadQ1A2" name="mediaFileQ1A2" accept="audio/*,image/*" />
                    </div>

                    <div id="videoUrlQ1A2" style="display: none;">
                        <label for="videoUrlQ1A2">YouTube Video URL:</label>
                        <input type="text" id="videoUrlQ1A2" name="videoUrlQ1A2">
                    </div>

                    <input type="text" name="answerTextQ1" placeholder="Answer 2" required>
                    <input type="radio" name="correctAnswerQ1" value="2"> Correct
                </div>
                <!-- Add more answers dynamically if needed -->
                <button class="addAnotherAnswerBtn" type="button" onclick="addAnswer()">Add Another Answer</button>

            </div>
            <div id="questionsContainer"></div>

            <input type="hidden" name="quizName" value="<%= request.getAttribute(" quizName") %>">
            <button class="addQuestionBtn" type="submit">Add Question</button>
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