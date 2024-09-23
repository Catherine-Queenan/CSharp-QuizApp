

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create Quiz and Add Questions</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
            padding: 20px;
            background-color: #f4f4f4;
        }
        .container {
            max-width: 600px;
            margin: auto;
            background-color: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1);
        }
        h1 {
            text-align: center;
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 10px;
        }
        input[type="text"], textarea, input[type="radio"] {
            width: calc(100% - 22px);
            padding: 10px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        input[type="radio"] {
            width: auto;
            margin-right: 10px;
        }
        .answer {
            margin-bottom: 10px;
        }
        button {
            padding: 10px 20px;
            background-color: #5cb85c;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }
        button:hover {
            background-color: #4cae4c;
        }
        .button-link {
            display: inline-block;
            padding: 10px 20px;
            background-color: #0275d8;
            color: white;
            text-decoration: none;
            border-radius: 4px;
            margin-bottom: 20px;
        }
        .button-link:hover {
            background-color: #025aa5;
        }
    </style>
    <%@page import="java.util.ArrayList" %>
</head>
<body>
    <div class="container">
        <h1>Create a New Quiz</h1>
        <% if (request.getAttribute("error") != null) { %>
            <p style="color:red;"><%= request.getAttribute("error") %></p>
        <% } %>
        <form method="post" action="createQuiz">
            <label for="quizName">Quiz Name:</label>
            <input type="text" id="quizName" name="quizName" required /><br />

            <label for="categoryName">Category Name:</label>
            <select name="categoryName" id="category">
                <% ArrayList<String> categories = (ArrayList<String>)request.getAttribute("categories"); %>
                <% for(int i = 0; i < categories.size(); i++){ %>
                    <option value="<%= categories.get(i)%>"><%= categories.get(i)%></option>
                <%} %>
                <option value="ADDANOTHERCATEGORY">Other</option>
            </select>
            <div id="newCatDiv" style="display:none;">
                <label for="newCategory">Other Category:</label>
                <input id="newCategory" name="newCategory" type="text" />
            </div>

            <label for="description">Description:</label><br />
            <textarea id="description" name="description"></textarea><br />
            <button type="submit">Create Quiz</button>

        </form>
         <!-- Check if the quiz was successfully created -->
         <% if (request.getAttribute("quizName") != null) { %>
            <h2>Quiz "<%= request.getAttribute("quizName") %>" created successfully!</h2>

            <!-- Button to add questions to the newly created quiz -->
            <form method="post" action="addQuestion">
                <input type="hidden" name="quizName" value="<%= request.getAttribute("quizName") %>" />
                <button type="submit">Add Questions to "<%= request.getAttribute("quizName") %>"</button>
            </form>
        <% } %>
    </div>
</body>
<script>
    function addAnswer() {
        const answerDiv = document.createElement('div');
        answerDiv.classList.add('answer');
        answerDiv.innerHTML = `
            <input type="text" name="answerText" placeholder="Answer" required>
            <input type="radio" name="correctAnswer" value="${document.querySelectorAll('input[name="answerText"]').length + 1}"> Correct
        `;
        document.getElementById('answersContainer').appendChild(answerDiv);
    }

    let newCatInput = document.getElementById("newCategory");
    let newCatDiv = document.getElementById("newCatDiv");
    let catSelect = document.getElementById("category");
    catSelect.addEventListener('change', () =>{
        if(catSelect.value === "ADDANOTHERCATEGORY"){
            newCatDiv.style.display = "block";
            newCatInput.required = true;
        } else {
            newCatDiv.style.display = "none";
            newCatInput.required = false;
        }
    });
</script>
</html>
