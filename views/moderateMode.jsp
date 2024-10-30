<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quiz</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f9;
            margin: 0;
            padding: 0;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        #question-container {
            margin: 20px;
            padding: 20px;
            background-color: white;
            border-radius: 8px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
            width: 90%;
            max-width: 600px;
            text-align: center;
        }

        #question {
            font-size: 24px;
            margin-bottom: 20px;
        }

        #options button {
            display: block;
            width: 100%;
            margin: 10px 0;
            padding: 15px;
            background-color: #3498db;
            color: white;
            border: none;
            border-radius: 5px;
            font-size: 18px;
            cursor: pointer;
            transition: background-color 0.3s;
        }

        #options button:hover {
            background-color: #2980b9;
        }

        #next-button {
            background-color: #2ecc71;
            color: white;
            border: none;
            padding: 15px 30px;
            font-size: 18px;
            border-radius: 5px;
            cursor: pointer;
            transition: background-color 0.3s;
            margin-top: 20px;
            display: none;
        }

        #next-button:hover {
            background-color: #27ae60;
        }

        #answerCounts {
            margin-top: 20px;
            text-align: left;
        }

        #answerCounts div {
            background-color: #f1f1f1;
            padding: 10px;
            margin: 5px 0;
            border-radius: 5px;
        }

        #timer {
            font-size: 20px;
            margin-top: 20px;
            color: #e74c3c;
        }
    </style>
</head>

