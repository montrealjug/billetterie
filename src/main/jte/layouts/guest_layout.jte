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
    <p class="grow-1 text-white text-3xl font-bold text-shadow-lg">La Billetterie Devoxx4kids Québec</p>
    <a href="/admin/events">TMP: Go to Admin page</a>
</nav>
</header>
<main class="grow-1">
    ${content}
</main>
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
