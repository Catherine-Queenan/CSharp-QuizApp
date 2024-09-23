<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Index?</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

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

        /* New CSS for logout button */
        .header {
            display: flex;
            justify-content: flex-end;
            padding: 10px;
        }

        .logout-btn {
            background-color: #007bff;
            color: white;
            border: none;
            border-radius: 5px;
            padding: 10px 20px;
            cursor: pointer;
            font-size: 16px;
        }

        .logout-btn:hover {
            background-color: #0056b3; 
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
        <div class="title">
            Trivia Game
        </div>
    </div>
    
    <h1>Quiz Dashboard</h1>
    <div id="content">
        <%= request.getAttribute("contentHtml") %>
    </div>
</body>
</html>
