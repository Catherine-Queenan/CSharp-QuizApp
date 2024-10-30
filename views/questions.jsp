<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Questions</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

        body {
            overflow-x: hidden;
        }
        
        .wrap {
            padding: 60px 0;
            overflow-y: scroll;
            -ms-overflow-style: none;  /* Internet Explorer 10+ */
            scrollbar-width: none;  /* Firefox */
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
            height: 100px; /* Adjust this value as needed */
            transition: all 0.5s ease; /* Smooth transition for the animation */
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
        .question>.imgWrap {
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

        /* Media display for the answers */
        .answersOption img {
            border-radius: 10px;
            max-width: 90%;
            width: 100%;
            max-height: 400px;
            height: 100%;
            object-fit: cover;
        }

        /* Timer */
        #timer {
            display: none;
            width: 700px;
            height: 20px;
            background-color: #DCEED1;
            border-radius: 1000px;
            overflow: hidden;
        }

        #timerCountdown {
            height: 100%;
            background-color: #d00000;
            transition-duration: 0.2s;
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
            <input name="enabled" type="hidden" id="autoplay" value="<%= session.getAttribute("autoplay") != null && (Boolean)session.getAttribute("autoplay") ? "true" : "false" %>">
            <button id="autoplayToggle">Autoplay: OFF</button>
        </form>
    </header>

    <div class="wrap" id="wrap">
        <div class="title cherry-cream-soda">
            Question <%=request.getAttribute("qNumber")%> / <%=request.getAttribute("quizSize")%>
        </div>
        <div class="questions">
            <%=request.getAttribute("questionsHtml")%>
        </div>
        <div id="timer" style="font-size: 20px; text-align: center; margin-top: 20px; display:none;">Time left: 60
            seconds</div>
    </div>
    <form id="questionForm" method="post" style="display: hidden;"></form>
</body>
<script>
    
    // Making all answer options have the same height value
    window.onload = function() {
        // Get all buttons inside the div with class "answersOption"
        var buttons = document.querySelectorAll('.answersOption button');
        
        var maxHeight = 0;
        
        // Loop through each button to determine the maximum height
        buttons.forEach(function(button) {
            var buttonHeight = button.offsetHeight;  // Get the height of the current button
            if (buttonHeight > maxHeight) {
                maxHeight = buttonHeight;  // Update the maxHeight if current button's height is greater
            }
        });
        
        // Set all buttons to the maximum height
        buttons.forEach(function(button) {
            button.style.height = maxHeight + "px";
        });


        // Changing the display depending on how long the content is
        if (document.querySelector(".questions").offsetHeight > 600) {
            document.getElementById("wrap").style.height = `fit-content`;
        } else {
            document.getElementById("wrap").style.height = `${100}vh`;
        }
    };

    let correctAnswer = document.getElementById('questionForm');
    // Declare the player variable
    var playerQuestion = null;
    var playerAnswer = null;

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
            }, 700);
        });
    }

    function submitCorrectAnswer() {
        setTimeout(() => {
            correctAnswer.submit();
        }, 500);
    }

    function nextQuestion() {
        if(answerMedia != null) {
            answerMedia.style.display = "flex";
            let audioAnswer = document.getElementById("audio-answer");
            let audioQuestion = document.getElementById("audio-question");
            
            if(audioQuestion != null){
                audioQuestion.pause();
            } else if(playerQuestion) {
                playerQuestion.stopVideo();
            }
            
            if(audioAnswer != null){
                audioAnswer.play();
            } else {
                playerAnswer.playVideo();
            }
        } else {
            submitCorrectAnswer()
        }
    }

    let correctAnswerButton = document.getElementById("rightPlayAnswer");
    let answerMedia = document.getElementById("mediaAnswer");
    correctAnswerButton.addEventListener('click', () => {
        correctAnswerButton.style.boxShadow = "0px 0px 50px rgb(0, 244, 0)";
        nextQuestion();
    });

    //---------------AUTOPLAY---------------\\
    let autoplayEnabled = document.getElementById("autoplay").value === "true";
    let autoplayTimer;
    let countdownTime = 30;
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
            autoplayToggleButton.textContent = "Autoplay: ON";
            timerDisplay.style.display = 'block';
            startAutoplay(correctButton);
            fetch('updateAutoplay?enabled=true');
        } else {
            autoplayToggleButton.textContent = "Autoplay: OFF";
            clearTimeout(autoplayTimer);
            clearInterval(timerInterval);
            timerDisplay.style.display = 'none';
            // timerDisplay.textContent = "Time left: 60 seconds";
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
                nextQuestion();
                // if (correctButton) {
                //     correctButton.click();
                // }
            }
        }, 1000);

        autoplayTimer = setTimeout(() => {
            correctAnswerButton.style.boxShadow = "0px 0px 50px rgb(0, 244, 0)";
            nextQuestion();
            // if (correctButton) {
            //     correctButton.click();
            // }
        }, countdownTime * 1000);
    }

    function updateTimerDisplay(timeLeft) {
        timerDisplay.innerHTML = `<div id="timerCountdown" style="width:${timeLeft / 30 * 100}%"></div>`;
        // console.log(timeLeft / 30 * 100);
    }

    
    //---------------VIDEO PLAYING---------------\\

    // Load the IFrame Player API code asynchronously
    var tag = document.createElement('script');
    tag.src = "https://www.youtube.com/iframe_api";
    var firstScriptTag = document.getElementsByTagName('script')[0];
    firstScriptTag.parentNode.insertBefore(tag, firstScriptTag);

    

    // Setting player attributes
    var IdQ = document.getElementById("videoId-question");
    var startTimeQ = parseInt(document.getElementById("videoStart-question") != null ? document.getElementById("videoStart-question").value : "0");
    var endTimeQ = parseInt(document.getElementById("videoEnd-question") != null ? document.getElementById("videoEnd-question").value : "0");

    var IdA = document.getElementById("videoId-answer");
    var startTimeA = parseInt(document.getElementById("videoStart-answer") != null ? document.getElementById("videoStart-answer").value : "0");
    var endTimeA = parseInt(document.getElementById("videoEnd-answer") != null ? document.getElementById("videoEnd-answer").value : "0");

    console.log(startTimeA)
    console.log(endTimeA)

    // Create the YouTube player after the API downloads
    function onYouTubeIframeAPIReady() {
        if(IdQ != null){
            playerQuestion = new YT.Player('player-question', {
                videoId: IdQ.value,
                playerVars: {
                    'playsinline': 1,
                    'start': startTimeQ,
                    'end': endTimeQ
                },
                events: {
                    'onReady': onPlayerQuestionReady,
                    'onStateChange': onPlayerQuestionStateChange
                }
            });
        }
        console.log(IdA);
        if(IdA != null) {
            playerAnswer = new YT.Player('player-answer', {
                videoId: IdA.value,
                playerVars: {
                    'playsinline': 1,
                    'start': startTimeA,
                    'end': endTimeA
                },
                events: {
                    'onStateChange': onPlayerAnswerStateChange
                }
            });
        }
        
    }

    // Play the video once it's ready and start at the 5-second mark
    function onPlayerQuestionReady(event) {
        event.target.seekTo(startTimeQ);
        event.target.playVideo();
    }

    // Monitor the video state and loop it between 5 and 6 seconds
    function onPlayerQuestionStateChange(event) {
        if (event.data == YT.PlayerState.PLAYING) {
            var checkTime = setInterval(function () {
                var currentTime = playerQuestion.getCurrentTime();
                if (currentTime >= endTimeQ) {
                    playerQuestion.seekTo(startTimeQ);
                }
            }, 100);
        }
    }

    function onPlayerAnswerStateChange(event) {
        if (event.data == YT.PlayerState.PLAYING) {
            var checkTime = setInterval(function () {
                var currentTime = playerAnswer.getCurrentTime();
                if (currentTime >= endTimeA) {
                    submitCorrectAnswer();
                }
            }, 100);
        }
    }

    //---------------AUDIO PLAYING---------------\\
    //makes audio loop
    function questionAudio() {
        if (document.getElementById("audio-question").currentTime >= endTimeQ) {
            document.getElementById("audio-question").currentTime = startTimeQ;
        }
    }

    function answerAudio() {
        console.log( parseInt(document.getElementById("videoEnd-answer").value))
        console.log(document.getElementById("audio-answer").currentTime)
        if (document.getElementById("audio-answer").currentTime >=  parseInt(document.getElementById("videoEnd-answer").value)) {
            submitCorrectAnswer();
        }
    }

</script>
<script src="scripts\logout.js"></script>
</html>