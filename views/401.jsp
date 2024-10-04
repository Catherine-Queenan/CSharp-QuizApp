<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Unauthorized Access</title>
    <style>
        body {
            text-align: center;
            padding: 50px;
        }
        .unauthorized-message {
            font-size: 24px;
            color: red;
            margin-bottom: 20px;
        }
        .back-home-button {
            font-size: 18px;
            padding: 10px 20px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }
        .back-home-button:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
    <div class="wrap">
        <i class="fa-solid fa-triangle-exclamation"></i>
        <% String errorMessage = (String) request.getAttribute("errorMessage"); %>
            <% if (errorMessage != null) { %>
                <div class="errorMsg cherry-cream-soda">
                    <%= errorMessage %>
                </div>
            <% } %>
        <button class="redirectHome" onclick="window.location.href='home'">Go Back to Home</button>
    </div>
</body>
</html>
