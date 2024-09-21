<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Main Page</title>
    <link rel="stylesheet" href="/CS-QuizGame/public/css/reset.css">
    <style>
        .title {
            text-align: center;
            font-size: 40px;
            margin-bottom: 20px;
        }
	
        .categories {
            width: 70%;
            padding: 50px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            flex-wrap: wrap;
            gap: 30px;
        }

        .category {
            border-radius: 15px;
            border: 0;
            transition-duration: 0.3s;
            font-size: 20px;
        }

        .category:hover {
            transform: scale(1.04);
            box-shadow: 5px 5px 10px rgb(14, 1, 47);
        }

        .category:nth-child(1),
        .category:nth-child(5n+1) {
            background-color: #FF4B32;
        }

        .category:nth-child(2),
        .category:nth-child(5n+2) {
            background-color: #FFB20F;
        }

        .category:nth-child(3),
        .category:nth-child(5n+3) {
            background-color: #FFE548;
            color: #0C1B33; 
        }

        .category:nth-child(4),
        .category:nth-child(5n+4) {
            background-color: #D7E8BA;
            color: #0C1B33;
        }

        .category:nth-child(5),
        .category:nth-child(5n+5) {
            background-color: #45425A;
        }

        .category form {
            width: 100%;
            height: 100%;
        }

        .category input {
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
            Categories
        </div>
        <div class="categories">
            <%= request.getAttribute("categoriesHtml") %>
        </div>
    </div>
</body>
</html>