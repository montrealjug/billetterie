@import java.util.List
@import org.montrealjug.billetterie.ui.PresentationBookerWithParticipants
@import org.montrealjug.billetterie.entity.Event
@import org.montrealjug.billetterie.entity.ActivityParticipant

@param List<PresentationBookerWithParticipants> bookerList
@param List<Event> allEvents

@template.layouts.admin_layout(title = "Booker List",content = @`

<div class="flex flex-col items-center justify-center">

    <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">
        <div class="flex flex-col items-center mb-6">
            <h1 class="text-2xl font-bold">Booker List</h1>
            <p>Total number of bookers: ${bookerList.size()}</p>
            <div class="flex gap-2">
                <a href="/admin/bookers/add-booker"
                   class="bg-green-500 text-white font-medium px-4 py-2 rounded hover:bg-green-600 focus:outline-none focus:ring focus:ring-green-300">
                    Add booker
                </a>
                <button onclick="displayReminderEmailModal()"
                   class="bg-red-500 text-white font-medium px-4 py-2 rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">
                    Send reminder email to all bookers
                </button>
            </div>
        </div>

        @for(PresentationBookerWithParticipants booker: bookerList)
            <div class="bg-gray-150 p-4 rounded-md shadow my-1">
                <div class="flex justify-between">
                    <p class="mx-10">${booker.firstName() + " " + booker.lastName()} (${booker.email()})</p>
                    <div>
                        <a href="/admin/bookers/${booker.email()}"
                           class="bg-blue-500 text-white px-3 py-1 rounded hover:bg-blue-600 focus:outline-none focus:ring focus:ring-blue-300">
                            Edit
                        </a>
                            <a href="/admin/bookings/${booker.emailSignature()}"
                               class="bg-green-500 text-white px-3 py-1 rounded hover:bg-green-600 focus:outline-none focus:ring focus:ring-green-300">
                                Checkin
                            </a>
                    </div>
                </div>

                !{var participants = booker.participants(); }
                @if(!participants.isEmpty())
                    @for(Event event: allEvents)
                        @if(participants.get(event) != null && !participants.get(event).isEmpty())
                            <div class="mt-2 ml-12 text-sm">
                                    <p class="font-semibold">Participants for ${event.getTitle()}:</p>
                                    <ul class="list-disc ml-6">
                                        @for(ActivityParticipant participant : participants.get(event))
                                            <li class="flex justify-between items-center">
                                                <span>${participant.getParticipant().getFirstName()} ${participant.getParticipant().getLastName()} - ${participant.getActivity().getTitle()}</span>
                                            </li>
                                        @endfor
                                    </ul>
                            </div>
                        @endif
                    @endfor
                @endif
            </div>
        @endfor
    </div>
</div>
`)

<script>
    function displayReminderEmailModal() {
        // Create the modal elements
        const modal = document.createElement("div");
        modal.id = "reminder-email-modal";
        modal.className = "fixed inset-0 flex items-center justify-center bg-black/50 z-50";
        @raw
        modal.innerHTML = `
        <div class="bg-white rounded-lg shadow-md p-6 w-96">
            <h2 class="text-lg font-semibold text-gray-800 mb-4">Send Reminder Email</h2>
            <p class="text-gray-800 text-sm mb-4">
                Are you sure you want to send a reminder email to all bookers?
            </p>
            <div id="modal-message" class="text-green-500 text-sm mb-4 hidden"></div>
            <div class="flex justify-end gap-2">
                <button type="button" onclick="closeReminderEmailModal()" class="bg-gray-300 text-gray-700 px-3 py-1 rounded hover:bg-gray-400 focus:outline-none focus:ring">Cancel</button>
                <button type="button" onclick="sendReminderEmail()" class="bg-red-500 text-white px-3 py-1 rounded hover:bg-red-600 focus:outline-none focus:ring focus:ring-red-300">Send</button>
            </div>
        </div>
        `;
        @endraw
        // add the modal in DOM
        document.body.appendChild(modal);
    }

    function closeReminderEmailModal() {
        const modal = document.getElementById("reminder-email-modal");
        if (modal) {
            modal.remove();
        }
    }

    async function sendReminderEmail() {
        try {
            const sendButton = document.querySelector("#reminder-email-modal button[onclick='sendReminderEmail()']");
            const cancelButton = document.querySelector("#reminder-email-modal button[onclick='closeReminderEmailModal()']");
            const modalMessage = document.getElementById("modal-message");

            // Disable buttons while processing
            sendButton.disabled = true;
            cancelButton.disabled = true;

            const response = await fetch("/admin/bookers/sendReminderEmail", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                }
            });

            if (response.status === 201) {
                modalMessage.textContent = "Successfully triggered reminder emails";
                modalMessage.classList.remove("hidden");
                cancelButton.disabled = false;
                sendButton.disabled = true;
            } else {
                modalMessage.textContent = "Error sending reminder emails. Please try again.";
                modalMessage.classList.remove("hidden");
                modalMessage.classList.add("text-red-500");
                modalMessage.classList.remove("text-green-500");
                sendButton.disabled = false;
                cancelButton.disabled = false;
            }
        } catch (error) {
            console.error("Error sending reminder emails:", error);
            const modalMessage = document.getElementById("modal-message");
            modalMessage.textContent = "An error occurred. Please try again.";
            modalMessage.classList.remove("hidden");
            modalMessage.classList.add("text-red-500");
            modalMessage.classList.remove("text-green-500");

            // Re-enable buttons
            document.querySelector("#reminder-email-modal button[onclick='sendReminderEmail()']").disabled = false;
            document.querySelector("#reminder-email-modal button[onclick='closeReminderEmailModal()']").disabled = false;
        }
    }
</script>
