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

<nav class="shadow-md p-4 bg-[#f78c40] w-full absolute flex justify-between">
    <div>
        DEVOXX4KIDS Admin
    </div>
    <ul class="flex justify-center space-x-10">
        <li><a href="/admin/events" class="hover:text-gray-700">Manage Event</a></li>
        <li><a href="/admin/bookers" class="hover:text-gray-700">Manage Booker</a></li>
    </ul>
    <div>
        <a href="/" class="hover:text-gray-700">Go to main website</a>
    </div>
</nav>

<div>
    ${content}
</div>

</body>
</html>
