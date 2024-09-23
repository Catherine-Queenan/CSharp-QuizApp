<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quizzes</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

        .quizzes {
            width: 70%;
            padding: 50px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 30px;
        }

        .quiz {
            border-radius: 15px;
            border: 0;
            transition-duration: 0.3s;
            font-size: 20px;
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
            cursor: pointer;
        }
        
    </style>
</head>
<body>
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
