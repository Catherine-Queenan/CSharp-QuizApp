<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Main Page</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <style>
        
        * { font-weight: 800; }
	
        .categories {
            width: 70%;
            padding: 50px;
            display: flex;
            justify-content: center;
            align-items: flex-start;
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

        .adminWrap {
            margin-top: 20px;
        }

        .admin {
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .admin button {
            all: unset;
            margin-top: 10px;
            border-radius: 15px;
            padding: 20px 40px;
            font-size: 22px;
            background-color: #D7E8BA;
            color: #0C1B33;
            cursor: pointer;
            transition-duration: 0.3s;
        }

        .admin button:hover {
            transform: scale(1.05);
            box-shadow: 5px 5px 10px rgb(14, 1, 47);
        }

    </style>
</head>
<body>
    <header>
        <form action="logout">
            <button id="logoutButton" class="logoutBtn" type="Submit">Log Out</button>
        </form>
    </header>

    <div class="wrap">
        <div class="title cherry-cream-soda">
            Categories
        </div>
        <div class="categories" id="categoriesContainer">
        </div>
        <div class="adminWrap" id="adminDashboard">
        </div>
    </div>
<script src="scripts\logout.js"></script>
<script>

const currentPath = window.location.pathname;
const pathSegments = currentPath.split('/');

// Fetch categories and render them dynamically
document.addEventListener('DOMContentLoaded', function() {
    console.log('Fetching categories...');
    pathSegments.pop();
    let newPath = pathSegments.join('/') + '/home-json';
    // Fetch categories and other user data
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
    })
    .then(data => {
        console.log('Fetched categories:', data);
        const categoriesContainer = document.getElementById('categoriesContainer');
        const adminDashboard = document.getElementById('adminDashboard');

        categoriesContainer.innerHTML = '';
        adminDashboard.innerHTML = '';

        // If user is an admin, show admin dashboard
        if (data.role === 'admin') {
            const adminDashboard = document.getElementById('adminDashboard');
            adminDashboard.innerHTML = `
                <div class="title cherry-cream-soda">Admin Dashboard</div>
                <div class="admin">
                    <button class="newQuiz" onclick="window.location.href='createQuiz'">Create a new Quiz</button>
                </div>
            `;
        }

        // Render categories dynamically
        if (data.categories.length === 0) {
            categoriesContainer.innerHTML = '<p>No categories available</p>';
            return;
        }
        data.categories.forEach(category => {
        const categoryDiv = document.createElement('div');
        categoryDiv.className = 'category';

        let mediaHtml = '';
        if (category.media && category.media.mediaFilePath) {
            mediaHtml = `<img src="${category.media.mediaFilePath}" alt="${category.categoryName}" class="categoryImg">`;
        } else {
            mediaHtml = '<div class="categoryImg"></div>';
        }

        // Add a button for each category that will fetch quizzes RESTfully
        categoryDiv.innerHTML = `
            <div class="categoryContent">
                <div class="categoryName">${category.categoryName}</div>
                <div class="img">${mediaHtml}</div>
                <button class="categoryButton">View Quizzes</button>
            </div>
        `;

            // Handle category button click to fetch quizzes
            categoryDiv.querySelector('.categoryButton').addEventListener('click', () => {
                // Fetch quizzes for the selected category
                fetch(`/quizzes?categoryName=${encodeURIComponent(category.categoryName)}`, {
                    method: 'GET',
                    headers: {
                        'Accept': 'application/json'
                    }
                })
                .then(response => response.json())
                .then(quizzes => {
                    // Display the quizzes (this part can be customized based on your requirements)
                    console.log('Fetched quizzes for category:', category.categoryName);
                    console.log(quizzes);

                    // For example, display the quiz titles dynamically:
                    let quizzesHtml = '<div class="quizList">';
                    quizzes.forEach(quiz => {
                        quizzesHtml += `<div class="quizItem">${quiz.title}</div>`;
                    });
                    quizzesHtml += '</div>';
                    categoryDiv.querySelector('.categoryContent').innerHTML += quizzesHtml;
                })
                .catch(error => {
                    console.error('Error fetching quizzes:', error);
                    categoryDiv.querySelector('.categoryContent').innerHTML += '<p>Error loading quizzes. Please try again later.</p>';
                });
            });

            categoriesContainer.appendChild(categoryDiv);
        });
    })
    .catch(error => {
        console.error('Error fetching categories:', error);
        categoriesContainer.innerHTML = '<p>There was an error loading the categories. Please try again later.</p>';
    });
});

</script>

</body>
</html>