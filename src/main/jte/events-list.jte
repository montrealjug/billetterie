@import org.montrealjug.billetterie.ui.PresentationEvent
@import org.montrealjug.billetterie.ui.PresentationActivity
@import java.time.format.DateTimeFormatter
@import java.util.List

@param List<PresentationEvent> events
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Event List</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<div class="min-h-screen flex flex-col items-center justify-center">


    <div class="min-h-screen flex flex-col items-center justify-center">
        <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">
            <!-- Create Event Button -->
            <div class="flex justify-between items-center mb-6">
                <h1 class="text-2xl font-bold">Event List</h1>
                <a href="/admin/events/createEvent"
                   class="bg-green-500 text-white font-medium px-4 py-2 rounded hover:bg-green-600 focus:outline-none focus:ring focus:ring-green-300">
                    Create Event
                </a>
            </div>
            @for(PresentationEvent event: events)

            <div class="bg-gray-100 p-4 rounded-md shadow">
                <div class="flex flex-col md:flex-row items-start md:items-center justify-between">
                    <div>
                        <h2 class="text-lg font-semibold text-gray-700">${event.title()} - ${event.active() ? "Active" : "Not Active"}</h2>
                        <p class="text-gray-600">${event.description()}</p>
                        <p class="text-gray-500 text-sm mt-1">${event.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</p>
                    </div>
                    <div class="flex space-x-2 mt-4 md:mt-0">
                        <a href="/admin/events/${event.id()}"
                           class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 focus:outline-none focus:ring focus:ring-blue-300">
                            Edit
                        </a>
                        <button onclick="deleteEvent(${event.id()})"
                                class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                            Delete
                        </button>
                    </div>
                </div>
                <!-- Activities Table -->
                <div class="mt-4">
                    <h3 class="text-md font-semibold text-gray-700 mb-2">Activities</h3>
                    <table class="w-full table-auto border-collapse border border-gray-300">
                        <thead>
                        <tr class="bg-gray-200">
                            <th class="border border-gray-300 px-4 py-2 text-left">Title</th>
                            <th class="border border-gray-300 px-4 py-2 text-left">Description</th>
                            <th class="border border-gray-300 px-4 py-2 text-left">Max Seats</th>
                            <th class="border border-gray-300 px-4 py-2 text-left">Max Waiting</th>
                            <th class="border border-gray-300 px-4 py-2 text-left">Start Time</th>
                            <th class="border border-gray-300 px-4 py-2 text-center">Actions</th>
                        </tr>
                        </thead>
                        <tbody id="activity-list-1">
                        <!-- Example Activity Row -->
                        @for(PresentationActivity activity: event.activities())


                        <tr>
                            <td class="border border-gray-300 px-4 py-2">${activity.title()}</td>
                            <td class="border border-gray-300 px-4 py-2">${activity.description()}</td>
                            <td class="border border-gray-300 px-4 py-2">${activity.maxParticipants()}</td>
                            <td class="border border-gray-300 px-4 py-2">${activity.maxWaitingQueue()}</td>

                            <td class="border border-gray-300 px-4 py-2">${activity.time().format(DateTimeFormatter.ofPattern("H:mm a"))}</td>
                            <td class="border border-gray-300 px-4 py-2 text-center space-x-2">
                                <a href="/admin/events/${event.id()}/activities/${activity.id()}"
                                   class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 focus:outline-none focus:ring focus:ring-blue-300">
                                    Edit
                                </a>
                                <button onclick="deleteActivity(${event.id()}, ${activity.id()})"
                                        class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                                    Delete
                                </button>
                            </td>
                        </tr>
                        @endfor

                        <!-- More rows will be dynamically added -->
                        </tbody>
                    </table>
                    <!-- Create Activity Button -->
                    <div class="text-right mt-2">
                        <button onclick="window.location.href = `/admin/events/${event.id()}/createActivity`"
                                class="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 focus:outline-none focus:ring focus:ring-green-300">
                            Add Activity
                        </button>
                    </div>
                </div>
            </div>
            @endfor
        </div>
    </div>

    <script>
@raw
    async function deleteEvent(eventId) {
        // Confirm the action with the user
        if (confirm("Are you sure you want to delete this event?")) {
            try {
                // Send DELETE request to the server
                const response = await fetch(`/admin/events/${eventId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });

                // Check if the deletion was successful
                if (response.ok) {
                    alert("Event deleted successfully!");
                    // Optionally, remove the event from the DOM
                    document.querySelector(`button[onclick="deleteEvent(${eventId})"]`).closest("div").parentElement.parentElement.remove();
                } else {
                    // Handle server error
                    const errorData = await response.json();
                    alert(`Failed to delete event: ${errorData.message || response.statusText}`);
                }
            } catch (error) {
                // Handle network errors
                console.error("Error deleting event:", error);
                alert("An error occurred while deleting the event. Please try again.");
            }
        }
    }

    async function deleteActivity(eventId, activityId) {
        // Confirm the action with the user
        if (confirm("Are you sure you want to delete this activity?")) {
            try {
                // Send DELETE request to the server
                const response = await fetch(`/admin/events/${eventId}/activities/${activityId}`, {
                    method: 'DELETE',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });

                // Check if the deletion was successful
                if (response.ok) {
                    alert("Activity deleted successfully!");
                    // Optionally, remove the activity from the DOM
                    document.querySelector(`button[onclick="deleteActivity(${eventId}, ${activityId})"]`).closest("tr").remove();
                } else {
                    // Handle server error
                    const errorData = await response.json();
                    alert(`Failed to delete activity: ${errorData.message || response.statusText}`);
                }
            } catch (error) {
                // Handle network errors
                console.error("Error deleting activity:", error);
                alert("An error occurred while deleting the activity. Please try again.");
            }
        }
    }

@endraw


        <%--// Delete activity handler--%>
        <%--function deleteActivity(eventId, activityId) {--%>
        <%--    if (confirm("Are you sure you want to delete this activity?")) {--%>
        <%--        console.log(`Activity with ID ${activityId} deleted in Event ID ${eventId}`);--%>
        <%--        const activityRow = document.querySelector(--%>
        <%--            `#activity-list-${eventId} button[onclick="deleteActivity(${eventId}, ${activityId})"]`--%>
        <%--        ).closest("tr");--%>
        <%--        activityRow.remove();--%>
        <%--    }--%>
        <%--}--%>

        <%--// Delete event handler--%>
        <%--function deleteEvent(eventId) {--%>
        <%--    if (confirm("Are you sure you want to delete this event?")) {--%>
        <%--        console.log(`Event with ID ${eventId} deleted`);--%>
        <%--        const eventCard = document.querySelector(`button[onclick="deleteEvent(${eventId})"]`).closest("div");--%>
        <%--        eventCard.remove();--%>
        <%--    }--%>
        <%--}--%>

        // Create activity handler
        <%--function createActivity(eventId) {--%>
        <%--    console.log(`Create activity for Event ID ${eventId}`);--%>
        <%--    // Redirect to a form or handle activity creation logic--%>
        <%--}--%>
    </script>


</div>
</body>
</html>