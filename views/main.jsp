<!DOCTYPE html>
<html>
<head>
    <title>Main Page</title>
</head>
<body>
    <h1>Categories</h1>
    <%= request.getAttribute("categoriesHtml") %>

    <h1>Admin dashboard</h1>
    <%= request.getAttribute("adminHtml") %>
</body>
</html>
