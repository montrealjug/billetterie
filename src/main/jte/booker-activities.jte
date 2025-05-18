@import org.montrealjug.billetterie.entity.Activity
@import org.montrealjug.billetterie.entity.ActivityParticipant
@import org.montrealjug.billetterie.ui.PresentationEvent
@import org.montrealjug.billetterie.ui.PresentationActivity
@import org.montrealjug.billetterie.ui.Utils
@import org.montrealjug.billetterie.entity.Booker
@import java.time.format.DateTimeFormatter

@param PresentationEvent event = null
@param Booker booker = null
@param String error = null

@template.layouts.guest_layout(
title = "Event Activities",
content = @`
<div class="flex flex-col items-center justify-center-safe h-full w-full bg-gray-100 py-2 md:p-2 lg:p-4">
    @if(error != null)
        <div class="bg-red-500 text-white p-4 mb-4 rounded-md w-full max-w-4xl">
            ${error}
        </div>
    @endif
        <div>Ticket for ${booker.getFirstName()} ${booker.getLastName()} (${booker.getEmail()})</div>

    @if(event != null)
        <div class="w-full max-w-full h-full md:rounded md:h-auto md:max-h-full md:p-4 lg:p-8 lg:max-w-6xl flex flex-col justify-end-safe md:bg-white md:shadow-md">
            <div class="bg-gray-100 p-2 grow-1 w-full max-w-full md:rounded-md md:shadow md:p-4 overflow-y-auto flex flex-col">
                <div class="flex flex-col md:flex-row-reverse items-start justify-between w-full">
                    <div class="w-full h-24 md:w-1/2">
                        @template.layouts.image_with_fallback(
                        entityType = "event",
                        cssClass = "object-contain size-full rounded-lg",
                        id = event.id()
                        )
                    </div>
                    <div class="md:w-1/2">
                        <h2 class="text-lg font-semibold text-gray-700">${event.title()}</h2>
                        <p class="mt-1">${Utils.DATE_FORMAT.format(event.date())}</p>
                        <p class="mt-1 text-sm md:text-base">
                            <span class="font-semibold text-gray-700">Lieu / Location:&nbsp;</span>
                            <span>${event.location()}</span>
                        </p>
                    </div>
                </div>
                <div class="my-2">$unsafe{event.description()}</div>
                <!-- Activities Section -->
                    <div class="mt-4">
                        <h3 class="text-md font-semibold text-gray-700 mb-2">Activities</h3>
                        <div class="bg-white rounded md:rounded-md md:shadow">
                            @for(PresentationActivity activity: event.activities())
                                !{var waitingParticipantsNumber = activity.totalWaitingQueue(); }
                                !{var regularParticipantsNumber = activity.totalParticipants() - waitingParticipantsNumber; }

                                <article class="my-4 flex flex-col md:flex-row items-start justify-between md:my-8 p-4 border-b border-gray-200">
                                    <div class="size-12 shrink-0 mr-2 md:size-16 md:mr-4">
                                        @template.layouts.image_with_fallback(
                                            entityType = "activity",
                                            cssClass = "object-cover object-center size-full rounded-lg",
                                            id = activity.id()
                                        )
                                    </div>
                                    <div class="grow-1 flex flex-col justify-between mt-2 md:mt-0">
                                        <h4 class="font-semibold">${activity.title()}</h4>
                                        <details name="activity" class="mt-2">
                                            <summary class="italic text-gray-700 list-none cursor-pointer">details</summary>
                                            <div class="mt-2">$unsafe{activity.description()}</div>
                                        </details>
                                    </div>
                                    <div class="flex flex-col justify-between items-end mx-2 self-stretch md:mx-4 mt-2 md:mt-0">
                                        <time>${activity.time().format(DateTimeFormatter.ofPattern("H:mm a"))}</time>
                                        <div class="flex flex-row items-center mt-2">
                                            <span class="text-left text-sm text-gray-700">seats:</span>
                                            <span class="ml-2 w-12 text-right">${regularParticipantsNumber}/${activity.maxParticipants()}</span>
                                        </div>
                                        <div class="flex flex-row items-center mt-1">
                                            <span class="text-left text-sm text-gray-700">waiting:</span>
                                            <span class="ml-2 w-12 text-right">${waitingParticipantsNumber}/${activity.maxWaitingQueue()}</span>
                                        </div>
                                        <div class="mt-2">
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
                                        </div>
                                    </div>
                                </article>

                                <div id="participants-container-${activity.id()}" class="${activity.participants().isEmpty() && activity.waitingParticipants().isEmpty() ? "hidden" : ""} px-4 py-2 bg-gray-50 mb-4 ml-4 mr-4 rounded">
                                    <div class="text-sm font-semibold mb-2">Participants:</div>
                                    <ul id="participants-list-${activity.id()}" class="list-disc pl-5">
                                        <!-- Display existing regular participants -->
                                        @if(regularParticipantsNumber != 0)
                                            <div class="mb-2" id="regular-participants-container">
                                                <span class="font-medium">Regular participants:</span>
                                                @for(ActivityParticipant activityParticipant : activity.participants())
                                                    !{var participant = activityParticipant.getParticipant(); }
                                                    <li class="flex justify-between items-center mb-1">
                                                        <span>${participant.getFirstName()} ${participant.getLastName()}, born in ${participant.getYearOfBirth()}</span>
                                                        @if (booker.getParticipants().contains(participant))
                                                            <div class="flex space-x-2">
                                                                <button onclick='displayRemovalModal(${activity.id()}, "${participant.getFirstName()}", "${participant.getLastName()}", ${participant.getYearOfBirth()})'
                                                                        data-activity-id="${activity.id()}"
                                                                        class="bg-red-500 text-white px-2 py-1 text-xs rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                                                                    Remove
                                                                </button>
                                                            </div>
                                                        @endif
                                                    </li>
                                                @endfor
                                            </div>
                                        @endif

                                            <!-- Display waiting participants -->
                                            @if(waitingParticipantsNumber != 0)
                                                <div id="waiting-participants-container">
                                                    <span class="font-medium text-orange-600">Waiting list:</span>
                                                    @for(ActivityParticipant activityParticipant : activity.waitingParticipants())
                                                        !{var participant = activityParticipant.getParticipant(); }
                                                            @if (booker.getParticipants().contains(participant))
                                                                <li class="text-orange-600 flex justify-between items-center mb-1">
                                                                        <span>${participant.getFirstName()} ${participant.getLastName()}, born in ${participant.getYearOfBirth()} (waiting)</span>
                                                                        <div class="flex space-x-2">
                                                                            <button onclick='displayRemovalModal(${activity.id()}, "${participant.getFirstName()}", "${participant.getLastName()}", ${participant.getYearOfBirth()})'
                                                                                    data-activity-id="${activity.id()}"
                                                                                    class="bg-red-500 text-white px-2 py-1 text-xs rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                                                                                Remove
                                                                            </button>
                                                                        </div>
                                                                </li>
                                                            @endif
                                                    @endfor
                                                </div>
                                            @endif

                                        <!-- New participants will be added here dynamically -->
                                    </ul>
                                </div>
                            @endfor
                        </div>

                        <!-- Submit changes button container - fixed at the bottom of the viewport -->
                        <div id="submit-changes-container" class="fixed bottom-0 left-0 right-0 w-full bg-white p-4 border-t border-gray-200 flex justify-center shadow-md z-50">
                            <button id="submit-changes-button"
                                    onclick="submitAllParticipants()"
                                    class="bg-gray-400 text-white px-4 py-2 rounded cursor-not-allowed"
                                    disabled>
                                Submit changes
                            </button>
                        </div>

                        <!-- Add padding at the bottom to prevent content from being hidden behind the fixed button -->
                        <div class="h-16"></div>
                    </div>
                </div>
            </div>
        @else
            <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">
                There is no event planned currently
            </div>
        @endif
    </div>

    <script>
        // Store booker email signature in a JavaScript variable
        const bookerEmailSignature = "${booker.getEmailSignature()}";
        // Store event ID in a JavaScript variable
        const eventId = "${event.id()}";
        // Store added participants that haven't been submitted yet
        const pendingParticipants = [];
        // Track if there are any pending changes
        let hasPendingChanges = false;

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
                    <button type="button" onclick="closeSubmitModal()" class="bg-gray-300 text-gray-700 px-3 py-1 rounded hover:bg-gray-400 focus:outline-none focus:ring">Cancel</button>
                    <button type="submit" class="bg-d4k-orange text-white px-3 py-1 rounded hover:opacity-80 focus:outline-none focus:ring focus:ring-blue-300">Submit</button>
                </div>
            </form>
        </div>
    `;
    @endraw

            document.body.appendChild(modal);

            const form = document.getElementById("participant-form");
            const errorMessage = document.getElementById("error-message");

            form.addEventListener("submit", (e) => {
                e.preventDefault();

                // Gather form data
                const activityId = parseInt(form.activityId.value);
                const firstName = form.firstName.value.trim();
                const lastName = form.lastName.value.trim();
                const yearOfBirth = parseInt(form.yearOfBirth.value);

                // Validate inputs
                if (!firstName || !lastName || !yearOfBirth) {
                    errorMessage.textContent = "Please fill in all fields.";
                    errorMessage.classList.remove("hidden");
                    return;
                }

                errorMessage.classList.add("hidden");

                // Create participant data object
                const participantData = {
                    activityId: activityId,
                    firstName: firstName,
                    lastName: lastName,
                    yearOfBirth: yearOfBirth,
                    bookerEmailSignature: bookerEmailSignature
                };

                // Add to pending participants array
                pendingParticipants.push(participantData);

                // Update UI with the new participant
                addParticipantToList(activityId, participantData);

                // Set flag for pending changes
                hasPendingChanges = true;

                // Update submit changes button state
                updateSubmitChangesButton();

                // Close the modal
                closeSubmitModal();
            });
        }

        function displayRemovalModal(activityId, firstName, lastName, yearOfBirth) {
            console.log("Display Removal")
            const modal = document.createElement('div');
            modal.id = "removal-modal";
            modal.className = "fixed inset-0 flex items-center justify-center bg-black/50 z-50";
            @raw
            modal.innerHTML = `
        <div class="bg-white rounded-lg shadow-md p-6 w-96">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Remove Participant</h2>
            <form id="removal-form" class="space-y-4">
                <input type="hidden" id="activity-id" name="activityId" value="` + activityId + `">
                <input type="hidden" id="first-name" name="firstName" value="` + firstName + `">
                <input type="hidden" id="last-name" name="lastName" value="` + lastName + `">
                <input type="hidden" id="yearOfBirth" name="yearOfBirth" min="1900" max="2023" value="`+ yearOfBirth + `">
                <span>Are you sure you want to remove participant: ` + firstName + " " + lastName +`?</span>
                <div id="error-message" class="text-red-500 text-sm hidden"></div>
                <div>
                    <button type="button" onclick="closeRemovalModal()" class="bg-gray-300 text-gray-700 px-3 py-1 rounded hover:bg-gray-400 focus:outline-none focus:ring">Cancel</button>
                    <button type="submit" class="bg-d4k-orange text-white px-3 py-1 rounded hover:opacity-80 focus:outline-none focus:ring focus:ring-blue-300">Confirm</button>
                </div>
            </form>
        </div>
    `;
            @endraw

            document.body.appendChild(modal);

            const form = document.getElementById("removal-form");
            const errorMessage = document.getElementById("error-message");

            form.addEventListener("submit", async (e) => {
                e.preventDefault();

                // Gather form data
                const activityId = form.activityId.value;
                const firstName = form.firstName.value;
                const lastName = form.lastName.value;
                const yearOfBirth = form.yearOfBirth.value;

                // Should not ever happen but just in case
                if (!firstName || !lastName || !yearOfBirth) {
                    errorMessage.textContent = "Something went wrong";
                    errorMessage.classList.remove("hidden");
                    return;
                }

                errorMessage.classList.add("hidden");

                // Send HTTP POST request
                try {
                    const url = "/events/" + eventId + "/removeParticipant";
                    const response = await fetch(url, {
                        method: "DELETE",
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
                        window.location.reload();
                        closeRemovalModal();
                    } else {
                        const errorData = await response.json();
                        errorMessage.textContent = errorData.message || "Participant removal failed.";
                        errorMessage.classList.remove("hidden");
                    }
                } catch (error) {
                    console.error("Error during removal:", error);
                    errorMessage.textContent = "An error occurred. Please try again.";
                    errorMessage.classList.remove("hidden");
                }
            });
        }

        function addParticipantToList(activityId, participant) {
            // Get the participants container and list
            const container = document.getElementById("participants-container-" + activityId);
            const list = document.getElementById("participants-list-" + activityId);

            // Find the article element for this activity
            const articles = document.querySelectorAll("article");
            let activityArticle = null;
            for (const article of articles) {
                const button = article.querySelector("button[onclick*='" + activityId + "']");
                if (button) {
                    activityArticle = article;
                    break;
                }
            }

            // Get max participants from the seats display (format: "X/Y")
            const seatsElements = activityArticle.querySelectorAll(".flex-row");
            let seatsText = "";
            for (const element of seatsElements) {
                if (element.textContent.includes("seats:")) {
                    seatsText = element.querySelector(".text-right").textContent.trim();
                    break;
                }
            }
            const maxParticipants = parseInt(seatsText.split('/')[1]);

            const currentRegularCount = document.querySelectorAll("#participants-list-" + activityId + " > div:first-child li").length;
            const isWaiting = currentRegularCount >= maxParticipants;

            // Find or create the appropriate section
            let section;
            if (isWaiting) {
                // Look for waiting list section
                section = list.children.namedItem("waiting-participants-container");
                if (!section) {
                    // Create waiting list section if it doesn't exist
                    section = document.createElement("div");
                    const heading = document.createElement("span");
                    heading.className = "font-medium text-orange-600";
                    heading.id = "waiting-participants-container"
                    heading.textContent = "Waiting list:";
                    section.appendChild(heading);
                    list.appendChild(section);
                }
            } else {
                // Look for regular participants section
                section = list.children.namedItem("regular-participants-container");
                if (!section) {
                    // Create regular participants section if it doesn't exist
                    section = document.createElement("div");
                    section.className = "mb-2";
                    section.id = "regular-participants-container"
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
                listItem.className = "text-orange-600 flex justify-between items-center mb-1";
                // listItem.textContent = participant.firstName + " " + participant.lastName + ", born in " + participant.yearOfBirth + " (waiting)";
                @raw
                listItem.innerHTML = `
                    <span>${participant.firstName} ${participant.lastName}, born in ${participant.yearOfBirth} (waiting)</span>
                    <div class="flex space-x-2">
                        <button onclick='displayRemovalModal(${activityId}, "${participant.firstName}", "${participant.lastName}", ${participant.yearOfBirth})'
                                data-activity-id="${activityId}"
                                class="bg-red-500 text-white px-2 py-1 text-xs rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                            Remove
                        </button>
                    </div>
                `
                @endraw

            } else {
                // listItem.textContent = participant.firstName + " " + participant.lastName + ", born in " + participant.yearOfBirth;
                listItem.className = "flex justify-between items-center mb-1";
                @raw
                listItem.innerHTML = `
                    <span>${participant.firstName} ${participant.lastName}, born in ${participant.yearOfBirth}</span>
                    <div class="flex space-x-2">
                        <button onclick='displayRemovalModal(${activityId}, "${participant.firstName}", "${participant.lastName}", ${participant.yearOfBirth})'
                                data-activity-id="${activityId}"
                                class="bg-red-500 text-white px-2 py-1 text-xs rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                            Remove
                        </button>
                    </div>
                `
                @endraw
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

            // Find the article element for this activity
            const articles = document.querySelectorAll("article");
            let activityArticle = null;
            for (const article of articles) {
                const button = article.querySelector("button[onclick*='" + activityId + "']");
                if (button) {
                    activityArticle = article;
                    break;
                }
            }

            if (activityArticle) {
                // Find the seats and waiting elements
                const flexRows = activityArticle.querySelectorAll(".flex-row");
                let seatsElement = null;
                let waitingElement = null;

                for (const row of flexRows) {
                    if (row.textContent.includes("seats:")) {
                        seatsElement = row.querySelector(".text-right");
                    } else if (row.textContent.includes("waiting:")) {
                        waitingElement = row.querySelector(".text-right");
                    }
                }

                if (isWaiting) {
                    // Update waiting count
                    const waitingText = waitingElement.textContent;
                    const [current, max] = waitingText.split('/');
                    const newCount = parseInt(current) + 1;
                    waitingElement.textContent = newCount + '/' + max;

                    // If waiting queue is now full, disable the add button
                    if (newCount >= parseInt(max)) {
                        const addButton = activityArticle.querySelector("button[onclick*='displayParticipantModal']");
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
                    const seatsText = seatsElement.textContent;
                    const [current, max] = seatsText.split('/');
                    const newCount = parseInt(current) + 1;
                    seatsElement.textContent = newCount + '/' + max;
                }
            }
        }

        function closeSubmitModal() {
            const modal = document.getElementById("participant-modal");
            if (modal) {
                modal.remove();
            }
        }

        function updateSubmitChangesButton() {
            const submitButton = document.getElementById("submit-changes-button");
            if (hasPendingChanges && pendingParticipants.length > 0) {
                submitButton.disabled = false;
                submitButton.classList.remove("bg-gray-400", "cursor-not-allowed");
                submitButton.classList.add("bg-d4k-orange", "hover:opacity-80");
            } else {
                submitButton.disabled = true;
                submitButton.classList.remove("bg-d4k-orange", "hover:opacity-80");
                submitButton.classList.add("bg-gray-400", "cursor-not-allowed");
            }
        }

        async function submitAllParticipants() {
            if (!hasPendingChanges || pendingParticipants.length === 0) {
                return;
            }

            try {
                const url = "/events/" + eventId + "/registerParticipants";
                const response = await fetch(url, {
                    method: "POST",
                    headers: {
                        "Content-Type": "application/json",
                    },
                    body: JSON.stringify(pendingParticipants),
                });

                if (response.ok) {
                    // Clear pending participants after successful submission
                    pendingParticipants.length = 0;
                    hasPendingChanges = false;

                    // Update button state
                    updateSubmitChangesButton();

                    // Show success message
                    alert("All participants have been successfully registered!");
                } else {
                    const errorData = await response.json();
                    alert(errorData.message || "Failed to register participants.");
                }
            } catch (error) {
                console.error("Error during registration:", error);
                alert("An error occurred while registering participants. Please try again.");
            }
        }

        function closeRemovalModal() {
            const modal = document.getElementById("removal-modal");
            if(modal) {
                modal.remove();
            }
        }
    </script>
`)
