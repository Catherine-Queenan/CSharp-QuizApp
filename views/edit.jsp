<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Edit Quiz</title>
    <link rel="stylesheet" href="../public/css/reset.css">
    <style>
        #wrap {
            padding: 80px 0;
            justify-content: unset;
            overflow-y: scroll;
            -ms-overflow-style: none;
            /* Internet Explorer 10+ */
            scrollbar-width: none;
            /* Firefox */
            -webkit-scrollbar: none;
        }

        /* Responsive */
        @media screen and (max-width: 800px) {
            .wrap {
                padding: 0;
            }
        }

        .editQuizForm {
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

        .editQuizForm label {
            font-size: 22px;
            margin-top: 10px;
        }

        .editQuizForm input,
        .editQuizForm textarea {
            border: 0;
            border-radius: 10px;
            padding: 15px 20px;
            font-size: 18px;
        }

        #mediaUpload {
            padding: 0;
            border-radius: 0;
        }

        .button-container {
            display: flex;
            text-align: center;
            gap: 20px;
            justify-content: center;
            align-items: center;
        }

        .button-container a,
        .saveBtn {
            all: unset;
            box-sizing: border-box;
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

        .quizMediaWrap {
            width: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .quizMediaWrap img {
            display: flex;
            justify-content: center;
            align-items: center;
            border-radius: 15px;
            max-width: 500px;
            width: 100%;
            max-height: 500px;
            height: 100%;
            object-fit: cover;
        }

    </style>
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

    <div class="wrap" id="wrap">
        <!-- <%-- Render the form generated in the servlet --%>
        <%= request.getAttribute("editFormHtml") %>     -->

        <div class="title cherry-cream-soda">
            Edit Quiz: <span id="quizNameTitle" class="cherry-cream-soda"></span>
        </div>
        
        <form id="editQuizForm" class="editQuizForm" method="post">

            <label for="title">Quiz Title:</label>
            <input type="text" id="quizNameInput" name="title" value="quizName">

            <input id="originalName" type="hidden" name="quizName" value="">
            <label for="description">Description:</label>
            <textarea id="description" name="description"></textarea>

            <label for="quizMedia">Quiz Image (optional):</label>
            <input type="file" id="mediaUpload" name="quizMedia">
            <div class="quizMediaWrap">
                <img id="quizMedia" src="" style="display:none">
            </div>

            <div class="button-container">
                <a id="seeQuestionsLink" class="button-link">Go Back to Questions List</a>
                <button id="saveButton" class="saveBtn">Save Changes</button>
            </div>
        </form>


    </div>

</body>
<script src="scripts\logout.js"></script>
<script>
    document.addEventListener('DOMContentLoaded', function () {
        // Making buttons same height
        // const seeQuestionsLinkHeight = .getBoundingClientRect().height;
        // const saveButtonHeight = document.getElementById("saveButton").getBoundingClientRect().height;

        const seeQuestionsLink = document.getElementById("seeQuestionsLink");
        const saveButton = document.getElementById("saveButton");

        
        const seeQuestionsLinkHeight = parseFloat(window.getComputedStyle(document.getElementById("seeQuestionsLink")).height);            
        const saveButtonHeight = parseFloat(window.getComputedStyle(document.getElementById("saveButton")).height);
        const buttonsPaddingValue = parseFloat(window.getComputedStyle(document.getElementById("seeQuestionsLink")).paddingTop);
        
        if (seeQuestionsLinkHeight > saveButtonHeight) {
            console.log(`${seeQuestionsLinkHeight}`)
            document.getElementById("saveButton").style.height = `${seeQuestionsLinkHeight}px`;
        } else {
            document.getElementById("seeQuestionsLink").style.height = `${saveButtonHeight}px`;
        }


        const quizTitleName = document.getElementById('quizNameTitle');
        const quizDescription = document.getElementById('description');
        const quizNameInput = document.getElementById('quizNameInput');
        const hiddenQuizName = document.getElementById('originalName');

        const quizImage = document.getElementById('quizMedia');

        const seeQuestions = document.getElementById('seeQuestionsLink');

        const currentPath = window.location.pathname;
        const pathSegments = currentPath.split('/');

        // Extract the category name from URL parameters
        const quizName = pathSegments[3];
        console.log(quizName);

        // Extract the base path dynamically (remove last segment if it's quiz-related)
        pathSegments.pop();
        pathSegments.pop();

        // Construct the new path dynamically
        const newPath = pathSegments.join('/') + `/edit-json/?quizName=${quizName}`;
        const postPath = pathSegments.join('/') + `/edit-json`;
        const homePath = pathSegments.join('/') + `/home`;
        const questionsPath = pathSegments.join('/') + "/editQuestions/";

        console.log(newPath)
        fetch(newPath, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        }).then(response => {
            if (!response.ok) {
                console.error('Response status:', response.status);
                throw new Error('Failed to fetch quiz');
            }
            return response.json(); // Change this temporarily to text() instead of json()
        }).then(data => {
            console.log(data)

            // Render quizzes dynamically
            if (data.quiz == null) {
                window.location.href = homePath;
            } else {
                quizTitleName.innerHTML = data.quiz.name;
                quizDescription.innerHTML = data.quiz.description;
                quizNameInput.value = data.quiz.name;
                hiddenQuizName.value = data.quiz.name;

                seeQuestions.href = questionsPath + data.quiz.name;

                if (data.quiz.media != null) {
                    quizImage.src = data.quiz.media.media_file_path;
                    quizImage.style.display = "block";
                }
            }
        });

        document.getElementById("saveButton").addEventListener('click', function () {
            console.log('Saving Changes');
            const form = document.getElementById("editQuizForm");
            const formData = new FormData(form);
            for (var pair of formData.entries()) {
                console.log(pair[0] + ', ' + pair[1]);
            }

            fetch(postPath, {
                method: 'POST',
                body: formData,

            }).then(response => {
                console.log(response);

                if (response.ok) {
                    pathSegments.pop();
                    window.location.reload();

                } else {
                    throw new Error(data.message || 'An error occurred');
                }

            })
                .catch(error => {
                    // Handle any errors that occurred during the fetch
                    console.error("Error:", error.message);
                    alert("An error occurred: " + error.message);
                });

        });

        const mediaUpload = document.getElementById("mediaUpload");
        mediaUpload.addEventListener('input', (event) => {

            //get the image, its url and the element to display it in
            let imageFile = event.target.files[0]
            let path = URL.createObjectURL(imageFile);

            //update the image display
            quizImage.style.display = "block";
            quizImage.src = path;
        });
    }); 
</script>
</html>