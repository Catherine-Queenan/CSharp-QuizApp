document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logoutButton');

    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            fetch('/QuizApp/logout', {
                method: 'GET', // or 'POST' if you change your servlet
            })
            .then(response => {
                if (response.ok) {
                    // Parse JSON response
                    return response.json(); // Ensure to parse the response
                } else {
                    throw new Error('Logout failed: ' + response.statusText);
                }
            })
            .then(data => {
                // Handle the JSON data returned from the server
                if (data.status === 'success') {
                    // Redirect to the login page
                    window.location.href = '/QuizApp/login'; // Redirect to login page
                } else {
                    console.error('Logout response:', data.message);
                }
            })
            .catch(error => {
                console.error('Error during logout:', error);
            });
        });
    }
});