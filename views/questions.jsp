<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Questions</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

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
        
        .homeBtn {
            background-color: #DCEED1;
        }

        .restartBtn {
            background-color: #FF4B3E;
            color: #DCEED1;
        }
        
        .title {
            text-align: center;
            font-size: 40px;
            margin-bottom: 20px;
        }

        .questions {
            width: 80%;
        }

        .question>p {
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
            <input name="enabled" type="hidden" id="autoplay" value="<%= request.getAttribute("autoplay") != null && (Boolean)request.getAttribute("autoplay") ? "true" : "false" %>">
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
        <div id="timer" style="font-size: 20px; text-align: center; margin-top: 20px; display:none;">Time left: 60 seconds</div>
    </div>
</body>
<script>
    //---------------AUTOPLAY---------------\\
    let autoplayEnabled = document.getElementById("autoplay").value === "false";
    let autoplayTimer;
    let countdownTime = 60;
    let timerDisplay = document.getElementById('timer');
    let timerInterval;

        const autoplayToggleButton = document.getElementById('autoplayToggle');
        const correctButton = document.querySelector('.answer[id="rightPlayAnswer"]');

        // Set up event listener for the autoplay toggle button
        // NO MORE CONSOLE.LOGS
        autoplayToggleButton.addEventListener('click', () => {
            event.preventDefault();
            autoplayEnabled = !autoplayEnabled;

            console.log(autoplayEnabled);
            console.log(document.getElementById("autoplay").value);
            console.log("Please god work");

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
                timerDisplay.textContent = "Time left: 60 seconds";
                fetch('updateAutoplay?enabled=false');
            }
        });

        // Start autoplay if enabled on page load
        if (autoplayEnabled) {
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
                if (correctButton) {
                    correctButton.click();
                }
            }
        }, 1000);

        autoplayTimer = setTimeout(() => {
            if (correctButton) {
                correctButton.click();
            }
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