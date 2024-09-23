<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Quiz</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>

        .title {
            font-size: 40px;
            margin: 0;
        }

        .eidtQuizForm {
            transform: scale(0.9);
            width: 60%;
            padding: 50px;
            border-radius: 15px;
            font-size: 18px;
            background-color: #45425A;
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .eidtQuizForm label {
            font-size: 22px;
            margin-top: 10px;
        }

        .eidtQuizForm input,
        .eidtQuizForm textarea {
            border: 0;
            border-radius: 10px;
            padding: 15px 20px;
            font-size: 18px;
        }

        .button-container {
            display: flex;
            /* flex-direction: column; */
            gap: 20px;
            justify-content: center;
            align-items: center;
        }
        
        .button-container a,
        .saveBtn {
            all: unset;
            margin-top: 20px;
            padding: 20px 50px;
            border-radius: 15px;
            font-size: 20px;
            color: rgb(244, 244, 244);
            display: flex;
            justify-content: center;
            align-items: center;
            background-color: #0C1B33;
            cursor: pointer;
            transition-duration: 0.3s;
        }

        .button-container a {
            background-color: #99c252;
            color: #0C1B33;
        }

        .button-container a:hover,
        .saveBtn:hover {
            box-shadow: inset 5px 5px 5px rgba(1, 1, 1, 0.5);
        }
        
        :focus {
            outline: none;
        }

        .mediaType {
            margin-bottom: 20px;
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
        <%-- Render the form generated in the servlet --%>
        <%= request.getAttribute("editFormHtml") %>    
    </div>

</body>
</html>
