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
            padding: 20px;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }

        .category input {
            all: unset;
            width: 100%;
            /* height: 100%; */
            cursor: pointer;
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
                <div class="categories" id="categories">
                    <%= request.getAttribute("categoriesHtml") %> 
                </div>
            </div>
            <button class="btn next"><i class="fa-solid fa-chevron-right"></i></button>
        </div>

        <div class="adminWrap">
            <%= request.getAttribute("adminHtml") %> 
        </div>
    </div>
    
    <script>

        // ------ Making categories display able to slide ------
        
        const categories = document.getElementById('categories');
        const prevBtn = document.querySelector('.prev');
        const nextBtn = document.querySelector('.next');
        const totalCategories = categories.children.length;
        const visibleCategories = 3; // Display 3 at a time
        let index = 0;

        // Adjust width dynamically based on the number of categories
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

        // If there are less than three categories
        window.onload = function() {
            if (totalCategories < 3) {
                categories.style.display = "flex";
                categories.style.justifyContent = "center"
                categories.style.width = `${100}%`;
            }
        };

    </script>
</body>
<script src="scripts\logout.js"></script>
</html>