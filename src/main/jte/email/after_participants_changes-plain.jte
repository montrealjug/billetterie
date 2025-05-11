@import org.montrealjug.billetterie.email.EmailModel.AfterParticipantsChangesEmail
@import org.montrealjug.billetterie.ui.PresentationActivityParticipant

@param AfterParticipantsChangesEmail context

Participation à ${context.event().getTitle()}

Bonjour ${context.booker().getFirstName()} ${context.booker().getLastName()},
Vous avez mis à jour la liste d'enfants participant à Devoxx4Kids

@for(PresentationActivityParticipant activityParticipant: context.participants())
    !{var participant = activityParticipant.participant();}
    !{var activity = activityParticipant.presentationActivity();}
    @if(activityParticipant.isWaiting())
        ${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} ans) - SUR LISTE D'ATTENTE
    @else
        ${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} ans)
    @endif
@endfor

Vérifier ou modifier la participation : ${context.registrationLink()}

L'équipe Devoxx4Kids Québec.

English version:
${context.event().getTitle()} participation

Hi ${context.booker().getFirstName()} ${context.booker().getLastName()},
You have updated the list of children participating in the next event.
@for(PresentationActivityParticipant activityParticipant: context.participants())
    !{var participant = activityParticipant.participant();}
    !{var activity = activityParticipant.presentationActivity();}
    @if(activityParticipant.isWaiting())
        ${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} year old) - ON WAITING LIST
    @else
        ${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} year old)
    @endif
@endfor


View or update the participation : ${context.registrationLink()}
The Devoxx4Kids Québec team.
