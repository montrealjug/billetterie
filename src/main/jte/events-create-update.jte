@import org.montrealjug.billetterie.ui.PresentationEvent

@param PresentationEvent event

@param String errorMessage

@template.layouts.admin_layout(
title = "${event != null ? \"Create\" : \"Update\"} Event" ,
content = @`
<div class="flex flex-col items-center justify-center">
    <a href="/admin/events" class="text-blue text-gray w-full max-w-md">&lt;&lt;&lt; Back</a>
    <div class="bg-white p-8 rounded shadow-md w-full max-w-md">

        @if(errorMessage != null)
            <div class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                <p class="font-semibold">Error</p>
                <p>${errorMessage}</p>
            </div>
        @endif
        @if(errorMessage == null)
        <h1 class="text-2xl font-bold text-center mb-6">${event != null ? "Update" : "Create"} Event</h1>
        <form action="/admin/events${event != null ? "/" + event.id() : ""}" method="POST">
            <div class="mb-4 flex items-center">
                @if(event==null )
                    <input type="checkbox" id="active" name="active" disabled
                           class="w-5 h-5 text-blue-500 border-gray-300 rounded focus:ring focus:ring-blue-300">
                @elseif(event.active())
                    <input type="checkbox" id="active" name="active" checked
                           class="w-5 h-5 text-blue-500 border-gray-300 rounded focus:ring focus:ring-blue-300">
                @else
                    <input type="checkbox" id="active" name="active"
                           class="w-5 h-5 text-blue-500 border-gray-300 rounded focus:ring focus:ring-blue-300">
                @endif
                <label for="active" class="ml-2 text-gray-700 font-medium">Active</label>
            </div>
            <div class="mb-4">
                <label for="title" class="block text-gray-700 font-medium mb-2">Event Title</label>
                <input type="text" id="title" name="title" required value="${event!=null ? event.title() : ""}"
                       class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none">
            </div>
            <!-- Description Input -->
            <div class="mb-4">
                <label for="description" class="block text-gray-700 font-medium mb-2">Description</label>
                <textarea id="description" name="description" rows="4" required
                          class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none">${event != null ? event.description() : ""}</textarea>
            </div>
            <!-- Date Picker -->
            <div class="mb-4">
                <label for="date" class="block text-gray-700 font-medium mb-2">Event Date</label>
                <input type="date" id="date" name="date" required value="${event!=null ? event.date().toString() : ""}"
                       class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none">
            </div>
            <!-- Submit Button -->
            <div class="text-center">
                <button type="submit"
                        class="bg-blue-500 text-white font-medium px-4 py-2 rounded-md hover:bg-blue-600 focus:outline-none focus:ring focus:ring-blue-300">
                    ${event != null ? "Update" : "Create"} Event
                </button>
            </div>
        </form>
        @endif
    </div>
</div>
`)
