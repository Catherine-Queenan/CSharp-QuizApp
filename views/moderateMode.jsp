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
    <p id="role" style="display: none;"><%=request.getAttribute("role")%></p>
    <p id="role" style="display: none;"><%=request.getAttribute("userName")%></p>
    <button id="next-button">Next Question</button>
    <div class="wrap">

        <div class="questions" style="display: none;">
            <%=request.getAttribute("questionsHtml")%>
        </div>

        <div id="timer" style="display:none;">Time left: 60 seconds</div>

        <form id="questionForm" method="post" style="display: hidden;"></form>
    </div>
    <div id="question-container">
        <p id="question"></p>
        <div id="options"></div>
    </div>



<script type="text/javascript">
    let role = document.getElementById("role").textContent;
    console.log("Role: ", role);
    if (role !== "a") {
        document.getElementById("next-button").style.display = "none";
        document.getElementById("answerCounts").style.display = "none";
        document.getElementById("question").style.display = "none";
    }
    let webSocket = new WebSocket('ws://localhost:8081/project1/questionsws');
    let questions = [];
    let answers = [];

    // Collect questions and answers from the HTML
    document.querySelectorAll(".questionTitle").forEach((question) => {
        questions.push(question.textContent);
    });

    let numOfQuestions = questions.length;
    
    document.querySelectorAll(".answer").forEach((answer) => {
        answers.push(answer.textContent);
    });

    let questionData = [];
    for (let i = 0; i < questions.length; i++) {
        let answerElements = document.querySelectorAll(`[data-question="${i + 1}"]`);

        // Collect the answer texts in an array
        let answers = Array.from(answerElements).map(answer => answer.innerHTML);

        // Create an object for each question and its answers
        questionData.push({
            question: questions[i],
            answers: answers
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
        
       
       
       if (response.question && response.answers) {
            displayQuestion(response.question, response.answers);
        } else if (response.type === "answerCounts") {
            displayAnswerCounts(response.counts);
        }else{
            console.log("Invalid message received:", response);
        }

        
    };

    // Display question and answers
    function displayQuestion(question, answers) {
        document.getElementById("question").textContent = question;
        const optionsDiv = document.getElementById("options");
        optionsDiv.innerHTML = '';  // Clear previous answers

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
        if (role === "a") {
            document.getElementById("next-button").style.display = "block";  // Show answer counts
            document.getElementById("options").style.display = "none";
        }
    }

    // Handle "Next Question" button click
    document.getElementById("next-button").addEventListener("click", function () {
        console.log("Sending next question request");
        console.log("Question index: ", questionIndex);
        console.log("Number of questions: ", numOfQuestions);
        if(questionIndex == numOfQuestions-1){
            window.location.href = "end";
        }
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
</script>


</body>

</html>