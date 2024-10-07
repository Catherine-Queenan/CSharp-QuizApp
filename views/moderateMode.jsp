<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quiz</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

        .title {
            padding: 10px;
            text-align: center;
            font-size: 30px;
            margin-bottom: 0;
        }

        #next-button {
            margin-top: 30px;
            padding: 20px 40px;
            border-radius: 15px;
            border: 0;
            margin-right: 10px;
            font-size: 22px;
            background-color: #99c252;
            transition-duration: 0.3s;
            cursor: pointer;
        }

        /* options */
        #options {
            width: 100%;
            padding: 30px;
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            align-items: center;
            gap: 20px;
        }

        #options button {
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

        #options button:nth-child(1) {
            background-color: #d00000 !important;
        }

        #options button:nth-child(2) {
            background-color: #FF4B3E !important;
        }

        #options button:nth-child(3) {
            background-color: #FFB20F !important;
        }

        #options button:nth-child(4) {
            background-color: #99c252 !important;
        }

        /* answer count */
        #answerCounts {
            margin-top: 20px;
            width: fit-content;
            display: flex;
            flex-direction: column;
            justify-content: center;
            gap: 10px;
        }

        #answerCounts div {
            padding: 10px 20px;
            border-radius: 10px;
        }

        #answerCounts div:nth-child(2),
        #answerCounts div:nth-child(4n + 2) {
            background-color: #d00000 !important;
        }

        #answerCounts div:nth-child(3),
        #answerCounts div:nth-child(4n + 3) {
            background-color: #FF4B3E !important;
        }

        #answerCounts div:nth-child(4),
        #answerCounts div:nth-child(4n + 4) {
            background-color: #FFB20F !important;
        }

        #answerCounts div:nth-child(5),
        #answerCounts div:nth-child(4n + 5) {
            background-color: #99c252 !important;
        }
        
    </style>
</head>

<body>

    <header>
        <form action="home">
            <button class="homeBtn" type="Submit">Home</button>
        </form>
    </header>

    <div class="wrap">
        <div id="question-container">
            <p id="question" class="title"></p>
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
        <div style="display: none;">

            <div class="questions" style="display: none;">
                <%=request.getAttribute("questionsHtml")%>
            </div>

            <form id="questionForm" method="post" style="display: hidden;"></form>
        </div>
    </div>

    <script type="text/javascript">

        function setHeight() {
            // Get all buttons inside the div with class "answersOption"
            var buttons = document.querySelectorAll('#options button');
            
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
        }

        // Making all answer options have the same height value
        window.onload = function() {
            setHeight();
        };
       
        let role = document.getElementById("role").textContent.trim();
        console.log("Role: ", role);

        const currentPath = window.location.pathname;
        const pathSegments = currentPath.split('/');
        // Extract the base path dynamically (remove last segment if it's quiz-related)
        console.log(pathSegments);
        pathSegments.pop();         
        console.log(pathSegments);
        console.log(pathSegments.join('/'));

        // Construct the new path dynamically
        const newPath = pathSegments.join('/') + `/questionsws`;


        let webSocket = new WebSocket('ws://localhost:8081/'+ newPath);
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

            console.log(response);

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
                setHeight();
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