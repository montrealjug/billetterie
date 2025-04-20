@param org.montrealjug.billetterie.ui.PresentationActivity activity
@param Long eventId
@param String errorMessage

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create New Activity</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<div class="min-h-screen flex items-center justify-center">
    <div class="bg-white p-8 rounded shadow-md w-full max-w-md">
        @if(errorMessage != null)
            <div class="mb-4 bg-red-100 border border-red-400 text-red-700 px-4 py-3 rounded">
                <p class="font-semibold">Error</p>
                <p>${errorMessage}</p>
            </div>
        @endif
        @if(errorMessage == null)
        <h1 class="text-2xl font-bold text-center mb-6">${activity != null ? "Update" : "Create"} Activity</h1>
        <form action="/admin/events/${eventId}/activities${activity != null ? "/" + activity.id() : ""}" method="POST">
            <!-- Title Input -->
            <div class="mb-4">
                <label for="title" class="block text-gray-700 font-medium mb-2">Activity Title</label>
                <input type="text" id="title" name="title" required value="${activity!=null ? activity.title() : ""}"
                       class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none">
            </div>
            <!-- Description Input -->
            <div class="mb-4">
                <label for="description" class="block text-gray-700 font-medium mb-2">Description</label>
                <textarea id="description" name="description" rows="4" required
                          class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none">${activity != null ? activity.description() : ""}</textarea>
            </div>
            <!-- Date Picker -->
            <div class="mb-4">
                <label for="time" class="block text-gray-700 font-medium mb-2">Activity Time</label>
                <input type="time" id="time" name="time" required value="${activity!=null ? activity.time().toString() : ""}"
                       class="border border-gray-300 rounded-md px-4 py-2 focus:ring focus:ring-blue-300 focus:outline-none">
            </div>
            <!-- Max Participants Input -->
            <div class="mb-4">
                <label for="maxParticipants" class="block text-gray-700 font-medium mb-2">Max Participants</label>
                <input type="number" id="maxParticipants" name="maxParticipants" required min="1"
                       value="${activity != null ? activity.maxParticipants() : 0}"
                       class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none">
            </div>

            <!-- Max Waiting Queue Input -->
            <div class="mb-4">
                <label for="maxWaitingQueue" class="block text-gray-700 font-medium mb-2">Max Waiting Queue</label>
                <input type="number" id="maxWaitingQueue" name="maxWaitingQueue" required min="0"
                       value="${activity != null ? activity.maxWaitingQueue() : 0}"
                       class="w-full px-4 py-2 border border-gray-300 rounded-md focus:ring focus:ring-blue-300 focus:outline-none">
            </div>
            <!-- Submit Button -->
            <div class="text-center">
                <button type="submit"
                        class="bg-blue-500 text-white font-medium px-4 py-2 rounded-md hover:bg-blue-600 focus:outline-none focus:ring focus:ring-blue-300">
                    ${activity != null ? "Update" : "Create"} Activity
                </button>
            </div>
        </form>
        @endif
    </div>
</div>
</body>
</html>>