<body>

    <header>
        <form action="home">
            <button class="homeBtn" type="Submit">Home</button>
        </form>

    </header>
    <div id="question-container">
        <p id="question"></p>
        <div id="options"></div>
    </div>

    <div id="answerCounts"></div>
    <p id="role" style="display: none;">
        <%=request.getAttribute("role")%>
    </p>
    <p id="userName" style="display: none;">
        <%=request.getAttribute("userName")%>
    </p>
    <button id="next-button">Next Question</button>
    <div class="wrap" style="display: none;">

        <div class="questions" style="display: none;">
            <%=request.getAttribute("questionsHtml")%>
        </div>


        <div id="timer" style="display:none;">Time left: 60 seconds</div>

        <form id="questionForm" method="post" style="display: hidden;"></form>
    </div>





    <script type="text/javascript">
       
        let role = document.getElementById("role").textContent.trim();
        console.log("Rolee: ", role);

        let webSocket = new WebSocket('ws://localhost:8081/project1/questionsws');
        let questions = [];
        let answers = [];
        let images = [];
        let videos = [];

        document.querySelectorAll("img").forEach((image) => {
            let mediaAttr = image.getAttribute("data-media");
            if (mediaAttr) {
                images.push({
                    src: image.getAttribute("src"),
                    media: mediaAttr // Store media attribute for reference
                });
            }
        });
        document.querySelectorAll("source").forEach((video) => {
            let mediaAttr = video.getAttribute("data-media");
            if (mediaAttr) {
                videos.push({
                    src: video.getAttribute("src"),
                    media: mediaAttr // Store media attribute for reference
                });
            }
        });
        console.log("Imagesss: ", images);
        console.log("Videos: ", videos);
        // Collect questions and answers from the HTML
        document.querySelectorAll(".questionTitle").forEach((question) => {
            questions.push(question.textContent);
        });


        console.log("Questions: ", questions);

        let numOfQuestions = questions.length;

        document.querySelectorAll(".answer").forEach((answer) => {
            answers.push(answer.textContent);
        });
        console.log("Questions: ", answers);

        let questionData = [];
        for (let i = 0; i < questions.length; i++) {
            let answerElements = document.querySelectorAll(`[data-question="${i + 1}"]`);

            // Collect the answer texts in an array
            let answers = Array.from(answerElements).map(answer => answer.innerHTML);

            // Collect images and videos for the current question
            let questionImages = images.filter(image => image.media == i + 1).map(image => image.src);
            let questionVideos = videos.filter(video => video.media == i + 1).map(video => video.src);

            // Create an object for each question and its answers
            questionData.push({
                question: questions[i],
                answers: answers,
                images: questionImages,
                videos: questionVideos
            });
        }

        webSocket.onopen = function () {
            console.log("Connection established ...");
            console.log("Sending data to server: ", JSON.stringify(questionData));  // Log the sent data
            webSocket.send(JSON.stringify(questionData));  // Send initial data (questions and answers)
        };

        let questionIndex;
        webSocket.onmessage = function (message) {
            console.log("Received message from server:", message.data);  // Log the incoming message
            let response = JSON.parse(message.data);
            questionIndex = response.questionIndex;

            if(response.type === "end"){
                webSocket.onclose = function () {
            console.log("Connection closed ...");
             globalThis.end = true;
            window.location.href = "end";
        };
            }
            if (response.question && response.answers) {
                displayQuestion(response.question, response.answers, response.images, response.videos);
            } else if (response.type === "answerCounts") {
                displayAnswerCounts(response.counts);
            } else {
                console.log("Invalid message received from server:", response);
            }
        };

        webSocket.onclose = function () {
            console.log("Connection closed ...");
             globalThis.end = true;
            window.location.href = "end";
        };
        // console.log("End: ", end);
        // if(end){
        //         window.location.href = "end";
        //     }
        // Display question, answers, and media (images, videos)
        function displayQuestion(question, answers, images, videos) {
            document.getElementById("question").textContent = question;
            const optionsDiv = document.getElementById("options");
            optionsDiv.innerHTML = '';  // Clear previous answers

            // Display answers as buttons
            answers.forEach(answer => {
                const answerElement = document.createElement("button");
                answerElement.textContent = answer;
                answerElement.classList.add("answer");
                optionsDiv.appendChild(answerElement);

                answerElement.addEventListener("click", function () {
                    console.log("Sending answer:", answer);  // Log the selected answer
                    webSocket.send(JSON.stringify({ type: "answer", answer: answer }));
                });
            });

            // Clear previous media before displaying new media
            let mediaDiv = document.getElementById("media-container");
            if (mediaDiv) {
                mediaDiv.remove();  // Remove the old media container
            }

            // Create a new media container
            const questionContainer = document.getElementById("question-container");
            mediaDiv = document.createElement("div");
            mediaDiv.id = "media-container";
            questionContainer.appendChild(mediaDiv);

            // Display images (if any)
            if (images && images.length > 0) {
                images.forEach(imageSrc => {
                    const imgElement = document.createElement("img");
                    if(role !== "a"){
                        imgElement.style.display = "none";
                    }
                    imgElement.src = imageSrc;
                    imgElement.alt = "Question Image";
                    imgElement.style.width = "100%"; // Adjust image size
                    mediaDiv.appendChild(imgElement);
                });
            }

            // Display YouTube videos (if any)
            if (videos && videos.length > 0) {
                videos.forEach(videoUrl => {
                    const videoElement = document.createElement("iframe");
                    if(role !== "a"){
                        videoElement.style.display = "none";
                    }
                    videoElement.src = videoUrl.replace("watch?v=", "embed/");
                    videoElement.width = "100%";
                    videoElement.height = "315";
                    videoElement.allow = "accelerometer; autoplay; clipboard-write; encrypted-media; gyroscope; picture-in-picture";
                    videoElement.allowFullscreen = true;
                    mediaDiv.appendChild(videoElement);
                });
            }

        }

        if (role === "a") {
            document.getElementById("next-button").style.display = "block";  // Show next button for admin
            document.getElementById("options").style.display = "none";
        }
        if (role !== "a") {
            document.getElementById("next-button").style.display = "none";
            document.getElementById("answerCounts").style.display = "none";
            document.getElementById("question").style.display = "none";
           
        } 
        // Handle "Next Question" button click
        document.getElementById("next-button").addEventListener("click", function () {
            //clear answer counts
            console.log("Sending next question request");
            console.log("Question index: ", questionIndex);
            console.log("Number of questions: ", numOfQuestions);
            webSocket.send(JSON.stringify({ type: "next" }));
        });

       
        // Display answer counts
        function displayAnswerCounts(counts) {
            let totalElement = document.getElementById("answerCounts");
            totalElement.innerHTML = "<h3>Answer Counts:</h3>";

            for (const answer in counts) {
                const answerCountElement = document.createElement("div");
                answerCountElement.textContent = `${answer}: ${counts[answer]}`;
                totalElement.appendChild(answerCountElement);
            }
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


</body>

</html>