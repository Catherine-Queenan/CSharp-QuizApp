<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Quiz</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>
        
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
