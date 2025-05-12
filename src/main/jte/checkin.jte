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
title = "Check-In Participants",
content = @`
<div class="flex flex-col items-center justify-center-safe h-full w-full bg-gray-100 py-2 md:p-2 lg:p-4">
    @if(error != null)
        <div class="bg-red-500 text-white p-4 mb-4 rounded-md w-full max-w-4xl">
            ${error}
        </div>
    @endif

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
                <!-- Activities Section -->
                <div class="mt-4">
                    <h3 class="text-md font-semibold text-gray-700 mb-2">Activities</h3>
                    <div class="bg-white rounded md:rounded-md md:shadow">
                        @for(PresentationActivity activity: event.activities())
                            !{var regularParticipantsNumber = activity.participants().size(); }
                            !{var waitingParticipantsNumber = activity.waitingParticipants().size(); }

                            @if (regularParticipantsNumber > 0 || waitingParticipantsNumber > 0)
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
                                    </div>
                                </article>

                                <div id="participants-container-${activity.id()}" class="px-4 py-2 bg-gray-50 mb-4 ml-4 mr-4 rounded">
                                    <div class="text-sm font-semibold mb-2">Participants:</div>
                                    <ul id="participants-list-${activity.id()}" class="list-disc pl-5">
                                        <!-- Display existing regular participants -->
                                        @if(regularParticipantsNumber != 0)
                                            <div class="mb-2">
                                                <span class="font-medium">Regular participants:</span>
                                                @for(ActivityParticipant activityParticipant : activity.participants())
                                                    !{var participant = activityParticipant.getParticipant(); }
                                                    <li class="flex items-center">
                                                        <input type="checkbox"
                                                               id="checkin-${activity.id()}-${participant.getId()}"
                                                               class="mr-2 checkin-checkbox"
                                                               data-activity-id="${activity.id()}"
                                                               data-participant-id="${participant.getId()}"
                                                               checked="${activityParticipant.getCheckInTime() != null}"
                                                        >
                                                        <label for="checkin-${activity.id()}-${participant.getId()}">
                                                            ${participant.getFirstName()} ${participant.getLastName()}, born in ${participant.getYearOfBirth()}
                                                        </label>
                                                    </li>
                                                @endfor
                                            </div>
                                        @endif

                                        <!-- Display waiting participants -->
                                        @if(waitingParticipantsNumber != 0)
                                            <div>
                                                <span class="font-medium text-orange-600">Waiting list:</span>
                                                @for(ActivityParticipant activityParticipant : activity.waitingParticipants())
                                                    !{var participant = activityParticipant.getParticipant(); }
                                                    <li class="flex items-center text-orange-600">
                                                        <input type="checkbox"
                                                               id="checkin-${activity.id()}-${participant.getId()}"
                                                               class="mr-2 checkin-checkbox"
                                                               data-activity-id="${activity.id()}"
                                                               data-participant-id="${participant.getId()}"
                                                               checked="${activityParticipant.getCheckInTime() != null}"
                                                               >
                                                        <label for="checkin-${activity.id()}-${participant.getId()}">
                                                            ${participant.getFirstName()} ${participant.getLastName()}, born in ${participant.getYearOfBirth()} (waiting)
                                                        </label>
                                                    </li>
                                                @endfor
                                            </div>
                                        @endif
                                    </ul>
                                </div>
                            @endif

                        @endfor
                    </div>
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
    document.addEventListener('DOMContentLoaded', function() {
        // Add event listeners to all checkboxes
        const checkboxes = document.querySelectorAll('.checkin-checkbox');
        checkboxes.forEach(checkbox => {
            checkbox.addEventListener('change', handleCheckInChange);
        });
    });

    async function handleCheckInChange(event) {
        const checkbox = event.target;
        const activityId = checkbox.getAttribute('data-activity-id');
        const participantId = checkbox.getAttribute('data-participant-id');
        const isChecked = checkbox.checked;

        try {
            const response = await fetch('/admin/bookings/checkin', {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    activityId: parseInt(activityId),
                    participantId: parseInt(participantId),
                    checked: isChecked
                })
            });

            if (response.ok) {
                const data = await response.json();
                alert(data.message);
            } else {
                const errorData = await response.json();
                alert(errorData.message || "Failed to update check-in status");
                // Revert checkbox state if the operation failed
                checkbox.checked = !isChecked;
            }
        } catch (error) {
            console.error("Error during check-in:", error);
            alert("An error occurred while updating check-in status. Please try again.");
            // Revert checkbox state if the operation failed
            checkbox.checked = !isChecked;
        }
    }
</script>
`)
