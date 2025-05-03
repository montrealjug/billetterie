@param org.montrealjug.billetterie.ui.PresentationBooker booker = null
@param String errorMessage = null

@template.layouts.admin_layout(
title = "${booker != null ? \"Add\" : \"Modify\"} Booker",
content = @`

<div class="min-h-screen flex flex-col items-center justify-center">
    <a href="/admin/bookers" class="text-blue text-gray w-full max-w-md">&lt;&lt;&lt; Back</a>
    <div class="bg-white p-8 rounded shadow-md w-full max-w-md">
        @if(errorMessage != null)
            <h2 class="text-red-500">${errorMessage}</h2>
        @else
        <h1 class="text-2xl font-bold text-center mb-6">${booker != null ? "Update" : "Add"} Booker</h1>
        <form action="${"/admin/bookers" + (booker != null ? "/" + booker.email() : "")}" method="POST">
            @if(booker != null)
                <div>
                    <label for="email" class="block text-gray-700 font-medium mb-2">Booker email</label>
                    <input type="email" id="email" name="email" disabled value="${booker.email()}"
                           class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none bg-gray-300"
                    >
                </div>
            @else
                <div>
                    <label for="email" class="block text-gray-700 font-medium mb-2">Booker email</label>
                    <input type="email" id="email" name="email" required value=""
                           class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none"
                    >
                </div>

            @endif

            <div>
                <label for="firstName" class="block text-gray-700 font-medium mb-2">First name</label>
                <input type="text" id="firstName" name="firstName" required
                       value="${booker != null ? booker.firstName() : ""}"
                       class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none"
                >
            </div>

            <div>
                <label for="lastName" class="block text-gray-700 font-medium mb-2">Last name</label>
                <input type="text" id="lastName" name="lastName" required
                       value="${booker != null ? booker.lastName() : ""}"
                       class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none"
                >

            </div>

            <div class="text-center">
                <button type="submit"
                        class="bg-blue-500 text-white font-medium px-4 py-2 rounded-md hover:bg-blue-600 focus:outline-none focus:ring focus:ring-blue-300">
                    ${booker != null ? "Update" : "Add"} booker
                </button>
            </div>
        </form>
        @endif
    </div>

</div>
`)

