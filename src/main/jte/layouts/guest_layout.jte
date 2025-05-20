@import gg.jte.Content

@param String title
@param Content content

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>${title}</title>
    <link rel="stylesheet" href="/static/main.css" />
    <%--<script src="https://cdn.jsdelivr.net/npm/@tailwindcss/browser@4"></script>
    <style type="text/tailwindcss">
        @theme {
            --color-d4k-orange: #f0690a;
        }
        @layer base {
            ol{
                @apply list-decimal mt-2;
            }
            li {
                @apply ml-6;
            }
            h3{
                @apply font-bold text-base mb-2;
            }
        }
    </style>--%>
    <!-- favicons and co. -->
    <link rel="icon" type="image/png" href="/static/favicons/favicon-96x96.png" sizes="96x96" />
    <link rel="icon" type="image/svg+xml" href="/static/favicons/favicon.svg" />
    <link rel="shortcut icon" href="/static/favicons/favicon.ico" />
    <link rel="apple-touch-icon" sizes="180x180" href="/static/favicons/apple-touch-icon.png" />
    <link rel="manifest" href="/static/favicons/site.webmanifest" />
</head>
<body class="bg-gray-100 flex flex-col h-screen max-h-screen justify-end-safe">
<header class="shadow-md p-2 pb-0 md:p-4 bg-d4k-orange w-full flex flex-col md:flex-row justify-between items-start text-white md:items-center gap-2">
    <img
        src="https://www.devoxx4kids.org/quebec/wp-content/uploads/sites/12/2014/06/D4K_QUEBEC_1000px.png"
        alt="Devoxx4Kids Québec logo"
        class="max-h-20 bg-white rounded-lg p-1 shadow-lg object-scale-down w-full md:w-auto md:p-2"
    />
    <p class="font-bold py-2 md:p-4 text-sm text-shadow-xs md:grow-1 md:text-2xl lg:text-3xl">Billetterie Devoxx4Kids Québec</p>
    <!-- TODO: remove admin link -->
    <a class="inline-block absolute top-24 right-2 md:top-8 md:p-2 text-xs shadow-2xs font-semibold p-1 rounded-md border-2 border-white hover:bg-white hover:text-d4k-orange md:text-xl"
       href="/admin/events">Admin</a>
</header>
<main class="grow-1 flex flex-col justify-start md:p-2 lg:p-4 overflow-hidden">
    ${content}
</main>
<footer class="bg-white p-2 flex justify-between items-center text-gray-500 text-xs md:p-4 md:text-sm">
    <span>&copy;&nbsp;${java.time.LocalDate.now().getYear()}&nbsp;Devoxx4Kids Québec</span>
    <ul class="flex flex-col text-right">
        <li><a href="#">Privacy Policy</a></li>
        <li><a href="#">Terms and Conditions</a></li>
    </ul>
</footer>
</body>
</html>
