<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Main Page</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>
        * { font-weight: 800; box-sizing: border-box; }

        .wrap {
            padding: 30px 0;
        }

        .categoryBtnWrap {
            width: 85%;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        
        #categoryWrap {
            width: 90%;
            /* padding: 0 100px; */
            margin: 0 auto;
            position: relative;
            overflow: hidden; /* Hides overflow categories */
        }

        .categories {
            width: max-content; /* Dynamically adjust width */
            display: flex;
            gap: 30px;
            overflow: hidden;
            transition: transform 0.4s ease-in-out;
        }

        .category {
            width: 30%; /* Shows 3 categories at a time */
            padding: 20px;
            text-align: center;
            border-radius: 15px;
            transition-duration: 0.3s;
            font-size: 20px;
        }

        .category:hover {
            transform: scale(0.99);
            box-shadow: inset 5px 5px 10px rgba(14, 1, 47, 0.7);
        }

        /* Category colors */
        .category:nth-child(1),
        .category:nth-child(5n+1) {
            background-color: #FF4B32;
            color: #0C1B33; 
        }

        .category:nth-child(2),
        .category:nth-child(5n+2) {
            background-color: #FFB20F;
            color: #0C1B33; 
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
            background-color: #6e6ba6;
            color: #0C1B33; 
        }

        .categoryLink {
            all: unset;
            width: 100%;
            height: 100%;
            /* padding: 20px; */
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            cursor: pointer;
        }

        .categoryName {
            width: 100%;
            /* cursor: pointer; */
        }
        
        /* Category img */
        .category img {
            margin-top: 20px;
            border-radius: 10px;
            max-width: 90%;
            width: 100%;
            max-height: 200px;
            height: 100%;
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

        /* admin section */
        .adminWrap {
            margin-top: 60px;
            transform: scale(0.9);
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

        <div class="categoryBtnWrap">
            <button class="btn prev"><i class="fa-solid fa-chevron-left"></i></button>
            <div id="categoryWrap">
                <div class="categories" id="categories"></div>
            </div>
            <button class="btn next"><i class="fa-solid fa-chevron-right"></i></button>
        </div>
        <div class="categoryContent"></div>

        <div class="adminWrap" id="adminDashboard"></div>
    </div>

</body>
<script src="scripts\logout.js"></script>
<script>

    document.addEventListener('DOMContentLoaded', function() {
        const categories = document.getElementById('categories');
        const prevBtn = document.querySelector('.prev');
        const nextBtn = document.querySelector('.next');
        const visibleCategories = 3; // Display 3 at a time
        let index = 0;

        const currentPath = window.location.pathname;
        const pathSegments = currentPath.split('/');
        pathSegments.pop();
        let newPath = pathSegments.join('/') + '/home-json';

        // Fetch categories and render them dynamically
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
            const categoriesContainer = document.getElementById('categories');
            const adminDashboard = document.getElementById('adminDashboard');

            categoriesContainer.innerHTML = '';
            adminDashboard.innerHTML = '';

            // If user is an admin, show admin dashboard
            if (data.role === 'admin') {
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
                }

                categoryDiv.innerHTML = `
                    <a class="categoryLink" href="/quizzes-json?categoryName=${encodeURIComponent(category.categoryName)}">
                        <div class="categoryName">${category.categoryName}</div>
                        <div class="img">${mediaHtml}</div>
                    </a>
                `;

                // Append category div to the container
                categoriesContainer.appendChild(categoryDiv);
            });

            // Set up the sliding mechanism
            const totalCategories = categories.children.length;

            categories.style.width = `${(totalCategories / visibleCategories) * 100}%`;

            // Function to slide categories
            function updateCategories() {
                const offset = -index * (document.getElementById("categoryWrap").clientWidth / visibleCategories); // Calculate the offset
                categories.style.transform = `translateX(${offset}px)`;
            }

            // Event listener for next button
            nextBtn.addEventListener('click', () => {
                if (index < totalCategories - visibleCategories) {
                    index++;
                } else {
                    index = 0; // Loop back to the first page
                }
                updateCategories();
            });

            // Event listener for previous button
            prevBtn.addEventListener('click', () => {
                if (index > 0) {
                    index--;
                } else {
                    index = totalCategories - visibleCategories; // Loop back to the last page
                }
                updateCategories();
            });

            // Disable buttons if there are not enough categories
            if (totalCategories <= visibleCategories) {
                nextBtn.style.display = 'none';
                prevBtn.style.display = 'none';
            }

            // Adjust for fewer categories on load
            if (totalCategories < 3) {
                categories.style.display = "flex";
                categories.style.justifyContent = "center"
                categories.style.width = `100%`;
                document.querySelectorAll(".category").forEach(function(category) {
                    category.style.width = `40%`;
                });
            }
        })
        .catch(error => {
            console.error('Error fetching categories:', error);
            categoriesContainer.innerHTML = '<p>There was an error loading the categories. Please try again later.</p>';
        });
    });


</script>
</html>