<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Questions</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>
        .wrap {
            padding: 60px 0;
            /* justify-content: unset; */
            overflow-y: scroll;
            -ms-overflow-style: none;
            /* Internet Explorer 10+ */
            scrollbar-width: none;
            /* Firefox */
            -webkit-scrollbar: none;
        }

        .errorHome,
        header button {
            padding: 10px 30px;
            border-radius: 10px;
            border: 0;
            margin-right: 10px;
            font-size: 16px;
            transition-duration: 0.3s;
            cursor: pointer;
        }

        header button:hover {
            transform: scale(1.03);
        }

        .restartBtn {
            background-color: #FF4B3E;
            color: #DCEED1;
        }

        .title {
            text-align: center;
            font-size: 40px;
            margin-bottom: 0;
        }

        .questions {
            width: 80%;
            transform: scale(0.9);
        }

        .question>p {
            display: flex;
            justify-content: center;
            align-items: center;
            font-size: 23px;
        }

        .answersOption {
            width: 100%;
            margin-top: 30px;
            display: flex;
            /* flex-direction: column; */
            flex-wrap: wrap;
            justify-content: center;
            align-items: center;
            gap: 20px;
        }

        .answersOption button {
            all: unset;
            width: 45%;
            padding: 20px 40px;
            box-sizing: border-box;
            border-radius: 10px;
            font-size: 20px;
            text-align: center;
            cursor: pointer;
            display: block;
            transition-duration: 0.3s;
        }

        /* Add this CSS for expanding the selected answer */
        .correct-expand {
            position: relative;
            z-index: 10;
            width: 100% !important;
            height: 100px;
            /* Adjust this value as needed */
            transition: all 0.5s ease;
            /* Smooth transition for the animation */
        }

        #questionForm {
            width: 45%;
        }

        #questionForm button {
            width: 100%;
        }

        .answer1 {
            background-color: #d00000 !important;
        }

        .answer2 {
            background-color: #FF4B3E !important;
        }

        .answer3 {
            background-color: #FFB20F !important;
        }

        .answer4 {
            background-color: #99c252 !important;
        }

        .answersOption button:hover {
            transform: scale(1.03);
        }

        /* Displaying media */
        .imgWrap {
            width: 100%;
            height: 400px;
            margin-top: 20px;
            overflow: hidden;
        }

        img {
            width: 100%;
            height: 100%;
            object-fit: contain;
        }

        .audioWrap {
            width: 100%;
            margin-top: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        audio {
            transform: scale(1.3);
            margin: 20px 0;
        }

        .videoWrap {
            width: 100%;
            height: 100%;
            margin-top: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        /* Error Message */

        .errorMsg {
            padding: 30px;
            font-size: 25px;
        }

        .errorHome {
            margin: 0 auto;
            padding: 20px 50px;
            font-size: 25px;
            border-radius: 15px;
            background-color: #99c252;
        }

        .errorHome:hover {
            transform: scale(1.05);
            box-shadow: 5px 5px 5px rgba(1, 1, 1, 0.3);
        }

        .errorBtnWrap,
        .errorMsg {
            display: flex;
            justify-content: center;
            align-items: center;
        }
    </style>
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
        <form method="get" action="updateAutoplay">
            <input name="enabled" type="hidden" id="autoplay" value="<%= session.getAttribute(" autoplay") !=null &&
                (Boolean)session.getAttribute("autoplay") ? "true" : "false" %>">
            <button id="autoplayToggle">Autoplay: OFF</button>
        </form>
    </header>

    <div class="wrap">
        <div class="title cherry-cream-soda">
            Question <%=request.getAttribute("qNumber")%> / <%=request.getAttribute("quizSize")%>
        </div>
        <div class="questions">
            <%=request.getAttribute("questionsHtml")%>
        </div>
        <textarea id="echoText" rows="5" cols="30"></textarea>
        <div id="guestAnswersContainer"></div>

        <div id="answerCounts" style="margin-top: 20px;">
            <h3>Answer Counts:</h3>
            <!-- Count data will be dynamically added here -->
        </div>
        <div id="timer" style="font-size: 20px; text-align: center; margin-top: 20px; display:none;">Time left: 60
            seconds</div>
    </div>
    <form id="questionForm" method="post" style="display: hidden;"></form>
</body>
<script type="text/javascript">
let socket = new WebSocket("ws://localhost:8081/project1/questions");

// Event handlers
socket.onopen = function (event) {
    console.log("WebSocket is open now.");
};

socket.onmessage = function (event) {
    console.log("Received message: " + event.data);
};

socket.onclose = function (event) {
    console.log("WebSocket is closed now.");
};

socket.onerror = function (error) {
    console.log("WebSocket error: " + error);
};

// Sending a message
function sendMessage() {
    let message = document.getElementById("messageInput").value;
    socket.send(message);
}



//     let answerCounts = {};
//     let webSocket = new WebSocket("ws://localhost:8081/examples/websocket/chat");
//     let echoText = document.getElementById("echoText");
//     echoText.value = "";
//     let message1 = document.querySelectorAll(".answersOption");
//     let guestAnswersContainer = document.getElementById("guestAnswersContainer");

//     let currentQuestion = document.querySelector(".questionText").innerHTML;
//     let currentAnswers = [];
//     document.querySelectorAll('[class*="PlayAnswer"]').forEach((answer) => {
//        currentAnswers.push(answer.textContent);
//     });
//     let data ={
//         question: currentQuestion,
//         answers: currentAnswers
//     }


//     webSocket.onopen = function (message) {
//         wsOpen(message);
//     }
//     webSocket.onmessage = function (message) {
//         let clientData = JSON.parse(message);
//         if(clientData.type === "answer") {
//             let totals = clientData.totals;
//             webSocket.clients.forEach(client => {
//                 client.send(JSON.stringify({type: "totals", totals: totals}));
//             });
//         }
//     }

//     webSocket.onclose = function (message) {
//         wsClose(message);
//     }

//     webSocket.onerror = function (message) {
//         wsError(message);
//     }

//     function wsOpen(message) {
//         echoText.value += "Connected ... \n";
//     }
//     function wsSendMessage(message) {
//         webSocket.send(JSON.stringify({type: "question", question: data.question, answers: data.answers}));
//         // updateAnswerCount(message);
//         // webSocket.send( );

//         // // Update counts for selected answers
//         // displayAnswerCounts();
//     }
//     function wsCloseConnection() {
//         webSocket.close();
//     }
//     function wsGetMessage(message) {
//         echoText.value += "Message received from the server: " + message.data + "\n";
//         let guestAnswer = document.createElement("div");
//         guestAnswer.textContent = message.data;
//         guestAnswersContainer.appendChild(guestAnswer);

//     }
// webSocket.onmessage = (event) => {
//         const message = JSON.parse(event.data);
//         if (message.type === "totals") {
//             answerCounts = message.totals;
//             displayAnswerCounts();
//         }
//         if (message.type === "question") {
//             const question = message.question;
//             const answers = message.answers;
//             displayQuestion(question, answers);
//         }

//     };

//     function displayQuestion(question, answers) {
//         const questionElement = document.createElement("div");
//         questionElement.textContent = question;
//         guestAnswersContainer.appendChild(questionElement);

//         answers.forEach((answer) => {
//             const answerElement = document.createElement("div");
//             answerElement.textContent = answer;
//             guestAnswersContainer.appendChild(answerElement);
//         });
//     }

//     function displayAnswerCounts() {
//         const answerCountsElement = document.getElementById("answerCounts");
//         answerCountsElement.innerHTML = "<h3>Answer Counts:??</h3>";

//         for (const answer in answerCounts) {
//             const answerCountElement = document.createElement("div");
//             answerCountElement.textContent = `${answer}: ${answerCounts[answer]}`;
//             answerCountsElement.appendChild(answerCountElement);
//         }
//     }


//     function wsClose(message) {
//         echoText.value += "Disconnect ... \n";
//     }

//     function wsError(message) {
//         echoText.value += "Error ... \n";
//     }

//     window.addEventListener("beforeunload", () => {
//         wsCloseConnection();
//     });

//     document.querySelectorAll(".wrongPlayAnswer").forEach((answer) => {
//         answer.addEventListener("click", () => {
//             wsSendMessage(answer.textContent);
//         });

//     });
//     document.querySelectorAll(".correctPlayAnswer").forEach((answer) => {
//         answer.addEventListener("click", () => {
//             wsSendMessage(answer.textContent);
//         });

//     });














    let correctAnswer = document.getElementById('questionForm');
    //---------------WRONG BUTTON MECHANICS---------------\\
    let wrongButtons = document.getElementsByClassName("wrongPlayAnswer");
    // let container = document.getElementsByClassName("wrap")[0];
    for (let i = 0; i < wrongButtons.length; i++) {
        wrongButtons[i].addEventListener('click', () => {
            wrongButtons[i].classList.add('wrong');
            wrongButtons[i].style.boxShadow = "0px 0px 50px rgb(244, 0, 0)";
            setTimeout(() => {
                wrongButtons[i].classList.remove('wrong');
                wrongButtons[i].style.boxShadow = "";
            }, 1500);
        });
    }

    let correctAnswerButton = document.getElementById("rightPlayAnswer");
    correctAnswerButton.addEventListener('click', () => {
        correctAnswerButton.style.boxShadow = "0px 0px 50px rgb(0, 244, 0)";
        setTimeout(() => {
            correctAnswer.submit();
        }, 500);
    });



    //---------------AUTOPLAY---------------\\
    let autoplayEnabled = document.getElementById("autoplay").value === "true";
    let autoplayTimer;
    let countdownTime = 60;
    let timerDisplay = document.getElementById('timer');
    let timerInterval;

    const autoplayToggleButton = document.getElementById('autoplayToggle');
    const correctButton = document.querySelector('.answer[id="rightPlayAnswer"]');

    function updateAutoplayButton() {
        autoplayToggleButton.textContent = autoplayEnabled ? "Autoplay: ON" : "Autoplay: OFF";
    }
    // Set up event listener for the autoplay toggle button
    // NO MORE CONSOLE.LOGS
    autoplayToggleButton.addEventListener('click', () => {
        event.preventDefault();
        autoplayEnabled = !autoplayEnabled;

        if (autoplayEnabled) {
            //autoplayToggleButton.textContent = "Autoplay: ON";
            timerDisplay.style.display = 'block';
            startAutoplay(correctButton);
            fetch('updateAutoplay?enabled=true');
        } else {
            //autoplayToggleButton.textContent = "Autoplay: OFF";
            clearTimeout(autoplayTimer);
            clearInterval(timerInterval);
            timerDisplay.style.display = 'none';
            timerDisplay.textContent = "Time left: 60 seconds";
            fetch('updateAutoplay?enabled=false');
        }

        updateAutoplayButton();
    });

    // Start autoplay if enabled on page load
    if (autoplayEnabled) {
        updateAutoplayButton();
        timerDisplay.style.display = 'block';
        startAutoplay(correctButton);
    }


    function startAutoplay(correctButton) {
        let timeLeft = countdownTime;
        updateTimerDisplay(timeLeft);

        clearInterval(timerInterval);

        timerInterval = setInterval(() => {
            timeLeft--;
            updateTimerDisplay(timeLeft);

            if (timeLeft <= 0) {
                clearInterval(timerInterval);
                correctAnswerButton.style.boxShadow = "0px 0px 50px rgb(0, 244, 0)";
                setTimeout(() => {
                    correctAnswer.submit();
                }, 500);
                // if (correctButton) {
                //     correctButton.click();
                // }
            }
        }, 1000);

        autoplayTimer = setTimeout(() => {
            correctAnswerButton.style.boxShadow = "0px 0px 50px rgb(0, 244, 0)";
            setTimeout(() => {
                correctAnswer.submit();
            }, 500);
            // if (correctButton) {
            //     correctButton.click();
            // }
        }, countdownTime * 1000);
    }

    function updateTimerDisplay(timeLeft) {
        timerDisplay.textContent = "Time left: " + timeLeft + " seconds";
    }


    //---------------VIDEO PLAYING---------------\\

    // Load the IFrame Player API code asynchronously
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/iframe_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

    // Declare the player variable
    var player;

    // Setting player attributes
    var Id = document.getElementById("videoId").value;
    var startTime = parseInt(document.getElementById("videoStart").value);
    var endTime = parseInt(document.getElementById("videoEnd").value);

    // Create the YouTube player after the API downloads
    function onYouTubeIframeAPIReady() {
        player = new YT.Player('player', {
            videoId: Id,
            playerVars: {
                'playsinline': 1,
                'start': startTime,
                'end': endTime
            },
            events: {
                'onReady': onPlayerReady,
                'onStateChange': onPlayerStateChange
            }
        });
    }

    // Play the video once it's ready and start at the 5-second mark
    function onPlayerReady(event) {
        event.target.seekTo(startTime);
        event.target.playVideo();
    }

    // Monitor the video state and loop it between 5 and 6 seconds
    function onPlayerStateChange(event) {
        if (event.data == YT.PlayerState.PLAYING) {
            var checkTime = setInterval(function () {
                var currentTime = player.getCurrentTime();
                if (currentTime >= endTime) {
                    player.seekTo(startTime);
                }
            }, 100);
        }
    }
    //---------------AUDIO PLAYING---------------\\
    //makes audio loop
    function audio() {
        if (document.querySelector("audio").currentTime >= parseInt(document.getElementById("videoEnd").value)) {
            document.querySelector("audio").currentTime = parseInt(document.getElementById("videoStart").value);
        }
    }

</script>

</html>