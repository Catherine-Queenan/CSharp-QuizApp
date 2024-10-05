<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Add a New Question</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

        .hidden {
            display: none !important;
        }

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
        }

        .newQuestionForm {
            width: 55%;
            margin-top: 50px;
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

        #answersContainer {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .addAnswerBtn,
        .addQuestionBtn {
            all: unset;
            /* margin-top: 20px; */
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

        .addAnswerBtn {
            width: fit-content;
            display: inline-block;
            background-color: #99c252;
            color: #0C1B33;
        }

        .addAnswerBtn:hover,
        .addQuestionBtn:hover {
            box-shadow: inset 5px 5px 5px rgba(1, 1, 1, 0.5);
        }
        
        :focus {
            outline: none;
        }

        .mediaType {
            margin-bottom: 20px;
        }

        #audioStartEnd,
        #videoUrlQuestion {
            display: flex;
            flex-direction: column;
            gap: 20px;
            margin-bottom: 20px;
        }

        #imageAudioUploadQuestion {
            flex-direction: column;
        }

    </style>
    <script>

        let answerCount = 2;
        function addAnswer() {
            console.log("added")
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
                <jsp:include page="answerMediaUpload.jsp"/>
            `;
            document.getElementById('answersContainer').appendChild(answerDiv);
        }

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

        <form class="newQuestionForm" method="post" enctype="multipart/form-data">
            <input type="hidden" name="quizName" id="quizName" value="<%= request.getAttribute(" quizName") %>">
            <label for="questionText">Question Text:</label>
            <input type="text" id="questionTextQ1" name="questionText" required>

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
                    <div>
                        <label for="audioStart">Audio Start (seconds):</label>
                        <input type="number" id="audioStart" name="audioStart" value="0"/>
                    </div>
                    <div>
                        <label for="audioEnd">Audio End (seconds):</label>
                        <input type="number" id="audioEnd" name="audioEnd" value="0"/>
                    </div>
                </div>
            </div>

            <div id="videoUrlQuestion" style="display: none;">
                <div>
                    <label for="videoUrl">YouTube Video URL:</label>
                    <input type="text" id="videoUrl" name="videoUrl">
                </div>
                <div>
                    <label for="videoStart">Clip Start (seconds):</label>
                    <input type="number" id="videoStart" name="videoStart" value="0"/>
                </div>
                <div>
                    <label for="videoEnd">Clip End (seconds):</label>
                    <input type="number" id="videoEnd" name="videoEnd" value="0"/>
                </div>
            </div>

            <div id="answersContainer">
                <label for="answerType">Answer Media Type:</label>
                <select class="mediaType" id="answerType" name="answerType">
                    <option value="TEXT">None</option>
                    <option value="VID">Video</option>
                    <option value="IMG">Image</option>
                    <option value="AUD">Audio</option>
                    <!-- Add other question types as needed -->
                </select>

                <div id="videoUrlAnswer" style="display: none;">
                    <div>
                        <label for="videoUrl">YouTube Video URL:</label>
                        <input type="text" class="videoUrlAnswer" name="videoUrl">
                    </div>
                    <div>
                        <label for="videoStart">Clip Start (seconds):</label>
                        <input type="number" class="videoStartAnswer" name="videoStart" value="0"/>
                    </div>
                    <div>
                        <label for="videoEnd">Clip End (seconds):</label>
                        <input type="number" class="videoEndAnswer" name="videoEnd" value="0"/>
                    </div>
                </div>

                <div id="audioUploadAnswer" style="display: none;">
                    <label for="mediaFile">File:</label>
                    <input type="file"  class="mediaFileAnswer" name="mediaFile" accept="audio/*,image/*" />
                    <div class="audioStartEndAnswer">
                        <div>
                            <label for="audioStart">Audio Start (seconds):</label>
                            <input type="number" class="audioStartAnswer" name="audioStart" value="0"/>
                        </div>
                        <div>
                            <label for="audioEnd">Audio End (seconds):</label>
                            <input type="number" class="audioEndAnswer" name="audioEnd" value="0"/>
                        </div>
                    </div>
                </div>

                <div class="answer">
                    <input type="text" name="answerText" placeholder="Answer 1" required>
                    <input type="radio" name="correctAnswer" value="1" checked="checked"> Correct
                    <jsp:include page="answerMediaUpload.jsp"/>
                </div>
                <div class="answer">
                    <input type="text" name="answerText" placeholder="Answer 2" required>
                    <input type="radio" name="correctAnswer" value="2"> Correct
                    <jsp:include page="answerMediaUpload.jsp"/>
                </div>
                <!-- <button class="addAnotherAnswerBtn" type="button" onclick="addAnswer()">Add Another Answer</button> -->
            </div>

            <!-- Add more answers dynamically if needed -->
            <button class="addAnswerBtn" id="addAnswerBtn" type="button" onclick="addAnswer()">Add Another Answer</button>
            <div id="questionsContainer"></div>

            
            <button class="addQuestionBtn" type="submit">Add Question</button>
        </form>
    </div>

    <script>
        console.log(document.getElementById("quizName").value);
        let questionMedia = document.getElementById('questionType');
        questionMedia.addEventListener('change', function () {
                if (questionMedia.value === 'IMG' || questionMedia.value === 'AUD') {
                    document.getElementById(`imageAudioUploadQuestion`).style.display = 'flex';
                    document.getElementById(`videoUrlQuestion`).style.display = 'none';
                    if(questionMedia.value === 'AUD'){
                        document.getElementById("audioStartEnd").style.display = 'flex';
                    }
                } else if (questionMedia.value === 'VID') {
                    document.getElementById(`imageAudioUploadQuestion`).style.display = 'none';
                    document.getElementById(`videoUrlQuestion`).style.display = 'flex';
                    document.getElementById("audioStartEnd").style.display = 'none';
                } else {
                    document.getElementById(`imageAudioUploadQuestion`).style.display = 'none';
                    document.getElementById(`videoUrlQuestion`).style.display = 'none';
                    document.getElementById("audioStartEnd").style.display = 'none';
                }
            });

        let answerMedia = document.getElementById('answerType');
        let answerImgUploads = document.getElementsByClassName(`imageUploadAnswer`);
        let answerAudUpload = document.getElementById(`audioUploadAnswer`);
        let answerVidUpload = document.getElementById(`videoUrlAnswer`);

        let displayAnswerMedia = function () {
            console.log("AAAAAAAAAA");
            if (answerMedia.value === 'IMG') {
                for(let i = 0; i < answerImgUploads.length; i++){
                    answerImgUploads[i].style.display = 'flex';
                }

                answerVidUpload.style.display = 'none';
                answerAudUpload.style.display = 'none';

            } else if (answerMedia.value === 'AUD') {
                for(let i = 0; i < answerImgUploads.length; i++){
                    answerImgUploads[i].style.display = 'none';
                }

                answerVidUpload.style.display = 'none';
                answerAudUpload.style.display = 'flex';

            } else if (answerMedia.value === 'VID') {
                for(let i = 0; i < answerImgUploads.length; i++){
                    answerImgUploads[i].style.display = 'none';
                }

                answerVidUpload.style.display = 'flex';
                answerAudUpload.style.display = 'none';
                
            } else {
                for(let i = 0; i < answerImgUploads.length; i++){
                    answerImgUploads[i].style.display = 'none';
                }
                
                answerVidUpload.style.display = 'none';
                answerAudUpload.style.display = 'none';
                
            }
        }

        answerMedia.addEventListener('change', displayAnswerMedia);
        document.getElementById("addAnswerBtn").addEventListener('click', displayAnswerMedia);

    </script>
</body>
</html>