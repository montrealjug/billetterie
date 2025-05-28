@import java.util.List
@import org.montrealjug.billetterie.ui.PresentationBookerWithParticipants
@import org.montrealjug.billetterie.entity.Event
@import org.montrealjug.billetterie.entity.ActivityParticipant

@param List<PresentationBookerWithParticipants> bookerList
@param Event activeEvent

@template.layouts.admin_layout(title = "Booker List",content = @`

<div class="flex flex-col items-center justify-center">

    <div class="bg-white p-8 rounded shadow-md w-full max-w-4xl">
        <div class="flex flex-col items-center mb-6">
            <h1 class="text-2xl font-bold">Booker List</h1>
            <p>Total number of bookers: ${bookerList.size()}</p>
            <a href="/admin/bookers/add-booker"
               class="bg-green-500 text-white font-medium px-4 py-2 rounded hover:bg-green-600 focus:outline-none focus:ring focus:ring-green-300">
                Add booker
            </a>
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

                @if(activeEvent != null && !booker.participants().isEmpty())
                    <div class="mt-2 ml-12 text-sm">
                        <p class="font-semibold">Participants for ${activeEvent.getTitle()}:</p>
                        <ul class="list-disc ml-6">
                            @for(ActivityParticipant participant : booker.participants())
                                <li class="flex justify-between items-center">
                                    <span>${participant.getParticipant().getFirstName()} ${participant.getParticipant().getLastName()} - ${participant.getActivity().getTitle()}</span>
                                </li>
                            @endfor
                        </ul>
                    </div>
                @endif
            </div>
        @endfor
    </div>
</div>
`)
