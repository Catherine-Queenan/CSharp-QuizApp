<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Questions</title>
    <link rel="stylesheet" href="/CS-QuizGame/public/css/reset.css">
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
    </header>

    <div class="wrap">
        <div class="title cherry-cream-soda">
            Question <%=request.getAttribute("qNumber")%> / <%=request.getAttribute("quizSize")%>
        </div>
        <div class="questions">
            <%=request.getAttribute("questionsHtml")%>
        </div>
    </div>
    
    
    <%= request.getAttribute("mediaHtml") %>

</body>
<script>
    let body = document.getElementsByTagName("body");
    let wrongAnswers = documnet.getElementsByClassName("wrongPlayAnswer");
    let rightAnswer = document.getElementById("rightPlayAnswer");

    wrongAnswers.array.forEach(element => {
        element.addEventListener('click', () => {
            body.style.background = "red";
            setTimeout(() => {
                body.style.background = "white";
            }, 5000);
        })
    });

    rightAnswer.addEventListener('click', () =>{
        body.style.background = "green";
        setTimeout(() => {
            document.getElementById("questionForm").submit();
        }, 5000);
    });

</script>
</html>
