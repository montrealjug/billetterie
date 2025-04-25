@import org.montrealjug.billetterie.ui.PresentationEvent
@import org.montrealjug.billetterie.ui.PresentationActivity
@import java.time.format.DateTimeFormatter

@param PresentationEvent event = null

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Event List</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100">
<a href="/admin/events">Go to the admin page</a>

<div class="min-h-screen flex flex-col items-center justify-center">


    <div class="min-h-screen flex flex-col items-center justify-center">
        @if(event != null)
            <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">


                <div class="bg-gray-100 p-4 rounded-md shadow">
                    <div class="flex flex-col md:flex-row items-start md:items-center justify-between">
                        <div>
                            <h2 class="text-lg font-semibold text-gray-700">${event.title()}</h2>
                            <p class="text-gray-600">${event.description()}</p>
                            <p class="text-gray-500 text-sm mt-1">${event.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</p>
                        </div>
                        <div class="size-24">
                            @template.layouts.image_with_fallback(
                                entityType = "event",
                                cssClass = "object-cover object-center size-full rounded-lg",
                                id = event.id()
                            )
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
                                </tr>
                            @endfor

                            <!-- More rows will be dynamically added -->
                            </tbody>
                        </table>
                        <!-- Create Activity Button -->

                    </div>
                </div>
                <button onclick="displayRegistrationModal()"
                        class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 focus:outline-none focus:ring focus:ring-blue-300">
                    Register
                </button>
            </div>

        @else
            <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">
                There is no event planned currrently
            </div>
        @endif

    </div>

    <script>

        function displayRegistrationModal() {
            // Create the modal elements
            const modal = document.createElement("div");
            modal.id = "registration-modal";
            modal.className = "fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50";

            modal.innerHTML = `
        <div class="bg-white rounded-lg shadow-md p-6 w-96">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Register</h2>
            <form id="registration-form" class="space-y-4">
                <div>
                    <label for="first-name" class="block text-gray-700">First Name</label>
                    <input type="text" id="first-name" name="firstName" class="border border-gray-300 rounded px-3 py-2 w-full" required>
                </div>
                <div>
                    <label for="last-name" class="block text-gray-700">Last Name</label>
                    <input type="text" id="last-name" name="lastName" class="border border-gray-300 rounded px-3 py-2 w-full" required>
                </div>
                <div>
                    <label for="email" class="block text-gray-700">Email</label>
                    <input type="email" id="email" name="email" class="border border-gray-300 rounded px-3 py-2 w-full" required>
                </div>
                <div>
                    <label for="confirm-email" class="block text-gray-700">Confirm Email</label>
                    <input type="email" id="confirm-email" name="confirmEmail" class="border border-gray-300 rounded px-3 py-2 w-full" required>
                </div>
                <div id="error-message" class="text-red-500 text-sm hidden"></div>
                <div class="flex justify-end gap-2">
                    <button type="button" onclick="closeModal()" class="bg-gray-300 text-gray-700 px-3 py-1 rounded hover:bg-gray-400 focus:outline-none focus:ring">Cancel</button>
                    <button type="submit" class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 focus:outline-none focus:ring focus:ring-blue-300">Submit</button>
                </div>
            </form>
        </div>
    `;

            document.body.appendChild(modal);

            const form = document.getElementById("registration-form");
            const errorMessage = document.getElementById("error-message");

            form.addEventListener("submit", async (e) => {
                e.preventDefault();

                // Gather form data
                const firstName = form.firstName.value.trim();
                const lastName = form.lastName.value.trim();
                const email = form.email.value.trim();
                const confirmEmail = form.confirmEmail.value.trim();

                // Validate inputs
                if (!firstName || !lastName || !email || !confirmEmail) {
                    errorMessage.textContent = "Please fill in all fields.";
                    errorMessage.classList.remove("hidden");
                    return;
                }

                if (email !== confirmEmail) {
                    errorMessage.textContent = "Emails do not match.";
                    errorMessage.classList.remove("hidden");
                    return;
                }

                errorMessage.classList.add("hidden");

                // Send HTTP POST request
                try {
                    const response = await fetch("/registerBooker", {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify({firstName, lastName, email}),
                    });

                    if (response.ok) {
                        alert("Registration successful!");
                        closeModal();
                        location.reload();
                    } else {
                        const errorData = await response.json();
                        errorMessage.textContent = errorData.message || "Registration failed.";
                        errorMessage.classList.remove("hidden");
                    }
                } catch (error) {
                    console.error("Error during registration:", error);
                    errorMessage.textContent = "An error occurred. Please try again.";
                    errorMessage.classList.remove("hidden");
                }
            });
        }

        function closeModal() {
            const modal = document.getElementById("registration-modal");
            if (modal) {
                modal.remove();
            }
        }
<%--        @raw--%>
<%--        async function deleteEvent(eventId) {--%>
<%--            // Confirm the action with the user--%>
<%--            if (confirm("Are you sure you want to delete this event?")) {--%>
<%--                try {--%>
<%--                    // Send DELETE request to the server--%>
<%--                    const response = await fetch(`/admin/events/${eventId}`, {--%>
<%--                        method: 'DELETE',--%>
<%--                        headers: {--%>
<%--                            'Content-Type': 'application/json',--%>
<%--                        },--%>
<%--                    });--%>

<%--                    // Check if the deletion was successful--%>
<%--                    if (response.ok) {--%>
<%--                        alert("Event deleted successfully!");--%>
<%--                        // Optionally, remove the event from the DOM--%>
<%--                        document.querySelector(`button[onclick="deleteEvent(${eventId})"]`).closest("div").parentElement.parentElement.remove();--%>
<%--                    } else {--%>
<%--                        // Handle server error--%>
<%--                        const errorData = await response.json();--%>
<%--                        alert(`Failed to delete event: ${errorData.message || response.statusText}`);--%>
<%--                    }--%>
<%--                } catch (error) {--%>
<%--                    // Handle network errors--%>
<%--                    console.error("Error deleting event:", error);--%>
<%--                    alert("An error occurred while deleting the event. Please try again.");--%>
<%--                }--%>
<%--            }--%>
<%--        }--%>

<%--        async function deleteActivity(eventId, activityId) {--%>
<%--            // Confirm the action with the user--%>
<%--            if (confirm("Are you sure you want to delete this activity?")) {--%>
<%--                try {--%>
<%--                    // Send DELETE request to the server--%>
<%--                    const response = await fetch(`/admin/events/${eventId}/activities/${activityId}`, {--%>
<%--                        method: 'DELETE',--%>
<%--                        headers: {--%>
<%--                            'Content-Type': 'application/json',--%>
<%--                        },--%>
<%--                    });--%>

<%--                    // Check if the deletion was successful--%>
<%--                    if (response.ok) {--%>
<%--                        alert("Activity deleted successfully!");--%>
<%--                        // Optionally, remove the activity from the DOM--%>
<%--                        document.querySelector(`button[onclick="deleteActivity(${eventId}, ${activityId})"]`).closest("tr").remove();--%>
<%--                    } else {--%>
<%--                        // Handle server error--%>
<%--                        const errorData = await response.json();--%>
<%--                        alert(`Failed to delete activity: ${errorData.message || response.statusText}`);--%>
<%--                    }--%>
<%--                } catch (error) {--%>
<%--                    // Handle network errors--%>
<%--                    console.error("Error deleting activity:", error);--%>
<%--                    alert("An error occurred while deleting the activity. Please try again.");--%>
<%--                }--%>
<%--            }--%>
<%--        }--%>

<%--        @endraw--%>


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
