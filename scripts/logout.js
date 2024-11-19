document.addEventListener('DOMContentLoaded', () => {
    const logoutButton = document.getElementById('logoutButton');

    const currentSessionPath = window.location.pathname;
    const pathSessionSegments = currentSessionPath.split('/');
    console.log("BEFORE " , pathSessionSegments)
    
    for (let i = pathSessionSegments.length; i > 2; i--)  {
        pathSessionSegments.pop();
    }    

    console.log("TRIMMED ", pathSessionSegments)

    let logoutPath = pathSessionSegments.join('/') + '/logout';
    let loginPath = pathSessionSegments.join('/') + '/login';
    
    if (logoutButton) {
        logoutButton.addEventListener('click', () => {
            fetch(logoutPath, {
                method: 'GET', // or 'POST' if you change your servlet
            })
            .then(response => {
                console.log("response ", response)
                if (response.ok) {
                    // Parse JSON response
                    return response.json(); // Ensure to parse the response
                } else {
                    throw new Error('Logout failed: ' + response.statusText);
                }
            })
            .then(data => {
                // Handle the JSON data returned from the server
                if (data.status == 'success') {
                    // Redirect to the login page
                    window.location.href = loginPath; // Redirect to login page
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