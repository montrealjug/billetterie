@import org.montrealjug.billetterie.entity.ActivityParticipant
@import org.montrealjug.billetterie.ui.PresentationEvent
@import org.montrealjug.billetterie.ui.PresentationActivity
@import java.time.format.DateTimeFormatter
@import java.util.stream.Stream

@param PresentationEvent event

@template.layouts.admin_layout(title = "Event Status", content = @`
    <div class="flex flex-col items-center justify-center">
        <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">

            <div class="bg-gray-100 p-4 rounded-md shadow">

                <!-- Activities Table -->
                <div class="mt-4">
                    <h3 class="text-md font-semibold text-gray-700 mb-2">Activities</h3>

                    @for(PresentationActivity activity: event.activities())
                        <div class="mb-6 border border-gray-300 rounded-md">
                            <div class="bg-gray-200 p-3 rounded-t-md">
                                <h4 class="font-semibold">${activity.title()}</h4>
                                <p class="text-sm text-gray-600">${activity.time().format(DateTimeFormatter.ofPattern("H:mm a"))}</p>
                                <p class="text-sm text-gray-600">Participants: ${activity.participants().size()}/${activity.maxParticipants()}</p>
                                <p class="text-sm text-gray-600">Waiting: ${activity.waitingParticipants().size()}/${activity.maxWaitingQueue()}</p>
                            </div>


                            !{var allParticipants = Stream.concat(activity.participants().stream(), activity.waitingParticipants().stream()).toList();}

                            <div class="p-3">
                                <h5 class="font-medium mb-2">Participants:</h5>
                                <ul class="list-disc pl-5">
                                    <!-- Regular participants -->
                                    @if(!allParticipants.isEmpty())
                                        <div class="mb-2">
                                            <span class="font-medium">Regular participants:</span>
                                            @for(ActivityParticipant activityParticipant : allParticipants)
                                                !{var participant = activityParticipant.getParticipant(); }
                                                <li class="mb-1">
                                                    ${participant.getFirstName()} ${participant.getLastName().substring(0,1)}, aged ${participant.getAge()} -
                                                    Parent: ${participant.getBooker().getFirstName()} ${participant.getBooker().getLastName().substring(0,1)}
                                                    @if(activityParticipant.getCheckInTime() != null)
                                                        <span class="text-green-500">✅</span>
                                                    @else
                                                        <span class="text-red-500">❌</span>
                                                    @endif
                                                </li>
                                            @endfor
                                        </div>
                                    @endif


                                    <!-- No participants message -->
                                    @if(activity.participants().isEmpty() && activity.waitingParticipants().isEmpty())
                                        <li>No participants yet</li>
                                    @endif
                                </ul>
                            </div>
                        </div>
                    @endfor
                </div>
            </div>
        </div>
    </div>

    <script>
        // Auto refresh the page every 10 seconds
        setTimeout(function() {
            location.reload();
        }, 10000);
    </script>
`)
