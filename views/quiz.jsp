<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quizzes</title>
    <link rel="stylesheet" href="../public/css/reset.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>

        .homeBtn a {
            text-decoration: none;
            color: #0C1B33;
        }

        .wrap {
            padding: 50px;
        }

        .quizzesBtnWrap {
            margin-top: 20px;
            width: 90%;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        #quizzesWrap {
            width: 90%;
            margin: 0 auto;
            position: relative;
            overflow: hidden; /* Hides overflow categories */
        }

        .quizzes {
            width: max-content; /* Dynamically adjust width */
            display: flex;
            justify-content: center;
            gap: 30px;
            overflow: hidden;
            transition: transform 0.4s ease-in-out;
        }

        .quiz {
            width: 30%;
            border: 0;
            border-radius: 15px;
            padding: 0 10px 25px 10px;
            font-size: 25px;
            color: #0C1B33;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            transition-duration: 0.3s;
            cursor: pointer;
        }

        .quiz:hover {
            transform: scale(0.99);
            box-shadow: inset 5px 5px 10px rgba(14, 1, 47, 0.7);
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
            background-color: #99c252;
            color: #0C1B33;
        }

        .quiz:nth-child(5),
        .quiz:nth-child(5n+5) {
            background-color: #6e6ba6;
        }

        .quizLink {
            all: unset;
            width: 100%;
            padding: 20px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            cursor: pointer;
        }

        .quiz input {
            all: unset;
            font-size: 30px;
            width: 100%;
            text-align: center;
            padding: 20px 0 0 0;
            box-sizing: border-box;
            cursor: pointer;
        }

        .quiz-description {
            width: 100%;
            padding-top: 10px;
            font-size: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        /* Quiz img */
        .quiz img {
            margin-top: 20px;
            max-width: 90%;
            width: 100%;
            max-height: 200px;
            height: 100%;
            width: 100%;
            height: 100%;
            border-radius: 10px;
            object-fit: cover;
        }

        .img {
            width: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        /* Slide buttons */
        .btn {
            all: unset;
            font-size: 30px;
            color: #DCEED1;
            border: none;
            cursor: pointer;
            transition-duration: 0.2s;
        }
        
        .btn:hover {
            color: #45425A;
        }
        
        .prev {
            display: none;
        }

        .next {
            display: none;
        }

        /* Admin section */
        .adminBtnWrap {
            display: flex;
            flex-wrap: wrap;
            justify-content: center;
            align-items: center;
            gap: 10px;
        }

        .adminBtnWrap button {
            border: 1px solid rgba(1, 1, 1, 0.1);
            border-radius: 10px;
            padding: 10px 20px;
            background-color: #DCEED1;
            transition-duration: 0.3s;
            cursor: pointer;
        }

        .adminBtnWrap button:hover {
            transform: scale(1.03);
            box-shadow: inset 5px 5px 5px #5a6a3e8c;
        }
        
        .deleteButton {
            background-color: #D00000 !important;
            color: rgb(244, 244, 244);
        }
        
        .deleteButton:hover {
            transform: scale(1.03);
            box-shadow: inset 5px 5px 5px rgba(1, 1, 1, 0.3);
        }

        .errorCode {
            font-size: 25px;
        }

        .moderateMode {
            margin-top: 10px;
            border-radius: 10px;
            padding: 10px 20px;
            background-color: #45425A;
            text-decoration: none;
            text-align: center;
            color: rgb(244, 244, 244);
            font-size: 16px;
            display: flex;
            justify-content: center;
            align-items: center;
            transition-duration: 0.3s;
            cursor: pointer;
        }

        .moderateMode:hover {
            transform: scale(1.03);
            box-shadow: inset 5px 5px 5px rgba(1, 1, 1, 0.3);
        }

        /* Responsive */
        @media screen and (max-width: 800px) {
            .wrap {
                padding: 30px;
            }
            /* Quizzes page */
            .quiz input {
                padding: 10px !important;
            }

            .quiz-description {
                margin: 0 !important;
            }
        }
    </style>
</head>
<body>
    <header>
        <button class="homeBtn">
            <a href="../home">
                Home
            </a>
        </button>
        <form action="logout">
            <button id="logoutButton" class="logoutBtn" type="Submit">Log Out</button>
        </form>
    </header>

    <div class="wrap">
        <div class="title cherry-cream-soda">
            Available Quizzes
        </div>

        <div class="quizzesBtnWrap">
            <button class="btn prev"><i class="fa-solid fa-chevron-left"></i></button>
            <div id="quizzesWrap">
                <div class="quizzes" id="quizzes"></div>
            </div>
            <button class="btn next"><i class="fa-solid fa-chevron-right"></i></button>
        </div>
    </div>
</body>
<script src="..\scripts\logout.js"></script>
    <script>
        
        document.addEventListener('DOMContentLoaded', function() {

            const currentPath = window.location.pathname;
            const pathSegments = currentPath.split('/');

            // Extract the category name from URL parameters
            const categoryName = pathSegments[3];

            // Extract the base path dynamically (remove last segment if it's quiz-related)
            pathSegments.pop(); 
            pathSegments.pop();

            // Construct the new path dynamically
            const newPath = pathSegments.join('/') + `/quizzes-json/?categoryName=${categoryName}`;
            const homePath = pathSegments.join('/') + `/home`;

            // console.log(newPath)
            fetch(newPath, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            })
            .then(response => {
                if (!response.ok) {
                    console.error('Response status:', response.status);
                    throw new Error('Failed to fetch quizzes');
                }
                return response.json(); // Change this temporarily to text() instead of json()
            })
            .then(data => {
                console.log(data)
                const quizzesContainer = document.getElementById("quizzes");
                quizzesContainer.innerHTML = '';

                // Render quizzes dynamically
                if (data.quizzes == null || data.quizzes.length === 0) {
                    window.location.href = homePath;
                } else {
                    data.quizzes.forEach(quiz => {
                        const quizDiv = document.createElement('div');
                        quizDiv.className = 'quiz';
    
                        let mediaHtml = '';
                        if (quiz.media && quiz.media.media_file_path) {
                            mediaHtml = `<img src="${quiz.media.media_file_path}" alt="${quiz.name}" class="categoryImg">`;
                        }
    
                        quizDiv.innerHTML = `
                            <form method="post" action="${pathSegments.join('/')}/quizzes-json" class="quizLink">
                                <input type="hidden" name="quizName" value="${quiz.name}">
                                <input type="submit" value="${quiz.name}">
                                <p class="quiz-description">${quiz.description}</p>
                                <div class="img">${mediaHtml}</div>
                            </form>
                        `;


                            // <a class="quizLink" href="${quiz.name}">
                            //     <div class="quizName">${quiz.name}</div>
                            //     <p class="quiz-description">${quiz.description}</p>
                            //     <div class="img">${mediaHtml}</div>
                            // </a>
                            
                        if (data.role === "admin") {
                            quizDiv.innerHTML += `
                                <div class="adminBtnWrap">    
                                    <button type="button" onclick="window.location.href='${pathSegments.join('/')}/edit/${quiz.name}'">Edit Quiz</button>
                                    <button type="button" class="deleteButton">Delete Quiz</button>
                                </div>
                            `;
                        }

                        quizDiv.innerHTML += `
                            <a href="${pathSegments.join('/')}/moderateMode?quizName=${quiz.name}" class="moderateMode">
                                Moderated Mode
                            </a>
                        `;
    
                        // Append category div to the container
                        quizzesContainer.appendChild(quizDiv);
                    });
                }

                // console.log(window.innerWidth)

                // ----- Set up the sliding mechanism ----- \\
                if (window.innerWidth < 650) {
                    displayQuizzes(1);
                } else if (window.innerWidth < 1000) {
                    displayQuizzes(2);
                } else {
                    displayQuizzes(3);
                }

                document.querySelectorAll('.deleteButton').forEach(button => {
                    button.addEventListener('click', function() {
                        // Get the quiz name
                        const quizName = button.parentElement.parentElement.querySelector('input[name="quizName"]').value;
                        console.log('Deleting quiz:', quizName);
    
                        // Show a confirmation dialog before proceeding
                        const isConfirmed = confirm(`Are you sure you want to delete the quiz: "${quizName}"?`);
                        if (isConfirmed) {
                            const deleteURL = pathSegments.join('/') + `/quizzes/deleteQuiz-json/?quizName=${encodeURIComponent(quizName)}`;
        
                            console.log("delete: " + deleteURL)
                            // Send a DELETE request to the server
                            fetch(deleteURL, {
                                method: 'DELETE'
                            })
                            .then(response => {
                                if (!response.ok) {
                                    console.error('Response status:', response.status);
                                    throw new Error('Failed to delete quiz');
                                }
                                return response.json();
                            })
                            .then(data => {
                                console.log('Quiz deleted:', data);
                                // Reload the page to reflect the changes
                                window.location.reload();
                            })
                            .catch(error => {
                                console.error('Error deleting quiz:', error);
                            });
                        }
                    });
                }) 
            })
            .catch(error => {
                console.error('Error fetching quizzes:', error);
                document.querySelector('.quizzes').innerHTML = '<p>There was an error loading the quizzes. Please try again later.</p>';
            });            
            
        });

        function displayQuizzes(maxVisible) {
            const quizzes = document.getElementById('quizzes');
            const prevBtn = document.querySelector('.prev');
            const nextBtn = document.querySelector('.next');
            const visibleQuizzes = maxVisible; 
            let index = 0;

            // Adjust width dynamically based on the number of quizzes
            const totalQuizzes = quizzes.children.length;
            quizzes.style.width = `${(totalQuizzes / visibleQuizzes) * 100}%`;

            // For responsive
            if (visibleQuizzes == 1) {
                quizzes.style.gap = "0";
                document.querySelectorAll(".quiz").forEach(function(quiz) {
                    quiz.style.width = `70%`;
                    quiz.style.margin = "0 20px";
                });
            }

            // Checking if quiz description is empty
            document.querySelectorAll(".quiz-description").forEach(function(description){
                if (description.innerText== "") {
                    description.style.display = "none";
                }
            });

            var quizLinkMaxHeight = 0;
            document.querySelectorAll(".quizLink").forEach(function(quizLink) {
                var quizLinkHeight = quizLink.offsetHeight;  // Get the height of the current button
                if (quizLinkHeight > quizLinkMaxHeight) {
                    quizLinkMaxHeight = quizLinkHeight;  // Update the maxHeight if current button's height is greater
                }
            });
            document.querySelectorAll(".quizLink .img").forEach(function(quizLink) {
                if (quizLink.innerHTML == "") {
                    quizLink.parentElement.style.height = "80%";
                }
            });

            // Function to slide quizzes
            function updatequizzes() {
                const offset = -index * (document.getElementById("quizzesWrap").clientWidth / visibleQuizzes); // Calculate the offset
                quizzes.style.transform = `translateX(${offset}px)`;
            }
    
            // Event listener for next button
            nextBtn.addEventListener('click', () => {
                if (index < totalQuizzes - visibleQuizzes) {
                    index++;
                } else {
                    index = 0; // Loop back to the first page
                }
                updatequizzes();
            });
    
            // Event listener for previous button
            prevBtn.addEventListener('click', () => {
                if (index > 0) {
                    index--;
                } else {
                    index = totalQuizzes - visibleQuizzes; // Loop back to the last page
                }
                updatequizzes();
            });
    
            // Disable buttons if there are not enough quizzes
            if (totalQuizzes > visibleQuizzes) {
                nextBtn.style.display = 'block';
                prevBtn.style.display = 'block';
            }

            if (totalQuizzes == 1) {
                quizzes.style.display = "flex";
                quizzes.style.justifyContent = "center"
                quizzes.style.width = `100%`;
                document.querySelectorAll(".quiz").forEach(function(quiz) {
                    quiz.style.width = `60%`;
                });
            } else if (totalQuizzes < 3 && visibleQuizzes > 2) {
                quizzes.style.display = "flex";
                quizzes.style.justifyContent = "center"
                quizzes.style.width = `100%`;
                document.querySelectorAll(".quiz").forEach(function(quiz) {
                    quiz.style.width = `45%`;
                });
            }

            // Changing the admin buttons for responsive 
            document.querySelectorAll(".quiz").forEach(function(quiz) {
                if (quiz.offsetWidth < 300) {
                    document.querySelectorAll(".adminBtnWrap button:nth-child(1)").forEach(function(editButton) {
                        editButton.innerHTML = `<i class="fa-solid fa-pen"></i>`
                    });
                    document.querySelectorAll(".deleteButton").forEach(function(deleteButton) {
                        deleteButton.innerHTML = `<i class="fa-solid fa-trash"></i>`;
                    });
                }
            })
        }

    </script>
</html>
