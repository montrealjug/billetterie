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
    <!-- favicons and co. -->
    <link rel="icon" type="image/png" href="/static/favicons/favicon-96x96.png" sizes="96x96" />
    <link rel="icon" type="image/svg+xml" href="/static/favicons/favicon.svg" />
    <link rel="shortcut icon" href="/static/favicons/favicon.ico" />
    <link rel="apple-touch-icon" sizes="180x180" href="/static/favicons/apple-touch-icon.png" />
    <link rel="manifest" href="/static/favicons/site.webmanifest" />
</head>
<body class="bg-gray-100 flex flex-col min-h-screen justify-between">
<header>
<nav class="shadow-md p-4 bg-[#f78c40] w-full flex justify-between items-center">
    <img
        src="https://www.devoxx4kids.org/quebec/wp-content/uploads/sites/12/2014/06/D4K_QUEBEC_1000px.png"
        alt="Devoxx4Kids Québec logo"
        class="max-h-24 bg-white rounded-lg p-2 shadow-lg"
    />
    <p class="grow-1 text-white text-xl font-bold text-shadow-lg">La Billetterie Devoxx4kids Québec (Admin)</p>
    <ul class="flex justify-center space-x-10">
        <li><a href="/admin/events" class="hover:text-gray-700">Manage Event</a></li>
        <li><a href="/admin/bookers" class="hover:text-gray-700">Manage Booker</a></li>
    </ul>
    <a href="/" class="hover:text-gray-700">Go to main website</a>
    <a href="/admin/logout" class="hover:text-gray-700">Logout</a>
</nav>
</header>
<main class="grow-1">
    ${content}
</main>
<footer></footer>
</body>
</html>
