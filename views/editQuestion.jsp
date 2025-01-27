<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Question</title>
    <link rel="stylesheet" href="../../public/css/reset.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>

        .hidden {
            display: none !important;
        }

        #wrap {
            padding: 80px 0;
            justify-content: unset;
            overflow-y: scroll;
            -ms-overflow-style: none;
            /* Internet Explorer 10+ */
            scrollbar-width: none;
            /* Firefox */
            -webkit-scrollbar: none;
        }

        /* Responsive */
        @media screen and (max-width: 800px) {
            .wrap {
                padding: 0;
            }
        }

        @media screen and (max-width: 500px) {
            #wrap {
                padding: 50px 0;
            }
        }

        .newQuestionForm {
            width: 55%;
            margin-top: 20px;
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
            max-width: 100%;
            border: 0;
            border-radius: 10px;
            padding: 15px 20px !important;
            font-size: 18px;
        }

        #questionForm {
            display: flex;
            flex-direction: column;
            gap: 10px;
        }

        .questionImgDisplayWrap {
            display: flex;
            justify-content: center;
            align-items: center;
            overflow: hidden;
        }

        .questionImgDisplayWrap img {
            display: flex;
            justify-content: center;
            align-items: center;
            border-radius: 15px;
            max-width: 500px;
            width: 100%;
            max-height: 500px;
            height: 100%;
            object-fit: cover;
        }

        #answersContainer {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .addAnswerBtn,
        .saveBtn {
            all: unset;
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
            text-align: center;
            width: fit-content;
            display: inline-block;
            background-color: #99c252;
            color: #0C1B33;
        }

        .addAnswerBtn:hover,
        .saveBtn:hover {
            box-shadow: inset 5px 5px 5px rgba(1, 1, 1, 0.5);
        }

        #answersContainer {
            margin-bottom: 20px;
            margin-top: 20px;
        }

        .answer {
            width: 100%;
            padding: 15px 20px !important;
            border-radius: 10px;
            background-color: rgb(244, 244, 244);
            display: flex;
            justify-content: space-between;
            align-items: center;
            gap: 10px;
        }

        .answerImgWrap {
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .answerImgWrap img {
            border-radius: 15px;
            max-width: 500px;
            width: 100%;
            max-height: 500px;
            height: 100%;
            object-fit: cover;
        }

        .addNewAnswer {
            all: unset;
            padding: 0 !important;
            display: flex;
            flex-wrap: wrap;
            align-items: center;
            gap: 10px;
        }

        #newAnswerForm button {
            all: unset;
            padding: 15px 20px;
            border-radius: 10px;
            margin-top: 10px;
            text-align: center;
            width: fit-content;
            display: inline-block;
            background-color: #99c252;
            color: #0C1B33;
        }

        .answer p {
            color: #0C1B33;
        }

        .answer .deleteAnswerBtn {
            all: unset;
            color: #D00000;
            cursor: pointer;
            transition-duration: 0.3s;
        }

        .answer .deleteAnswerBtn:hover {
            scale: 1.2;
        }

        .answer input {
            max-width: 100%;
        }

        .answer input:focus {
            outline: none;
        }

        /* Question media */
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

        .videoUrlAnswer {
            display: block;
            width: 100%;
            color: #FFE548;
            /* Preventing URL to overflow */
            white-space: pre-wrap; /* CSS3 */    
            white-space: -moz-pre-wrap; /* Mozilla, since 1999 */
            white-space: -pre-wrap; /* Opera 4-6 */    
            white-space: -o-pre-wrap; /* Opera 7 */    
            word-wrap: break-word; /* Internet Explorer 5.5+ */
        }

        /* Answer media */
        #videoUrlAnswer,
        .audioStartEndAnswer {
            display: flex;
            flex-direction: column;
            gap: 20px;
            margin-bottom: 20px;
        }

        #imageAudioUploadQuestion,
        #audioUploadAnswer {
            display: flex;
            flex-direction: column;
        }

        #answerAudio {
            margin-bottom: 15px;
        }

        #answerAudioFileName, 
        #audioFileName {
            color: #FFE548;
            margin-bottom: 10px;
        }

        .audioStartEndAnswer {
            margin-top: 10px;
        }

    </style>
    <script>
        let answerCount = 0;
        function addAnswer() {
            console.log("added")
            document.getElementById('addAnswerBtn').classList.add('hidden');
            
            answerCount++;
            const newAnswerForm = document.createElement('div');
            newAnswerForm.classList.add(`answer`);
            newAnswerForm.classList.add(`addNewAnswer`);
            newAnswerForm.method = `post`;
            newAnswerForm.enctype = "multipart/form-data";
            newAnswerForm.innerHTML = `
                <input type="text" name="answerText" placeholder="Answer ${answerCount}" required>
                <div>
                    <input type="checkbox" name="correctAnswer" value="1"> Correct
                </div>
                <jsp:include page="/views/answerMediaUpload.jsp"/>
            `;
            document.getElementById('newAnswerForm').appendChild(newAnswerForm);

            const submitBtn = document.createElement('button');
            submitBtn.type = "submit";
            submitBtn.innerHTML = "Save New Answer";
            document.getElementById('newAnswerForm').appendChild(submitBtn);
        }

    </script>
