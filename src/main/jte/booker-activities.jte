@import org.montrealjug.billetterie.entity.Activity
@import org.montrealjug.billetterie.entity.ActivityParticipant
@import org.montrealjug.billetterie.ui.PresentationEvent
@import org.montrealjug.billetterie.ui.PresentationActivity
@import org.montrealjug.billetterie.entity.Booker
@import java.time.format.DateTimeFormatter

@param PresentationEvent event = null
@param Booker booker = null
@param String error = null

@template.layouts.guest_layout(
title = "Event Activities",
content = @`
<div class="flex flex-col items-center justify-center">
    @if(error != null)
        <div class="bg-red-500 text-white p-4 mb-4 rounded-md w-full max-w-4xl">
            ${error}
        </div>
    @endif

    <div class="flex flex-col items-center justify-center">
        @if(event != null)
            <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">
                <div class="bg-gray-100 p-4 rounded-md shadow">
                    <div class="flex flex-col md:flex-row items-start md:items-center justify-between">
                        <div>
                            <h2 class="text-lg font-semibold text-gray-700">${event.title()}</h2>
                            $unsafe{event.description()}
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
                                <th class="border border-gray-300 px-4 py-2 text-left">Attendance</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Waiting line</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Start Time</th>
                                <th class="border border-gray-300 px-4 py-2 text-left">Actions</th>
                            </tr>
                            </thead>
                            <tbody id="activity-list-1">
                            <!-- Activities Rows -->
                            @for(PresentationActivity activity: event.activities())
                                !{var regularParticipantsNumber = activity.participants().size(); }
                                !{var waitingParticipantsNumber = activity.waitingParticipants().size(); }

                                <tr>
                                    <td class="border border-gray-300 px-4 py-2">${activity.title()}</td>
                                    <td class="border border-gray-300 px-4 py-2">$unsafe{activity.description()}</td>
                                    <td class="border border-gray-300 px-4 py-2">
                                        ${regularParticipantsNumber}/${activity.maxParticipants()}
                                    </td>
                                    <td class="border border-gray-300 px-4 py-2">
                                        ${waitingParticipantsNumber}/${activity.maxWaitingQueue()}
                                    </td>
                                    <td class="border border-gray-300 px-4 py-2">${activity.time().format(DateTimeFormatter.ofPattern("H:mm a"))}</td>
                                    <td class="border border-gray-300 px-4 py-2">
                                        @if(activity.registrationStatus().equals(Activity.RegistrationStatus.CLOSED))
                                            <button disabled
                                                    class="bg-gray-400 text-white px-3 py-1 rounded cursor-not-allowed">
                                                Activity Full
                                            </button>
                                        @else
                                            <button onclick="displayParticipantModal(${activity.id()})" 
                                                    class="bg-d4k-orange text-white px-3 py-1 rounded hover:opacity-80 focus:outline-none focus:ring focus:ring-orange-300">
                                                Add participant
                                            </button>
                                        @endif
                                    </td>
                                </tr>
                                <tr id="participants-container-${activity.id()}" class="${activity.participants().isEmpty() ? "hidden" : ""}">
                                    <td colspan="6" class="border border-gray-300 px-4 py-2 bg-gray-50">
                                        <div class="text-sm font-semibold mb-2">Participants:</div>
                                        <ul id="participants-list-${activity.id()}" class="list-disc pl-5">
                                            <!-- Display existing regular participants -->
                                            @if(regularParticipantsNumber != 0)
                                                <div class="mb-2">
                                                    <span class="font-medium">Regular participants:</span>
                                                    @for(ActivityParticipant activityParticipant : activity.participants())
                                                        !{var participant = activityParticipant.getParticipant(); }
                                                        <li>${participant.getFirstName()} ${participant.getLastName()}, born in ${participant.getYearOfBirth()}</li>
                                                    @endfor
                                                </div>
                                            @endif

                                            <!-- Display waiting participants -->
                                            @if(waitingParticipantsNumber != 0)
                                                <div>
                                                    <span class="font-medium text-orange-600">Waiting list:</span>
                                                    @for(ActivityParticipant activityParticipant : activity.waitingParticipants())
                                                        !{var participant = activityParticipant.getParticipant(); }
                                                        <li class="text-orange-600">${participant.getFirstName()} ${participant.getLastName()}, born in ${participant.getYearOfBirth()} (waiting)</li>
                                                    @endfor
                                                </div>
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
    <script>
        // Store booker email signature in a JavaScript variable
        const bookerEmailSignature = "${booker.getEmailSignature()}";
        // Store event ID in a JavaScript variable
        const eventId = "${event.id()}";

        function displayParticipantModal(activityId) {
            // Create the modal elements
            const modal = document.createElement("div");
            modal.id = "participant-modal";
            modal.className = "fixed inset-0 flex items-center justify-center bg-black/50 z-50";
    @raw
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
                    <button type="submit" class="bg-d4k-orange text-white px-3 py-1 rounded hover:opacity-80 focus:outline-none focus:ring focus:ring-blue-300">Submit</button>
                </div>
            </form>
        </div>
    `;
    @endraw

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

            // Check if we need to create the regular or waiting participants section
            // Get max participants from the attendance cell (format: "X/Y")
            const activityRow = document.querySelector("tr:has(button[onclick*='" + activityId + "'])");
            const attendanceCell = activityRow.querySelector("td:nth-child(3)");
            const attendanceText = attendanceCell.textContent.trim();
            const maxParticipants = parseInt(attendanceText.split('/')[1]);

            const currentRegularCount = document.querySelectorAll("#participants-list-" + activityId + " > div:first-child li").length;
            const isWaiting = currentRegularCount >= maxParticipants;

            // Find or create the appropriate section
            let section;
            if (isWaiting) {
                // Look for waiting list section
                section = list.querySelector("div:nth-child(2)");
                if (!section) {
                    // Create waiting list section if it doesn't exist
                    section = document.createElement("div");
                    const heading = document.createElement("span");
                    heading.className = "font-medium text-orange-600";
                    heading.textContent = "Waiting list:";
                    section.appendChild(heading);
                    list.appendChild(section);
                }
            } else {
                // Look for regular participants section
                section = list.querySelector("div:first-child");
                if (!section) {
                    // Create regular participants section if it doesn't exist
                    section = document.createElement("div");
                    section.className = "mb-2";
                    const heading = document.createElement("span");
                    heading.className = "font-medium";
                    heading.textContent = "Regular participants:";
                    section.appendChild(heading);
                    list.appendChild(section);
                }
            }

            // Create a new list item for the participant
            const listItem = document.createElement("li");
            if (isWaiting) {
                listItem.className = "text-orange-600";
                listItem.textContent = participant.firstName + " " + participant.lastName + ", born in " + participant.yearOfBirth + " (waiting)";
            } else {
                listItem.textContent = participant.firstName + " " + participant.lastName + ", born in " + participant.yearOfBirth;
            }

            // Add the list item to the appropriate section
            section.appendChild(listItem);

            // Show the participants container if it's hidden
            container.classList.remove("hidden");

            // Update the attendance and waiting line counts
            updateCounts(activityId, isWaiting);
        }

        function updateCounts(activityId, isWaiting) {
            // This is a simplified update - in a real app, you'd want to refresh from the server
            // or maintain more accurate client-side state
            const activityRow = document.querySelector("tr:has(button[onclick*='" + activityId + "'])");
            if (activityRow) {
                const attendanceCell = activityRow.querySelector("td:nth-child(3)");
                const waitingCell = activityRow.querySelector("td:nth-child(4)");

                if (isWaiting) {
                    // Update waiting count
                    const waitingText = waitingCell.textContent;
                    const [current, max] = waitingText.split('/');
                    const newCount = parseInt(current) + 1;
                    waitingCell.textContent = newCount + '/' + max;

                    // If waiting queue is now full, disable the add button
                    if (newCount >= parseInt(max)) {
                        const addButton = activityRow.querySelector("button[onclick*='displayParticipantModal']");
                        if (addButton) {
                            const disabledButton = document.createElement("button");
                            disabledButton.disabled = true;
                            disabledButton.className = "bg-gray-400 text-white px-3 py-1 rounded cursor-not-allowed";
                            disabledButton.textContent = "Activity Full";
                            addButton.parentNode.replaceChild(disabledButton, addButton);
                        }
                    }
                } else {
                    // Update attendance count
                    const attendanceText = attendanceCell.textContent;
                    const [current, max] = attendanceText.split('/');
                    const newCount = parseInt(current) + 1;
                    attendanceCell.textContent = newCount + '/' + max;
                }
            }
        }

        function closeModal() {
            const modal = document.getElementById("participant-modal");
            if (modal) {
                modal.remove();
            }
        }
    </script>
`)
