<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quizzes</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

        .wrap {
            padding: 50px;
        }

        .quizzes {
            width: 80%;
            padding: 50px 10px;
            overflow-y: scroll;
            display: flex;
            justify-content: space-between;
            align-items: flex-start;
            flex-wrap: wrap;
            gap: 30px;

            /* Hiding scroll bar */
            -ms-overflow-style: none;  /* Internet Explorer 10+ */
            scrollbar-width: none;  /* Firefox */
        }

        .quizzes::-webkit-scrollbar { 
            display: none;  /* Safari and Chrome */
        }

        .quiz {
            width: 31%;
            border: 0;
            border-radius: 15px;
            padding: 10px;
            font-size: 25px;
            color: #0C1B33;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            transition-duration: 0.3s;
        }

        .quiz:hover {
            transform: scale(1.04);
            box-shadow: 5px 5px 10px rgb(14, 1, 47);
        }

        .quiz:nth-child(1),
        .quiz:nth-child(5n+1) {
            background-color: #FF4B32;
        }

        .quiz:nth-child(2),
        .quiz:nth-child(5n+2) {
            background-color: #FFB20F;
        }

        .quiz:nth-child(3),
        .quiz:nth-child(5n+3) {
            background-color: #FFE548;
            color: #0C1B33; 
        }

        .quiz:nth-child(4),
        .quiz:nth-child(5n+4) {
            background-color: #D7E8BA;
            color: #0C1B33;
        }

        .quiz:nth-child(5),
        .quiz:nth-child(5n+5) {
            background-color: #45425A;
        }

        .quiz form {
            width: 100%;
            height: 100%;
        }

        .quiz input {
            all: unset;
            width: 100%;
            height: 100%;
            padding: 20px 40px;
            box-sizing: border-box;
            text-align: center;
            cursor: pointer;
        }

        .quiz p {
            padding: 0 20px 10px 20px;
            font-size: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .adminBtnWrap {
            margin-top: 10px;
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            align-items: center;
            gap: 10px;
        }

        .adminBtnWrap button {
            border: 1px solid rgba(1, 1, 1, 0.1);
            border-radius: 10px;
            padding: 10px 20px;
            transition-duration: 0.3s;
        }

        .adminBtnWrap button:hover {
            transform: scale(1.03);
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
            Available Quizzes
        </div>
        <div class="quizzes">
            <%= request.getAttribute("quizzesHtml")%>
        </div>
    </div>
</body>
</html>
