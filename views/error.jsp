<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Unauthorized Access</title>
    <link rel="stylesheet" href="public/css/reset.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.6.0/css/all.min.css" integrity="sha512-Kc323vGBEqzTmouAECnVceyQqyqdsSiqLQISBL29aUW4U/M7pSPA/gEUZQqv1cwx4OnYxTxve5UMg5GT6L4JJg==" crossorigin="anonymous" referrerpolicy="no-referrer" />
    <style>

        .wrap {
            height: 100vh !important;
        }
        
        .fa-triangle-exclamation {
            font-size: 100px;
            color: #FF4B3E;
            margin-bottom: 20px;
        }

        .message {
            width: 80%;
            text-align: center;
            font-size: 40px;
            margin-bottom: 50px;
        }

        .redirectHome {
            padding: 20px 40px;
            border-radius: 15px;
            border: 0;
            margin-right: 10px;
            font-size: 22px;
            background-color: #99c252;
            transition-duration: 0.3s;
            cursor: pointer;
        }

        .redirectHome:hover {
            transform: scale(1.05);
            box-shadow: 5px 5px 5px rgba(1, 1, 1, 0.3);
        }

    </style>
</head>
<body>
    <div class="wrap">
        <i class="fa-solid fa-triangle-exclamation"></i>
        <div class="message cherry-cream-soda" id="message"></div>
        <button type="button" id="redirectHome" class="redirectHome">Go Back to Home</button>
    </div>
</body>
<script>
    const urlParams = new URLSearchParams(window.location.search);
    const errorMessage = urlParams.get('errorMessage');

    document.addEventListener('DOMContentLoaded', () => { 

        let rediretPath;

        if (errorMessage) {
            document.querySelector('.message').textContent = decodeURIComponent(errorMessage);
        }

        if(errorMessage.includes('Invalid')) {
            redirectPath = 'login';
        } else {
            redirectPath = 'home';
        }

        document.getElementById('redirectHome').addEventListener('click', () => {
            console.log('Redirecting to: index.html');
            if (redirectPath) {
                console.log('Redirecting to:', redirectPath); // Debugging to check if the click is firing
                window.location.href = redirectPath;
            } else {
                console.error('Redirect path is not set.');
            }
        });
    });
</script>
</html>
