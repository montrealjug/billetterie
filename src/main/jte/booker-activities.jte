@import org.montrealjug.billetterie.ui.PresentationEvent
@import org.montrealjug.billetterie.ui.PresentationActivity
@import org.montrealjug.billetterie.entity.Booker
@import org.montrealjug.billetterie.entity.Participant
@import java.time.format.DateTimeFormatter
@import java.util.List
@import java.util.Map

@param PresentationEvent event = null
@param Booker booker = null
@param String error = null
@param Map<Long, List<Participant>> activityParticipants = null
@param Map<Long, Integer> activityTotalParticipants = null

@template.layouts.guest_layout(
title = "Event Activities",
content = @`
<div class="min-h-screen flex flex-col items-center justify-center">
    @if(error != null)
        <div class="bg-red-500 text-white p-4 mb-4 rounded-md w-full max-w-4xl">
            ${error}
        </div>
    @endif

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
                    </div>
                    <!-- Activities Table -->
                    <div class="mt-4">
                        <h3 class="text-md font-semibold text-gray-700 mb-2">Activities</h3>
                        <table class="w-full table-auto border-collapse border border-gray-300">
                            <thead>
                            <tr class="bg-gray-200">
                                <th class="border border-gray-300 px-4 py-2 text-left">Title</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Description</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Currently booked</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Start Time</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Actions</th>
                            </tr>
                            </thead>
                            <tbody id="activity-list-1">
                            <!-- Activities Rows -->
                            @for(PresentationActivity activity: event.activities())
                                <tr>
                                    <td class="border border-gray-300 px-4 py-2">${activity.title()}</td>
                                    <td class="border border-gray-300 px-4 py-2">${activity.description()}</td>
                                    <td class="border border-gray-300 px-4 py-2">${activityTotalParticipants != null && activityTotalParticipants.containsKey(activity.id()) ? activityTotalParticipants.get(activity.id()) : 0}/${activity.maxParticipants()}</td>
                                    <td class="border border-gray-300 px-4 py-2">${activity.time().format(DateTimeFormatter.ofPattern("H:mm a"))}</td>
                                    <td class="border border-gray-300 px-4 py-2">
                                        <button onclick="displayParticipantModal(${activity.id()})" 
                                                class="bg-[#e16309] text-white px-3 py-1 rounded hover:opacity-80 focus:outline-none focus:ring focus:ring-orange-300">
                                            Add participant
                                        </button>
                                    </td>
                                </tr>
                                <tr id="participants-container-${activity.id()}" class="${activityParticipants != null && activityParticipants.containsKey(activity.id()) && !activityParticipants.get(activity.id()).isEmpty() ? "" : "hidden"}">
                                    <td colspan="6" class="border border-gray-300 px-4 py-2 bg-gray-50">
                                        <div class="text-sm font-semibold mb-2">Participants:</div>
                                        <ul id="participants-list-${activity.id()}" class="list-disc pl-5">
                                            <!-- Display existing participants -->
                                            @if(activityParticipants != null && activityParticipants.containsKey(activity.id()))
                                                @for(Participant participant : activityParticipants.get(activity.id()))
                                                    <li>${participant.getFirstName()} ${participant.getLastName()}, born in ${participant.getYearOfBirth()}</li>
                                                @endfor
                                            @endif
                                            <!-- New participants will be added here dynamically -->
                                        </ul>
                                    </td>
                                </tr>
                            @endfor
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        @else
            <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">
                There is no event planned currently
            </div>
        @endif
    </div>

</div>
`)

    <script>
        // Store booker email signature in a JavaScript variable
        const bookerEmailSignature = "${booker.getEmailSignature()}";
        // Store event ID in a JavaScript variable
        const eventId = "${event.id()}";

        function displayParticipantModal(activityId) {
            // Create the modal elements
            const modal = document.createElement("div");
            modal.id = "participant-modal";
            modal.className = "fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-50";

            modal.innerHTML = `
        <div class="bg-white rounded-lg shadow-md p-6 w-96">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Add Participant</h2>
            <form id="participant-form" class="space-y-4">
                <input type="hidden" id="activity-id" name="activityId" value="` + activityId + `">
                <div>
                    <label for="first-name" class="block text-gray-700">First Name</label>
                    <input type="text" id="first-name" name="firstName" class="border border-gray-300 rounded px-3 py-2 w-full" required>
                </div>
                <div>
                    <label for="last-name" class="block text-gray-700">Last Name</label>
                    <input type="text" id="last-name" name="lastName" class="border border-gray-300 rounded px-3 py-2 w-full" required>
                </div>
                <div>
                    <label for="yearOfBirth" class="block text-gray-700">Year of Birth</label>
                    <input type="number" id="yearOfBirth" name="yearOfBirth" min="1900" max="2023" class="border border-gray-300 rounded px-3 py-2 w-full" required>
                </div>
                <div id="error-message" class="text-red-500 text-sm hidden"></div>
                <div class="flex justify-end gap-2">
                    <button type="button" onclick="closeModal()" class="bg-gray-300 text-gray-700 px-3 py-1 rounded hover:bg-gray-400 focus:outline-none focus:ring">Cancel</button>
                    <button type="submit" class="bg-[#ee7c2a] text-white px-3 py-1 rounded hover:opacity-80 focus:outline-none focus:ring focus:ring-blue-300">Submit</button>
                </div>
            </form>
        </div>
    `;

            document.body.appendChild(modal);

            const form = document.getElementById("participant-form");
            const errorMessage = document.getElementById("error-message");

            form.addEventListener("submit", async (e) => {
                e.preventDefault();

                // Gather form data
                const activityId = form.activityId.value;
                const firstName = form.firstName.value.trim();
                const lastName = form.lastName.value.trim();
                const yearOfBirth = form.yearOfBirth.value;

                // Validate inputs
                if (!firstName || !lastName || !yearOfBirth) {
                    errorMessage.textContent = "Please fill in all fields.";
                    errorMessage.classList.remove("hidden");
                    return;
                }

                errorMessage.classList.add("hidden");

                // Send HTTP POST request
                try {
                    const url = "/events/" + eventId + "/registerParticipant";
                    const response = await fetch(url, {
                        method: "POST",
                        headers: {
                            "Content-Type": "application/json",
                        },
                        body: JSON.stringify({
                            activityId: parseInt(activityId),
                            firstName: firstName,
                            lastName: lastName,
                            yearOfBirth: parseInt(yearOfBirth),
                            bookerEmailSignature: bookerEmailSignature
                        }),
                    });

                    if (response.ok) {
                        // Get the participant data from the response
                        const participantData = await response.json();

                        // Add participant to the list
                        addParticipantToList(activityId, participantData);

                        closeModal();
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

        function addParticipantToList(activityId, participant) {
            // Get the participants container and list
            const container = document.getElementById("participants-container-" + activityId);
            const list = document.getElementById("participants-list-" + activityId);

            // Create a new list item for the participant
            const listItem = document.createElement("li");
            listItem.textContent = participant.firstName + " " + participant.lastName + ", born in " + participant.yearOfBirth;

            // Add the list item to the list
            list.appendChild(listItem);

            // Show the participants container if it's hidden
            container.classList.remove("hidden");
        }

        function closeModal() {
            const modal = document.getElementById("participant-modal");
            if (modal) {
                modal.remove();
            }
        }
    </script>

