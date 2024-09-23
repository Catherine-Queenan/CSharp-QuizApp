<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Quiz</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

        .wrap {
            padding: 80px 0;
            justify-content: unset;
            overflow-y: scroll;
            -ms-overflow-style: none;  /* Internet Explorer 10+ */
            scrollbar-width: none;  /* Firefox */
            -webkit-scrollbar: none;
        }

        .title {
            font-size: 40px;
            margin: 0;
        }

        .questionsWrap {
            width: 65%;
            margin-top: 50px;
            border-radius: 15px;
            font-size: 18px;
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .question {
            width: 100%;
            background-color: #45425A;
            border-radius: 15px;
            padding: 40px 40px 30px 40px;
        }

        .addQuestionBtn,
        .deleteBtn {
            all: unset;
            display: inline-block;
            margin-top: 20px;
            padding: 15px 40px;
            border-radius: 15px;
            font-size: 18px;
            /* color: rgb(244, 244, 244); */
            color: #0C1B33;
            background-color: #FF4B3E;
            cursor: pointer;
            transition-duration: 0.3s;
        }

        .addQuestionBtn {
            margin-top: 20px;
            background-color: #D7E8BA;
            font-size: 20px;
        }

        .deleteBtn:hover {
            box-shadow: inset 5px 5px 5px rgba(1, 1, 1, 0.5);
        }

        .addQuestionBtn:hover {
            transform: scale(1.03);
            box-shadow: 5px 5px 5px rgba(1, 1, 1, 0.4);
        }

    </style>
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
            Questions for <%= request.getAttribute("quizName") %>
        </div>

        <div class="questionsWrap">
            <%= request.getAttribute("questionsHtml") %>
        </div>

        <!-- Add Question button -->
        <a href="addQuestion?quizName=<%= request.getAttribute("quizName") %>" class="addQuestionBtn">Add Question</a>
    </div>

</body>
</html>
