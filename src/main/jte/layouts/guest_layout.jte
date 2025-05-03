@import gg.jte.Content

@param String title
@param Content content

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">

<nav class="absolute shadow-md p-4 bg-[#f78c40] w-full flex justify-between">
    <div>DEVOXX4KIDS LOGO</div>
    <div><a href="/admin/events">TMP: Go to Admin page</a></div>
</nav>

<div>
    ${content}
</div>

<footer>
    <div>
        <ul class="flex space-x-10 justify-center">
            <li><a href="#">Privacy Policy</a></li>
            <li><a href="#">Terms and Conditions</a></li>
        </ul>
    </div>
</footer>
</body>

</html>
