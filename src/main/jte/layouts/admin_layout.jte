@import gg.jte.Content

@param String title
@param Content content

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <!-- replace the tailwind tags with the following when tailwind build is activated -->
    <!-- link rel="stylesheet" href="/static/main.css" /-->
    <script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <style type="text/tailwindcss">
        @theme {
            --color-d4k-orange: #f0690a;
        }
    </style>
    <!-- favicons and co. -->
    <link rel="icon" type="image/png" href="/static/favicons/favicon-96x96.png" sizes="96x96" />
    <link rel="icon" type="image/svg+xml" href="/static/favicons/favicon.svg" />
    <link rel="shortcut icon" href="/static/favicons/favicon.ico" />
    <link rel="apple-touch-icon" sizes="180x180" href="/static/favicons/apple-touch-icon.png" />
    <link rel="manifest" href="/static/favicons/site.webmanifest" />
</head>
<body class="bg-gray-100 flex flex-col h-screen justify-between">
<header class="shadow-md p-4 bg-d4k-orange w-full flex justify-between items-center text-white">
    <img
        src="https://www.devoxx4kids.org/quebec/wp-content/uploads/sites/12/2014/06/D4K_QUEBEC_1000px.png"
        alt="Devoxx4Kids Québec logo"
        class="max-h-20 bg-white rounded-lg p-2 shadow-lg text-shadow-xs"
    />
    <p class="grow-1 text-3xl font-bold px-4">Billetterie Devoxx4Kids Québec (Admin)</p>
    <nav>
        <ul class="flex flex-wrap justify-end gap-4">
            <li>
                <a class="inline-block shadow-2xs font-semibold p-3 rounded-md border-2 border-white hover:bg-white hover:text-d4k-orange"
                   href="/admin/events">Manage Events</a>
            </li>
            <li>
                <a class="inline-block shadow-2xs font-semibold p-3 rounded-md border-2 border-white hover:bg-white hover:text-d4k-orange"
                   href="/admin/bookers">Manage Bookers</a>
            </li>
            <li>
                <a class="inline-block shadow-2xs font-semibold p-3 rounded-md border-2 border-white hover:bg-white hover:text-d4k-orange"
                    href="/">Main Site</a>
            </li>
            <li>
                <a class="inline-block shadow-2xs font-semibold p-3 rounded-md border-2 border-white hover:bg-white hover:text-d4k-orange"
                   href="/admin/logout">Logout</a>
            </li>
        </ul>
</nav>
</header>
<main class="grow-1 justify-start overflow-y-auto p-4 flex flex-col">
    ${content}
</main>
<footer class="bg-white p-4 flex justify-between text-gray-500 text-sm">
    <span>&copy;&nbsp;${java.time.LocalDate.now().getYear()}&nbsp;Devoxx4Kids Québec</span>
</footer>
</body>
<script type="text/javascript">
    document.addEventListener('DOMContentLoaded', function() {
        const pathname = window.location.pathname
        let navIndex = -1;
        if (pathname.startsWith("/admin/events")) {
            navIndex = 1;
        }
        else if (pathname.startsWith("/admin/bookers")) {
            navIndex = 2;
        }
        if (navIndex !== -1) {
            @raw
            const a = document.querySelector(`header > nav > ul > li:nth-child(${navIndex}) > a`);
            @endraw
            if (a) {
                a.classList.add("text-d4k-orange", "bg-white");
            }
        }
    });
</script>
</html>