</head>
<body>
    <header>
        <form id="homeForm" action="home">
            <button class="homeBtn" type="Submit">Home</button>
        </form>
        <form action="logout">
            <button id="logoutButton" class="logoutBtn" type="Submit">Log Out</button>
        </form>
    </header>

    <div class="wrap" id="wrap">
        <div class="title cherry-cream-soda">
            <!-- Add Question to <%= request.getAttribute("quizName") %> -->
            Edit Question
        </div>

        <div class="newQuestionForm">
            <form id="questionForm" class="editForm" method="post" enctype="multipart/form-data">
                <label for="questionText">Question Text:</label>
                <input type="text" id="questionText" name="questionText" required>

                
                <label for="questionType">Question Media Type:</label>
                <select class="mediaType" id="questionType" name="questionType">
                    <option value="TEXT">None</option>
                    <option value="VID">Video</option>
                    <option value="IMG">Image</option>
                    <option value="AUD">Audio</option>
                    <!-- Add other question types as needed -->
                </select>
                <input type="hidden" name="oldMediaFilePath" id="oldMediaFilePath">
                
                <div class="questionImgDisplayWrap">
                    <img id="questionImgDisplay" style="display:none;">
                </div>
                <audio id="questionAudio" style="display:none" preload controls ontimeupdate="Audio()">
                    <source id="questionAudioSrc" type="audio/mp3">
                </audio>

                <div id="imageAudioUploadQuestion" style="display: none;">
                    <div id="audioFileName"></div>
                    <div>
                        <label for="mediaFile">File:</label>
                        <input type="file" id="mediaFile" name="mediaFile" accept="audio/*,image/*" />
                    </div>
                    <div id="audioStartEnd" style="display: none;">
                        <div>
                            <label for="audioStart">Audio Start (seconds):</label>
                            <input type="number" id="audioStart" name="audioStart" value="0" />
                        </div>
                        <div>
                            <label for="audioEnd">Audio End (seconds):</label>
                            <input type="number" id="audioEnd" name="audioEnd" value="0" />
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
                        <input type="number" id="videoStart" name="videoStart" value="0" />
                    </div>
                    <div>
                        <label for="videoEnd">Clip End (seconds):</label>
                        <input type="number" id="videoEnd" name="videoEnd" value="0" />
                    </div>
                </div>

                <div id="answerForm" class="editForm">
    
                    <div id="answersContainer">
                        <p>Answer Media Type: <span id="answerType"></span></p>
                        <div id="videoUrlAnswer" style="display: none;">
                            <div>
                                <a class="videoUrlAnswer" id="videoUrlAnswerIn"></a>
                            </div>
                            <div>
                                <p class="videoStartAnswer" id="videoStartAnswer"></p>
                            </div>
                            <div>
                                <p class="videoEndAnswer" id="videoEndAnswer"></p>
                            </div>
                        </div>
    
                        <div id="audioUploadAnswer" style="display: none;">
                            <!-- <audio id="answerAudio" preload controls>
                                <source id="answerAudioSrc" type="audio/mp3">
                            </audio> -->
    
                            <div id="answerAudioFileName"></div>
                            <div class="audioStartEndAnswer">
                                <div>
                                    <p id="audioStartAnswer" class="audioStartAnswer">Audio Start:</p>
                                </div>
                                <div>
                                    <p id="audioEndAnswer" class="audioEndAnswer">Audio End:</p>
                                </div>
                            </div>
                        </div>

                    </div>        
    
                    <!-- Add more answers dynamically if needed -->
                    <button class="addAnswerBtn" id="addAnswerBtn" type="button">Add Another Answer</button>
                    <div id="questionsContainer"></div>
                </div>
            </form>
            
            <form id="newAnswerForm" method="post" enctype="multipart/form-data"></form>
            
            <!-- Save button -->
            <button form="questionForm" class="saveBtn" type="submit">Save</button>

        </div>
    </div>
    <script src="..\..\scripts\logout.js"></script>
    <script>

        let answerMedia = document.getElementById('answerType');
        let answerImgUploads = document.getElementsByClassName(`imageUploadAnswer`);
        let answerAudUpload = document.getElementById(`audioUploadAnswer`);
        let answerVidUpload = document.getElementById(`videoUrlAnswer`);

        let displayAnswerMedia = function (type) {
            if (type === 'IMG') {
                for (let i = 0; i < answerImgUploads.length; i++) {
                    answerImgUploads[i].style.display = 'flex';
                }

                answerVidUpload.style.display = 'none';
                answerAudUpload.style.display = 'none';

            } else if (type === 'AUD') {
                for (let i = 0; i < answerImgUploads.length; i++) {
                    answerImgUploads[i].style.display = 'none';
                }

                answerVidUpload.style.display = 'none';
                answerAudUpload.style.display = 'flex';

            } else if (type === 'VID') {
                for (let i = 0; i < answerImgUploads.length; i++) {
                    answerImgUploads[i].style.display = 'none';
                }

                answerVidUpload.style.display = 'flex';
                answerAudUpload.style.display = 'none';

            } else {
                for (let i = 0; i < answerImgUploads.length; i++) {
                    answerImgUploads[i].style.display = 'none';
                }

                answerVidUpload.style.display = 'none';
                answerAudUpload.style.display = 'none';

            }
        }

        let translateMediaType = function (type) {
            switch (type) {
                case "AUD":
                    return "Audio";
                case "VID":
                    return "Video";
                case "IMG":
                    return "Images";
                default:
                    return "Text"
            }
        }

        function controlAnswerDeletes() {
            let buttons = document.querySelectorAll(".deleteAnswerBtn");
            console.log(buttons);
            if (buttons.length < 3) {
                buttons.forEach(button => {
                    button.disabled = true;
                    button.style.opacity = 0.5;
                });
            }
        }

        document.addEventListener('DOMContentLoaded', function () {
            const questionEditForm = document.getElementById('questionForm');
            const answerEditForm = document.getElementById('answerForm');

            const currentPath = window.location.pathname;
            const pathSegments = currentPath.split('/');

            // Extract the category name from URL parameters
            const questionNum = parseInt(pathSegments[4]);
            const quizName = pathSegments[3];

            // Extract the base path dynamically (remove last segment if it's quiz-related)
            pathSegments.pop();
            pathSegments.pop();
            pathSegments.pop();

            // Construct the new path dynamically
            const getFetchPath = pathSegments.join('/') + `/editQuestion-json/?id=${questionNum}`;
            const updateQuestionListPath = pathSegments.join('/') + `/editQuestions/${quizName}`;
            const postPath = pathSegments.join('/') + `/editQuestion-json`;
            const editAnswerPath = pathSegments.join('/') + `/editAnswer/`;

            const homePath = pathSegments.join('/') + `/home`;
            document.getElementById("homeForm").action = homePath;

            var answerType = "TEXT";

            fetch(getFetchPath, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            }).then(response => {
                if (!response.ok) {
                    console.error('Response status:', response.status);
                    throw new Error('Failed to fetch quiz');
                }
                return response.json(); // Change this temporarily to text() instead of json()
            }).then(data => {
                console.log(data)
                document.getElementById('questionText').value = data.question.question_text;
                document.getElementById('questionType').value = data.question.question_type;

                let questionImgDisplay = document.getElementById('questionImgDisplay');
                let questionAudio = document.getElementById('questionAudio');

                if (data.question.media != null) {
                    let mediaFilePath = data.question.media.media_file_path;
                    let mediaStart = data.question.media.media_start;
                    let mediaEnd = data.question.media.media_end;

                    console.log(mediaStart);
                    console.log(mediaEnd);
                    document.getElementById('oldMediaFilePath').value = "../../" + mediaFilePath;

                    switch (data.question.question_type) {
                        case "AUD":
                            questionImgDisplay.style.display = "none";
                            let tempFilePath = mediaFilePath;
                            const tempFileName = tempFilePath.split('/').pop();

                            document.getElementById('audioFileName').innerHTML = "Current audio file name: " + tempFileName;
                            document.getElementById('audioStart').inner = mediaStart;
                            document.getElementById('audioEnd').value = mediaEnd;

                            document.getElementById(`imageAudioUploadQuestion`).style.display = 'flex';
                            document.getElementById(`videoUrlQuestion`).style.display = 'none';
                            if (questionMedia.value === 'AUD') {
                                document.getElementById("audioStartEnd").style.display = 'flex';
                            }
                            break;
                        case "VID":
                            questionImgDisplay.style.display = "none";
                            questionAudio.style.display = "none";

                            document.getElementById('videoUrl').value = mediaFilePath;
                            document.getElementById('videoStart').value = mediaStart;
                            document.getElementById('videoEnd').value = mediaEnd;

                            document.getElementById(`imageAudioUploadQuestion`).style.display = 'none';
                            document.getElementById(`videoUrlQuestion`).style.display = 'flex';
                            document.getElementById("audioStartEnd").style.display = 'none';
                            break;
                        case "IMG":
                            questionImgDisplay.style.display = "block";
                            questionAudio.style.display = "none";

                            questionImgDisplay.src = "../../" + mediaFilePath;
                            break;
                        default:
                            questionImgDisplay.style.display = "none";
                            questionAudio.style.display = "none";
                    }
                }


                //Handle questions
                let answers = data.answers;
                answers.forEach(answer => {
                    console.log("ANSWER COUNT:" + answerCount);
                    if (answerCount == 3) {
                        document.getElementById('addAnswerBtn').classList.add('hidden');
                    }

                    answerCount++;

                    const answerDiv = document.createElement('div');
                    answerDiv.classList.add(`answer`);
                    answerDiv.id = `answer`;
                    let answerText = document.createElement('p');
                    answerText.innerHTML = answer.answer_text + ((answer.is_correct == 1) ? " (CORRECT)" : "");
                    answerDiv.appendChild(answerText);

                    let answerBool = document.createElement('input');
                    answerBool.type = "radio";
                    answerBool.name = "correctAnswer";
                    answerBool.value = answerCount;

                     let deleteBtn = document.createElement("button");
                    deleteBtn.type = "button";
                    deleteBtn.innerHTML = `<i class="fa-solid fa-trash"></i>`;
                    deleteBtn.classList.add("deleteAnswerBtn");

                    if (answer.is_correct == 1) {
                        answerBool.checked = true;
                        deleteBtn.addEventListener("click", () => {
                            alert("Create a new correct answer to delete this one!");
                        });
                    } else {
                        deleteBtn.addEventListener("click", () => {
                        fetch(editAnswerPath + "?id=" + answer.answer_num, {
                            method: "DELETE",
                            headers: {
                                "Accept": "application/json" // Expect a JSON response
                            }
                        }).then(response => {
                            return response.json().then(data => {
                                if (response.ok) {
                                    window.location.reload();

                                } else {
                                    throw new Error(data.message || 'An error occurred');
                                }
                            })
                        }).catch(error => {
                            // Handle any errors that occurred during the fetch
                            console.error("Error:", error.message);
                            alert("An error occurred: " + error.message);
                        });
                        })
                    }
                    answerDiv.appendChild(deleteBtn);
                    

                    let answerImage;
                    if (answer.answer_type == "IMG") {
                        let answerImg = document.createElement('img');
                        answerImg.src = "../../" + answer.media.media_file_path;

                        let ansewrImgWrap = document.createElement('div');
                        ansewrImgWrap.className = "answerImgWrap";
                        ansewrImgWrap.appendChild(answerImg);
                        answerImage = ansewrImgWrap;

                    } else if (answer.is_correct == 1) {
                        switch (answer.answer_type) {
                            case "AUD":
                                let tempFilePath = answer.media.media_file_path;
                                const tempFileName = tempFilePath.split('/').pop();
                                document.getElementById('answerAudioFileName').innerHTML = "Current audio file name: " + tempFileName;

                                // document.getElementById("answerAudioSrc").src = "../../" + answer.media.media_file_path + "#t=" + answer.media.media_start;
                                document.getElementById('audioStartAnswer').innerHTML = "Audio Start (seconds): " + answer.media.media_start;
                                document.getElementById('audioEndAnswer').innerHTML = "Audio End (seconds): " + answer.media.media_end;
                                break;
                            case "VID":
                                document.getElementById('videoUrlAnswerIn').innerHTML = answer.media.media_file_path;
                                document.getElementById('videoUrlAnswerIn').href = answer.media.media_file_path;
                                document.getElementById("videoUrlAnswerIn").target = "_blank";

                                document.getElementById('videoStartAnswer').innerHTML = "Clip Start (seconds): " + answer.media.media_start;
                                document.getElementById('videoEndAnswer').innerHTML = "Clip End (seconds): " + answer.media.media_end;
                                break;
                        }
                    }

                   

                    

                    document.getElementById('answersContainer').appendChild(answerDiv);
                    if (answerImage) {
                        document.getElementById('answersContainer').appendChild(answerImage);
                    }
                    answerType = answer.answer_type;
                });


                document.getElementById('answerType').innerHTML = translateMediaType(answerType);

                displayAnswerMedia(answerType);
                controlAnswerDeletes();
            }).catch(error => {
                console.error('Error fetching categories:', error);
            });

            document.querySelectorAll('.editForm').forEach(form => {
                form.addEventListener("submit", function (event) {
                    event.preventDefault(); // Prevent the default form submission

                    const formData = new FormData(form);
                    formData.append("id", questionNum);
                    formData.append("quizName", quizName);

                    if (form.id == "questionForm") {
                        formData.append("edit", "question");
                    } else {
                        formData.append("edit", "answer");
                    }

                    fetch(postPath, { // Replace with your servlet URL
                        method: "POST",
                        body: formData,
                        headers: {
                            "Accept": "application/json" // Expect a JSON response
                        }
                    }).then(response => {
                        return response.json().then(data => {
                            if (response.ok) {
                                window.location.href = updateQuestionListPath;

                            } else {
                                throw new Error(data.message || 'An error occurred');
                            }
                        })
                    }).catch(error => {
                        // Handle any errors that occurred during the fetch
                        console.error("Error:", error.message);
                        alert("An error occurred: " + error.message);
                    });
                });

            });

            let questionMedia = document.getElementById('questionType');
            questionMedia.addEventListener('change', function () {
                if (questionMedia.value === 'IMG' || questionMedia.value === 'AUD') {
                    document.getElementById(`imageAudioUploadQuestion`).style.display = 'flex';
                    document.getElementById(`videoUrlQuestion`).style.display = 'none';
                    if (questionMedia.value === 'AUD') {
                        document.getElementById("audioStartEnd").style.display = 'flex';
                    } else {
                        document.getElementById("audioStartEnd").style.display = 'none';
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


            let answerForm = document.getElementById("newAnswerForm");
            console.log(answerForm);
            answerForm.addEventListener("submit", (event) => {
                event.preventDefault(); // Prevent the default form submission

                const formData = new FormData(answerForm);
                formData.append("answerType", answerType);
                for (var pair of formData.entries()) {
                    console.log(pair[0] + ', ' + pair[1]);
                }

                console.log(editAnswerPath);
                fetch(editAnswerPath, { // Replace with your servlet URL
                    method: "POST",
                    body: formData,
                    headers: {
                        "Accept": "application/json" // Expect a JSON response
                    }
                }).then(response => {
                    return response.json().then(data => {
                        if (response.ok) {
                            window.location.reload();
                        } else {
                            throw new Error(data.message || 'An error occurred');
                        }
                    })
                }).catch(error => {
                    // Handle any errors that occurred during the fetch
                    console.error("Error:", error.message);
                    alert("An error occurred: " + error.message);
                });
            });

            document.getElementById("addAnswerBtn").addEventListener("click", () => {
                addAnswer();
                displayAnswerMedia(answerType);
            });
        });

    </script>
</body>
</html>