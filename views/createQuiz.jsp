<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Creating a New Quiz</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>
        .title {
            font-size: 40px;
        }

        .wrap {
            padding: 60px 0;
            justify-content: unset;
            overflow-y: scroll;
            -ms-overflow-style: none;
            /* Internet Explorer 10+ */
            scrollbar-width: none;
            /* Firefox */
            -webkit-scrollbar: none;
            z-index: -99;
        }

        .newQuizForm {
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

        .newQuizForm label {
            font-size: 22px;
            margin-top: 10px;
        }

        .newQuizForm input,
        .newQuizForm textarea,
        .newQuizForm select {
            border: 0;
            border-radius: 10px;
            padding: 15px 20px;
            font-size: 18px;
        }

        .createQuizBtn {
            all: unset;
            margin-top: 20px;
            padding: 20px;
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

        .createQuizBtn:hover {
            box-shadow: inset 5px 5px 5px rgba(1, 1, 1, 0.8);
        }

        :focus {
            outline: none;
        }

        #categoryImage {
            margin-top: 30px;
        }
    </style>
    <%@page import="java.util.ArrayList" %>
</head>

<body>
    <header>
        <form action="home">
            <button class="homeBtn" type="Submit">Home</button>
        </form>
        <form action="logout">
            <button id="logoutButton" class="logoutBtn" type="Submit">Log Out</button>
        </form>
    </header>

    <div class="wrap">
        <div class="title cherry-cream-soda">
            Create a New Quiz
        </div>
        <!-- <% if (request.getAttribute("error") != null) { %>
            <p><%= request.getAttribute("error") %></p>
        <% } %> -->
        <!-- ↑ Error message I if threre is one -->

        <form id="newQuizForm" class="newQuizForm" method="post" action="createQuiz" enctype="multipart/form-data">
            <label for="quizName">Quiz Name:</label>
            <input type="text" id="quizName" name="quizName" required />
            <div class="quizImgWrap">
                <input type="checkbox" name="quizImage" id="quizImage">
                <label for="quizImage">Add Quiz Image</label>
                <div id="imageUploadQuiz" style="display: none;">
                    <label for="quizMediaFile">File:</label>
                    <input type="file" id="quizMediaFile" name="quizMedia" accept="image/*" />
                </div>
            </div>

            <label for="categoryName">Category Name:</label>
            <select name="categoryName" id="categories">


                
            </select>
            <div id="newCatDiv" style="display:none;">
                <label for="newCategory">Other Category:</label>
                <input id="newCategory" name="newCategory" type="text" />
                <br>
                <input type="checkbox" name="categoryImage" id="categoryImage">
                <label for="categoryImage">Add Category Image</label>
                <div id="imageUploadCategory" style="display: none;">
                    <label for="categoryMediaFile">File:</label>
                    <input type="file" id="categoryMediaFile" name="categoryMedia" accept="image/*" />
                </div>
            </div>

            <label for="description">Description:</label>
            <textarea id="description" name="description"></textarea>

            <button class="createQuizBtn" type="submit">Create Quiz</button>

        </form>

        <!-- Check if the quiz was successfully created -->
        <!-- <% if (request.getAttribute("quizName") != null) { %> -->
        <!-- <h2>Quiz "<%= request.getAttribute("quizName") %>" created successfully!</h2> -->

        <h2>Quiz quizName created successfully!</h2>

        <!-- Button to add questions to the newly created quiz -->
        <form method="post" action="addQuestion">
            <!-- <input type="hidden" name="quizName" value="<%= request.getAttribute("quizName") %>" /> -->
            <!-- // ADD IMAGES -->
            <!-- <button type="submit">Add Questions to "<%= request.getAttribute("quizName") %>"</button> -->
            <input type="hidden" name="quizName" value="quizName">
            <button type="submit">Add Questions to quizName</button>
        </form>
        <!-- <% } %> -->
    </div>
</body>
<script src="scripts\logout.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const form = document.getElementById("newQuizForm");
        const categories = document.getElementById('categories');

        const currentPath = window.location.pathname;
        const pathSegments = currentPath.split('/');
        pathSegments.pop();
        let newPath = pathSegments.join('/') + '/createQuiz-json';

        fetch(newPath, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('Failed to fetch categories');
                }
                return response.json();
            }).then(data => {
                console.log('Fetched categories:', data);

                if (data.categories.length === 0) {
                    categories.innerHTML = '<p>No categories available</p>';
                    return;
                }

                data.categories.forEach(category => {
                    const categoryOption = document.createElement('option');
                    categoryOption.value = category.name;
                    categoryOption.innerHTML = category.name;

                    // Append category div to the container
                    categories.appendChild(categoryOption);
                });

                categories.innerHTML += "<option value=\"ADDANOTHERCATEGORY\">Other</option>";
            })
            .catch(error => {
                console.error('Error fetching categories:', error);
                document.getElementById('categories').innerHTML = '<p>There was an error loading the categories. Please try again later.</p>';
            });


        form.addEventListener("submit", function (event) {
            event.preventDefault(); // Prevent the default form submission

            const formData = new FormData(form); // Create a FormData object to handle the file uploads
            let category = document.getElementById("categories").value;
            if(category === "ADDANOTHERCATEGORY"){
                category = document.getElementById("newCategory").value;
            }
        const currentPath = window.location.pathname;
        const pathSegments = currentPath.split('/');
        pathSegments.pop();
        let newPath = pathSegments.join('/') + '/createQuiz-json';
        let successPath = pathSegments.join('/') + '/quizzes?categoryName=' + category;

            fetch(newPath, { // Replace with your servlet URL
                method: "POST",
                body: formData,
                headers: {
                    "Accept": "application/json" // Expect a JSON response
                }
            })
                .then(response => {
                    return response.json().then(data => {
                if (response.ok) {
                    pathSegments.pop();
                    window.location.href = successPath;
                
                } else {
                    throw new Error(data.message || 'An error occurred');
                }
            })
                })
                .catch(error => {
                    // Handle any errors that occurred during the fetch
                    console.error("Error:", error.message);
                    alert("An error occurred: " + error.message);
                });
        });

    });

    function addAnswer() {
        const answerDiv = document.createElement('div');
        answerDiv.classList.add('answer');
        answerDiv.innerHTML = `
            <input type="text" name="answerText" placeholder="Answer" required>
            <input type="radio" name="correctAnswer" value="${document.querySelectorAll('input[name="answerText"]').length + 1}"> Correct
        `;
        document.getElementById('answersContainer').appendChild(answerDiv);
    }

    let categoryImage = document.getElementById("categoryImage");
    let categoryMediaFile = document.getElementById("imageUploadCategory");
    categoryImage.addEventListener('change', () => {
        if (categoryImage.checked) {
            categoryMediaFile.style.display = "block";
        } else {
            categoryMediaFile.style.display = "none";
        }
    });

    let quizImage = document.getElementById("quizImage");
    let quizMediaFile = document.getElementById("imageUploadQuiz");
    quizImage.addEventListener('change', () => {
        if (quizImage.checked) {
            quizMediaFile.style.display = "block";
        } else {
            quizMediaFile.style.display = "none";
        }
    });

    let newCatInput = document.getElementById("newCategory");
    let newCatDiv = document.getElementById("newCatDiv");
    let catSelect = document.getElementById("categories");
    catSelect.addEventListener('change', () => {
        if (catSelect.value === "ADDANOTHERCATEGORY") {
            newCatDiv.style.display = "block";
            newCatInput.required = true;
        } else {
            newCatDiv.style.display = "none";
            newCatInput.required = false;
        }
    });
</script>

</html>