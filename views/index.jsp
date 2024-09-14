<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Index?</title>
    <link rel="stylesheet" href="reset.css">
    <style>
        .title {
            text-align: center;
            font-size: 40px;
            margin-bottom: 50px;
        }

        .categories {
            width: 85%;
            height: 500px;
            display: flex;
            justify-content: space-around;
        }

        .category {
            width: 23%;
            height: 100%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            transition-duration: 0.5s;
        }

        .category:hover {
            transform: scale(1.05);
            transition-duration: 0.5s;
        }

        .img {
            width: 100%;
            height: 80%;
            border-radius: 15px;
            overflow: hidden;
            background-color: darkseagreen;
            box-shadow: 7px 7px 10px rgba(0, 0, 0, 0.4);
        }

        .c-title {
            width: 100%;
            text-align: center;
            font-size: 25px;
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 10px;
        }
    </style>
</head>
<body>
    <header>
        <div class="loginbtn">
            login
        </div>
    </header>
    <div class="wrap">
        <div class="title">
            Trivia Game
        </div>
        <div class="categories">
            <!-- Categories will be injected dynamically by the servlet -->
            <%= request.getAttribute("categoriesHtml") %>
        </div>
    </div>
</body>
</html>
