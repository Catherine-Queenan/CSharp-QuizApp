<!DOCTYPE html>
<html>

<head>
    <title>Questions</title>
</head>

<body>
    <form action="home"><button type="Submit">Home</button></form>
    <form method="post">
        <input type="hidden" value="true" name="restart">
        <button type="Submit">Restart</button>
    </form>
    <h1>Question <%=request.getAttribute("qNumber")%> out of <%=request.getAttribute("quizSize")%>
    </h1>
    <div id="questions">
        <%=request.getAttribute("questionsHtml")%>
    </div>

</body>
<script>

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
        if (document.querySelector("audio").currentTime >= endTime) {
            document.querySelector("audio").currentTime = startTime
        }
    }

</script>

</html>