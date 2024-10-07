<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Quizzes</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>

        .wrap {
            padding: 50px;
        }

        .quizzesBtnWrap {
            margin-top: 20px;
            width: 85%;
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
            gap: 30px;
            overflow: hidden;
            transition: transform 0.4s ease-in-out;
        }

        .quiz {
            width: 31%;
            border: 0;
            border-radius: 15px;
            padding: 10px;
            padding-bottom: 15px;
            font-size: 25px;
            color: #0C1B33;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            transition-duration: 0.3s;
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

        .quiz form {
            width: 100%;
            height: 100%;
            padding: 20px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }

        .quiz input {
            all: unset;
            width: 100%;
            /* height: 100%; */
            text-align: center;
            padding: 20px 40px;
            box-sizing: border-box;
            cursor: pointer;
        }

        .quiz p {
            padding: 0 20px 10px 20px;
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

        /* Admin section */

        .adminBtnWrap {
            margin-top: 10px;
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
            transition-duration: 0.3s;
            cursor: pointer;
        }

        .adminBtnWrap button:hover {
            transform: scale(1.03);
            box-shadow: inset 5px 5px 5px rgba(1, 1, 1, 0.3);
        }

        .adminBtnWrap button:nth-child(1) {
            background-color: #D00000;
            color: rgb(244, 244, 244);
        }

        .adminBtnWrap button:nth-child(2) {
            background-color: #DCEED1;
        }

        .adminBtnWrap button:nth-child(2):hover {
            box-shadow: inset 5px 5px 5px #5a6a3e8c;
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

    <div class="wrap">
        <div class="title cherry-cream-soda">
            Available Quizzes
        </div>

        <div class="quizzesBtnWrap">
            <button class="btn prev"><i class="fa-solid fa-chevron-left"></i></button>
            <div id="quizzesWrap">
                <div class="quizzes" id="quizzes">

                    <!-- <%= request.getAttribute("quizzesHtml")%> -->
                    <div class="quiz">
                        <form method="post">
                            <input type="hidden" name="quizName" value="quizName">
                            <input type="submit" value="quizName">
                            <p class="quiz-description"><!-- Quiz description --></p>
                            <div class="img">
                                <!-- If no image, the img tag doesn't exist -->
                                <img src="pathToPic" alt="quizName" class="categoryImg">
                            </div>
                        </form>
                        <!-- adminBtnWrap doesn't display if user isn't admin -->
                        <div class="adminBtnWrap">    
                            <button type="button" onclick="window.location.href='deleteQuiz?quizName=testanswer'">Delete Quiz</button>
                            <button type="button" onclick="window.location.href='edit?quizName=testanswer'">Edit Quiz</button>
                        </div>
                    </div>
                    
                </div>
            </div>
            <button class="btn next"><i class="fa-solid fa-chevron-right"></i></button>
        </div>

        <div class="quizWrap" id="quizContainer">
            Loading quizzes...
        </div>
    </div>

    <script>

        document.addEventListener('DOMContentLoaded', function() {
            const currentPath = window.location.pathname;
            const pathSegments = currentPath.split('/');
            
            // Extract the category name from URL parameters
            const categoryName = pathSegments[2];

            // Extract the base path dynamically (remove last segment if it's quiz-related)
            pathSegments.pop(); 
            pathSegments.pop();            

            // Construct the new path dynamically
            const newPath = pathSegments.join('/') + `/quizzes-json/${categoryName}`;

            console.log(newPath)
            fetch(newPath, {
                method: 'GET',
                headers: {
                    'Accept': 'application/json'
                }
            })
            .then(response => {
                console.error('Response status:', response.status);
                if (!response.ok) {
                    throw new Error('Failed to fetch quizzes');
                }
                return response.json(); // Change this temporarily to text() instead of json()
            })
            .then(data => {
                console.log("Raw response data: ", data);
                // Try parsing it as JSON after confirming it's in JSON format
                try {
                    const parsedData = JSON.parse(data);
                    console.log("Parsed quizzes data: ", parsedData);
                } catch (err) {
                    console.error("Error parsing JSON: ", err);
                }
                // Render the quizzes...
            })
            .catch(error => {
                console.error('Error fetching quizzes:', error);
                document.getElementById('quizContainer').innerHTML = '<p>There was an error loading the quizzes. Please try again later.</p>';
            });


            // ------ Making quizzes display able to slide ------
            
            const quizzes = document.getElementById('quizzes');
            const prevBtn = document.querySelector('.prev');
            const nextBtn = document.querySelector('.next');
            const totalQuizzes = quizzes.children.length;
            const visibleQuizzes = 3; // Display 3 at a time
            let index = 0;
    
            // Adjust width dynamically based on the number of quizzes
            quizzes.style.width = `${(totalQuizzes / visibleQuizzes) * 100}%`;
    
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
            if (totalQuizzes <= visibleQuizzes) {
                nextBtn.style.display = 'none';
                prevBtn.style.display = 'none';
            }

            if (totalQuizzes < 3) {
                quizzes.style.display = "flex";
                quizzes.style.justifyContent = "center"
                quizzes.style.width = `${100}%`;
                document.querySelectorAll(".quiz").forEach(function(quiz) {
                    quiz.style.width = `${40}%`;
                });
            }
        });

        // document.addEventListener('DOMContentLoaded', function() {

        //     const currentPath = window.location.pathname;
        //     const pathSegments = currentPath.split('/');
        //     pathSegments.pop();
            
        //     const urlParams = new URLSearchParams(window.location.search);
        //     console.log(currentPath);
        //     const categoryName = urlParams.get('categoryName');
            
        //     // let newPath = pathSegments.join('/') + `/?categoryName=${categoryName}`;
        //         // let newPath =  `/quizzes/categoryName=${categoryName}`;
        //     let newPath = `/${categoryName}`;

        //     console.log(newPath)

        //     fetch(newPath, {
        //         method: 'GET',
        //         headers: {
        //             'Accept': 'application/json'
        //         }
        //     })
        //     .then(response => {
        //         if (!response.ok) {
        //             console.error('Response status:', response.status); // Log the error code
        //             throw new Error('Failed to fetch quizzes');
        //         }
        //         return response.json();
        //     })
        //     .then(data => {
        //         console.log("Fetched quizzes: ", data);
        //         // Further code to render the quizzes...
        //         const quizContainer = document.getElementById('quizzes');
        //         quizContainer.innerHTML = ''; // Clear previous content

        //         // Check if there are quizzes
        //         if (data.quizzes && data.quizzes.length > 0) {
        //             data.quizzes.forEach(quiz => {
        //                 const quizDiv = document.createElement('div');
        //                 quizDiv.className = 'quiz';

        //                 quizDiv.innerHTML = `
        //                     <h3>${quiz.title}</h3>
        //                     <p>${quiz.description}</p>
        //                     <a href="/quiz/${quiz.id}">Take this quiz</a>
        //                 `;

        //                 quizContainer.appendChild(quizDiv);
        //             });
        //         } else {
        //             quizContainer.innerHTML = '<p>No quizzes available for this category.</p>';
        //         }
        //     })
        //     .catch(error => {
        //         console.error('Error fetching quizzes:', error);
        //         document.getElementById('quizContainer').innerHTML = '<p>There was an error loading the quizzes. Please try again later.</p>';
        //     });

        //     // ------ Making quizzes display able to slide ------
            
        //     const quizzes = document.getElementById('quizzes');
        //     const prevBtn = document.querySelector('.prev');
        //     const nextBtn = document.querySelector('.next');
        //     const totalQuizzes = quizzes.children.length;
        //     const visibleQuizzes = 3; // Display 3 at a time
        //     let index = 0;
    
        //     // Adjust width dynamically based on the number of quizzes
        //     quizzes.style.width = `${(totalQuizzes / visibleQuizzes) * 100}%`;
    
        //     // Function to slide quizzes
        //     function updatequizzes() {
        //         const offset = -index * (document.getElementById("quizzesWrap").clientWidth / visibleQuizzes); // Calculate the offset
        //         quizzes.style.transform = `translateX(${offset}px)`;
        //     }
    
        //     // Event listener for next button
        //     nextBtn.addEventListener('click', () => {
        //         if (index < totalQuizzes - visibleQuizzes) {
        //             index++;
        //         } else {
        //             index = 0; // Loop back to the first page
        //         }
        //         updatequizzes();
        //     });
    
        //     // Event listener for previous button
        //     prevBtn.addEventListener('click', () => {
        //         if (index > 0) {
        //             index--;
        //         } else {
        //             index = totalQuizzes - visibleQuizzes; // Loop back to the last page
        //         }
        //         updatequizzes();
        //     });
    
        //     // Disable buttons if there are not enough quizzes
        //     if (totalQuizzes <= visibleQuizzes) {
        //         nextBtn.style.display = 'none';
        //         prevBtn.style.display = 'none';
        //     }

        //     if (totalQuizzes < 3) {
        //         quizzes.style.display = "flex";
        //         quizzes.style.justifyContent = "center"
        //         quizzes.style.width = `${100}%`;
        //         document.querySelectorAll(".quiz").forEach(function(quiz) {
        //             quiz.style.width = `${40}%`;
        //         });
        //     }
        // });

    </script>
</body>
<script src="scripts\logout.js"></script>
</html>
