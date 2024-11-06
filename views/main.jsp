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

        body {
            overflow-x: hidden;
        }
        
        #wrap {
            height: unset !important;
            padding: 80px 0 !important;
            justify-content: center;
            position: unset !important;
            overflow-y: scroll;
            -ms-overflow-style: none;  /* Internet Explorer 10+ */
            scrollbar-width: none;  /* Firefox */
            -webkit-scrollbar: none;
        }

        .categoryBtnWrap,
        .modSessionWrap {
            width: 85%;
            display: flex;
            justify-content: center;
            align-items: center;
        }
        
        #categoryWrap,
        #modSessionContWrap {
            width: 90%;
            /* padding: 0 100px; */
            margin: 0 auto;
            position: relative;
            overflow: hidden; /* Hides overflow categories */
        }

        .categories,
        .modSessionCont {
            width: max-content; /* Dynamically adjust width */
            display: flex;
            justify-content: center;
            gap: 30px;
            overflow: hidden;
            transition: transform 0.4s ease-in-out;
        }

        .emptyMsg {
            width: 100%;
            padding: 15px 10% 0 10%;
            font-size: 18px;
            text-align: center;
            margin-bottom: 20px;
        }

        .category {
            width: 30%; /* Shows 3 categories at a time */
            padding: 20px;
            text-align: center;
            border-radius: 15px;
            transition-duration: 0.3s;
            font-size: 20px;
            cursor: pointer;
        }

        .modSession {
            all: unset;
            width: 30%; /* Shows 3 categories at a time */
            padding: 20px;
            text-align: center;
            border-radius: 15px;
            transition-duration: 0.3s;
            font-size: 16px;
            cursor: pointer;
        }
        
        .category:hover,
        .modSession:hover {
            transform: scale(0.99);
            box-shadow: inset 5px 5px 10px rgba(14, 1, 47, 0.7);
        }

        /* Category colors */
        .category:nth-child(1),
        .category:nth-child(5n+1),
        .modSession:nth-child(1),
        .modSession:nth-child(5n+1) {
            background-color: #FF4B32;
            color: #0C1B33; 
        }

        .category:nth-child(2),
        .category:nth-child(5n+2),
        .modSession:nth-child(2),
        .modSession:nth-child(5n+2) {
            background-color: #FFB20F;
            color: #0C1B33; 
        }

        .category:nth-child(3),
        .category:nth-child(5n+3),
        .modSession:nth-child(3),
        .modSession:nth-child(5n+3) {
            background-color: #FFE548;
            color: #0C1B33; 
        }

        .category:nth-child(4),
        .category:nth-child(5n+4),
        .modSession:nth-child(4),
        .modSession:nth-child(5n+4) {
            background-color: #D7E8BA;
            color: #0C1B33;
        }

        .category:nth-child(5),
        .category:nth-child(5n+5),
        .modSession:nth-child(5),
        .modSession:nth-child(5n+5) {
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
            display: none;
        }

        .btn:hover {
            color: #45425A;
        }

        /* admin section */
        .adminWrap {
            margin-top: 60px;
            transform: scale(0.9);
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
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

        /* Moderated sessions */
        .moderatedTitle {
            width: 80%;
            margin-bottom: 20px;
            font-size: 30px;
            text-align: center;
        }

        .moderatedSessions {
            margin-bottom: 60px;
            width: 100%;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
        }

    </style>
</head>
<body>
    <header>
        <form action="logout">
            <button id="logoutButton" class="logoutBtn" type="Submit">Log Out</button>
        </form>
    </header>

    <div class="wrap" id="wrap">
        <!-- New Div for Moderated Sessions -->
        <div class="moderatedSessions" id="moderatedSessionsContainer">
            <h2 class="moderatedTitle cherry-cream-soda">
                Moderated Sessions
            </h2>
            <div class="modSessionWrap">
                <button class="btn modPrev"><i class="fa-solid fa-chevron-left"></i></button>
                <div id="modSessionContWrap">
                    <div id="modSessionsCont" class="modSessionCont">
                        <!-- Moderated sessions will be injected here by JavaScript -->
                    </div>
                </div>
                <button class="btn modNext"><i class="fa-solid fa-chevron-right"></i></button>
            </div>
        </div>
        
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
    //     // Function to check session status
    // function checkSession() {
    //     return fetch('/QuizApp/home/session-status', {
    //         method: 'GET',
    //     })
    //     .then(response => {
    //         if (!response.ok) {
    //             throw new Error('Failed to check session status');
    //         }
    //         return response.json();
    //     })
    //     .then(data => {
    //         if (!data.loggedIn) {
    //             // User is not logged in, redirect to login page
    //             window.location.href = '/QuizApp/login'; 
    //             return false; // User is not logged in
    //         }
    //         return true; // User is logged in
    //     })
    //     .catch(error => {
    //         console.error('Error checking session status:', error);
    //         return false; // Assume not logged in on error
    //     });
    // } 

        const currentSessionPath = window.location.pathname;
        const pathSessionSegments = currentSessionPath.split('/');
        pathSessionSegments.pop();
        let sessionPath = pathSessionSegments.join('/') + '/getActiveSessions?action=getActiveSessions';

        // Fetch current moderated sessions
        fetch(sessionPath, {
            method: 'GET',
            headers: {
                'Accept': 'application/json'
            }
        })
        .then(response => {
            // Check if the response is okay and convert to JSON
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            return response.json();
        })
        .then(data => {
            const modSessionsContainer = document.getElementById('modSessionsCont');
            modSessionsContainer.innerHTML = ''; // Clear previous content

            // Check if there are any moderated sessions
            if (!data.sessions || data.sessions.length === 0) {
                // If no sessions, display a message
                modSessionsContainer.innerHTML = '<div class="emptyMsg">No moderated sessions available.</div>';
            } else {
                // If there are sessions, display them
                data.sessions.forEach(modSession => {
                    const modSessionBtn = document.createElement('button');
                    modSessionBtn.className = 'modSession';
                    modSessionBtn.innerText = `Join ${modSession.moderator}'s session`;
                    modSessionBtn.onclick = function() {
                        joinSession(modSession.sessionId, modSession.quizName);
                    };
                    modSessionsContainer.appendChild(modSessionBtn);
                });
            }
        })
        .catch(error => {
            console.error('Error fetching moderated sessions:', error);
            const modSessionsContainer = document.getElementById('modSessionsCont');
            modSessionsContainer.innerHTML = '<div>Error fetching moderated sessions. Please try again later.</div>';
        });

        // Function to join a session
        function joinSession(modSessionId, quizName) {
            const currentSessPath = window.location.pathname;
            const pathSessSegments = currentSessPath.split('/');
            pathSessSegments.pop();
            let sessSessionPath = pathSessSegments.join('/') + `/moderateMode?sessionId=${encodeURIComponent(modSessionId)}&quizName=${encodeURIComponent(quizName)}`;
            window.location.href = sessSessionPath;
        }

        const currentPath = window.location.pathname;
        const pathSegments = currentPath.split('/');
        pathSegments.pop();
        let newPath = pathSegments.join('/') + '/home-json';
        console.log(newPath)
        
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

            console.log(data.role)
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
                document.querySelector(".categoryBtnWrap").innerHTML = '<p class="emptyMsg">No categories available</p>';
                document.getElementById("categories").style.width = "100%";
                return;
            }

            data.categories.forEach(category => {
                const categoryDiv = document.createElement('div');
                categoryDiv.className = 'category';

                let mediaHtml = '';
                // console.log(category.media.media_file_path)
                if (category.media && category.media.media_file_path) {
                    console.log(category.media.media_file_path)
                    mediaHtml = `<img src="${category.media.media_file_path}" alt="${category.name}" class="categoryImg">`;
                }

                categoryDiv.innerHTML = `
                    <a class="categoryLink" href="quizzes/${encodeURIComponent(category.name)}">
                        <div class="categoryName">${category.name}</div>
                        <div class="img">${mediaHtml}</div>
                    </a>
                `;

                // Append category div to the container
                categoriesContainer.appendChild(categoryDiv);
            });

            // Set up the sliding mechanism
            if (window.innerWidth < 650) {
                displayCategories(1);
                displayModSessions(2);
            } else if (window.innerWidth < 1000) {
                displayCategories(2);
                displayModSessions(3);
            } else {
                displayCategories(3);
                displayModSessions(3);
            }
        })
        .catch(error => {
            console.error('Error fetching categories:', error);
            document.getElementById('categories').innerHTML = '<p class="emptyMsg">There was an error loading the categories. Please try again later.</p>';
            document.getElementById("categories").style.width = "100%";
        });
    });

    function displayCategories(maxVisible) {
        const categories = document.getElementById('categories');
        const prevBtn = document.querySelector('.prev');
        const nextBtn = document.querySelector('.next');
        const visibleCategories = maxVisible; 
        let index = 0;

        // Adjust width dynamically based on the number of quizzes
        const totalCategories = categories.children.length;
        categories.style.width = `${(totalCategories / visibleCategories) * 100}%`;

        // For responsive
        if (visibleCategories == 1) {
            categories.style.gap = "0";
            document.querySelectorAll(".category").forEach(function(category) {
                category.style.width = `70%`;
                category.style.margin = "0 20px";
            });
        }

        var categoryLinkMaxHeight = 0;
        document.querySelectorAll(".categoryLink").forEach(function(categoryLink) {
            if (categoryLink.querySelector(".img").innerHTML == "") {
                categoryLink.height = "100%";
            }
        });

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

        // Disable buttons if there are not enough quizzes
        if (totalCategories > visibleCategories) {
            nextBtn.style.display = 'block';
            prevBtn.style.display = 'block';
        }

        if (totalCategories == 1) {
            categories.style.display = "flex";
            categories.style.justifyContent = "center"
            categories.style.width = `100%`;
            if (window.innerWidth < 500) {
                document.querySelectorAll(".category").forEach(function(category) {
                    category.style.width = `80%`;
                });
            } else {
                document.querySelectorAll(".category").forEach(function(category) {
                    category.style.width = `60%`;
                });
            }
        } else if (totalCategories < 3 && visibleCategories > 2) {
            categories.style.display = "flex";
            categories.style.justifyContent = "center"
            categories.style.width = `100%`;
            document.querySelectorAll(".category").forEach(function(category) {
                category.style.width = `45%`;
            });
        }
    }

    function displayModSessions(maxVisible) {
        const modSessions = document.getElementById('modSessionsCont');
        const prevBtn = document.querySelector('.modPrev');
        const nextBtn = document.querySelector('.modNext');
        const visibleModSessions = maxVisible; 
        let index = 0;

        // Adjust width dynamically based on the number of quizzes
        const totalModSessions = modSessions.children.length;
        modSessions.style.width = `${(totalModSessions / visibleModSessions) * 100}%`;

        // For responsive
        if (visibleModSessions == 2) {
            modSessions.style.gap = "0";
            document.querySelectorAll(".modSession").forEach(function(modSession) {
                modSession.style.width = `40%`;
                modSession.style.margin = "0 10px";
            });
        }

        var modSessionMaxHeight = 0;
        document.querySelectorAll(".modSession").forEach(function(modSession) {
            if (modSession.offsetHeight > modSessionMaxHeight) {
                modSessionMaxHeight = modSession.offsetHeight;
            } else {
                modSession.style.height = modSessionMaxHeight;
            }
        });

        // Function to slide categories
        function updateModSessions() {
            const offset = -index * (document.getElementById("modSessionContWrap").clientWidth / visibleModSessions); // Calculate the offset
            modSessions.style.transform = `translateX(${offset}px)`;
        }

        // Event listener for next button
        nextBtn.addEventListener('click', () => {
            if (index < totalModSessions - visibleModSessions) {
                index++;
            } else {
                index = 0; // Loop back to the first page
            }
            updateModSessions();
        });

        // Event listener for previous button
        prevBtn.addEventListener('click', () => {
            if (index > 0) {
                index--;
            } else {
                index = totalModSessions - visibleModSessions; // Loop back to the last page
            }
            updateCategories();
        });

        // Disable buttons if there are not enough quizzes
        if (totalModSessions > visibleModSessions) {
            nextBtn.style.display = 'block';
            prevBtn.style.display = 'block';
        }

        if (totalModSessions == 1) {
            modSessions.style.display = "flex";
            modSessions.style.justifyContent = "center"
            modSessions.style.width = `100%`;
            if (window.innerWidth < 500) {
                document.querySelectorAll(".modSession").forEach(function(modSession) {
                    modSession.style.width = `60%`;
                });
            } else {
                document.querySelectorAll(".modSession").forEach(function(modSession) {
                    modSession.style.width = `40%`;
                });
            }
        } else if (totalModSessions < 3 && visibleModSessions > 2) {
            modSessions.style.display = "flex";
            modSessions.style.justifyContent = "center"
            modSessions.style.width = `100%`;
            document.querySelectorAll(".modSession").forEach(function(modSession) {
                modSession.style.width = `45%`;
            });
        }
    }
//});

</script>
</html>