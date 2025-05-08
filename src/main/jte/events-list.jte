@import org.montrealjug.billetterie.entity.ActivityParticipant
@import org.montrealjug.billetterie.ui.PresentationEvent
@import org.montrealjug.billetterie.ui.PresentationActivity
@import java.time.format.DateTimeFormatter
@import java.util.List

@param List<PresentationEvent> events

@template.layouts.admin_layout(title = "Event List", content = @`

    <div class="flex flex-col items-center justify-center">
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
                        $unsafe{event.description()}
                        <p class="text-gray-500 text-sm mt-1">${event.date().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))}</p>
                        <p class="text-gray-500 text-sm mt-1">${event.location() != null ? event.location() : ""}</p>
                        <p class="text-gray-500 text-sm mt-1">${event.imagePath()}</p>
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
                            <th class="border border-gray-300 px-4 py-2 text-left">Attendance</th>
                            <th class="border border-gray-300 px-4 py-2 text-left">Waiting line</th>
                            <th class="border border-gray-300 px-4 py-2 text-left">Start Time</th>
                            <th class="border border-gray-300 px-4 py-2 text-center">Actions</th>
                        </tr>
                        </thead>
                        <tbody id="activity-list-1">
                        <!-- Example Activity Row -->
                        @for(PresentationActivity activity: event.activities())
                            !{var regularParticipantsNumber = activity.participants().size(); }
                            !{var waitingParticipantsNumber = activity.waitingParticipants().size(); }

                            <tr>
                            <td class="border border-gray-300 px-4 py-2">
                                ${activity.title()}
                                <span class="block mt-1 text-sm text-gray-500 text-no-wrap whitespace-nowrap">${activity.imagePath()}</span>
                            </td>
                            <td class="border border-gray-300 px-4 py-2">$unsafe{activity.description()}</td>
                            <td class="border border-gray-300 px-4 py-2">
                                ${regularParticipantsNumber}/${activity.maxParticipants()}
                            </td>
                            <td class="border border-gray-300 px-4 py-2">
                                ${waitingParticipantsNumber}/${activity.maxParticipants()}
                            </td>

                            <td class="border border-gray-300 px-4 py-2">${activity.time().format(DateTimeFormatter.ofPattern("H:mm a"))}</td>
                            <td class="border border-gray-300 px-4 py-2 text-center space-x-2">
                                <a href="/admin/events/${event.id()}/activities/${activity.id()}"
                                   class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 focus:outline-none focus:ring focus:ring-blue-300">
                                    Edit
                                </a>
                                <button onclick="deleteActivity('${event.id()}', ${activity.id()})"
                                        class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                                    Delete
                                </button>
                            </td>
                        </tr>
                        <!-- Participants Row -->
                        <tr>
                            <td colspan="6" class="border border-gray-300 px-4 py-2 bg-gray-50">
                                <div class="text-sm font-semibold mb-2">Participants:</div>
                                <ul class="list-disc pl-5" id="participants-list-${activity.id()}">
                                    <!-- Regular participants -->
                                    @if(regularParticipantsNumber > 0)
                                        <div class="mb-2">
                                            <span class="font-medium">Regular participants:</span>
                                            @for(ActivityParticipant activityParticipant : activity.participants())
                                                !{var participant = activityParticipant.getParticipant(); }
                                                <li class="flex justify-between items-center mb-1">
                                                    <span>${participant.getFirstName()} ${participant.getLastName()}, aged ${participant.getAge()} - Booker: <a href="http://localhost:8080/admin/bookers/${participant.getBooker().getEmail()}">${participant.getBooker().getFirstName()} ${participant.getBooker().getLastName()} (${participant.getBooker().getEmail()})</a></span>
                                                    <div class="flex space-x-2">
                                                        <button onclick="deleteParticipant(${participant.getId()})"
                                                                data-activity-id="${activity.id()}"
                                                                class="bg-red-500 text-white px-2 py-1 text-xs rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                                                            Delete
                                                        </button>
                                                    </div>
                                                </li>
                                            @endfor
                                        </div>
                                    @endif

                                    <!-- Waiting participants -->
                                    @if(waitingParticipantsNumber > 0)
                                        <div>
                                            <span class="font-medium text-orange-600">Waiting list:</span>
                                            @for(ActivityParticipant activityParticipant : activity.waitingParticipants())
                                                !{var participant = activityParticipant.getParticipant(); }
                                                <li class="flex justify-between items-center mb-1 text-orange-600">
                                                    <span>${participant.getFirstName()} ${participant.getLastName()}, aged ${participant.getAge()} - Booker: <a href="http://localhost:8080/admin/bookers/${participant.getBooker().getEmail()}" class="text-orange-800">${participant.getBooker().getFirstName()} ${participant.getBooker().getLastName()} (${participant.getBooker().getEmail()})</a> (waiting)</span>
                                                    <div class="flex space-x-2">
                                                        <button onclick="deleteParticipant(${participant.getId()})"
                                                                data-activity-id="${activity.id()}"
                                                                class="bg-red-500 text-white px-2 py-1 text-xs rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                                                            Delete
                                                        </button>
                                                    </div>
                                                </li>
                                            @endfor
                                        </div>
                                    @endif

                                    <!-- No participants message -->
                                    @if(regularParticipantsNumber == 0 )
                                        <li>No participants yet</li>
                                    @endif
                                </ul>
                            </td>
                        </tr>
                        @endfor

                        <!-- More rows will be dynamically added -->
                        </tbody>
                    </table>
                    <!-- Create Activity Button -->
                    <div class="text-right mt-2">
                        <button onclick="window.location.href = '/admin/events/${event.id()}/createActivity'"
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
        async function deleteEvent(eventId) {
            // Confirm the action with the user
            if (confirm("Are you sure you want to delete this event?")) {
                try {
                    // Send DELETE request to the server
                    @raw
                    const response = await fetch(`/admin/events/${eventId}`, {
                        method: 'DELETE',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                    });
                    @endraw
                    // Check if the deletion was successful
                    if (response.ok) {
                        alert("Event deleted successfully!");
                        // Optionally, remove the event from the DOM
                        @raw
                        document.querySelector(`button[onclick="deleteEvent(${eventId})"]`).closest("div").parentElement.parentElement.remove();
                        @endraw
                    } else {
                        // Handle server error
                        const errorData = await response.json();
                        @raw
                        alert(`Failed to delete event: ${errorData.message || response.statusText}`);
                        @endraw
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
                    @raw
                    const response = await fetch(`/admin/events/${eventId}/activities/${activityId}`, {
                        method: 'DELETE',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                    });
                    @endraw

                    // Check if the deletion was successful
                    if (response.ok) {
                        alert("Activity deleted successfully!");
                        // Optionally, remove the activity from the DOM
                        @raw
                        document.querySelector(`button[onclick="deleteActivity(${eventId}, ${activityId})"]`).closest("tr").remove();
                        @endraw
                    } else {
                        // Handle server error
                        const errorData = await response.json();
                        @raw
                        alert(`Failed to delete activity: ${errorData.message || response.statusText}`);
                        @endraw
                    }
                } catch (error) {
                    // Handle network errors
                    console.error("Error deleting activity:", error);
                    alert("An error occurred while deleting the activity. Please try again.");
                }
            }
        }

        async function deleteParticipant(participantId) {
            if (confirm("Are you sure you want to delete this participant?")) {
                try {
                    // Get the activity ID from the data attribute
                    const activityId = event.target.getAttribute('data-activity-id');

                    // Send DELETE request to the server
                    @raw
                    const response = await fetch(`/admin/activities/${activityId}/participants/${participantId}`, {
                        method: 'DELETE',
                        headers: {
                            'Content-Type': 'application/json',
                        },
                    });
                    @endraw


                    if (response.ok) {
                        alert("Participant deleted successfully!");
                        // Reload the page to show updated data
                        window.location.reload();
                    } else {
                        @raw
                        const errorData = await response.json();
                        alert(`Failed to delete participant: ${errorData.message || response.statusText}`);
                        @endraw

                    }
                } catch (error) {
                    console.error("Error deleting participant:", error);
                    alert("An error occurred while deleting the participant. Please try again.");
                }
            }
        }

    </script>
`)
