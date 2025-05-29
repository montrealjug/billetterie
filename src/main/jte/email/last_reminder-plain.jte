@import org.montrealjug.billetterie.email.EmailModel.LastReminderEmail
@import org.montrealjug.billetterie.ui.PresentationActivityParticipant

@param LastReminderEmail context

Participation à ${context.event().getTitle()}

Bonjour ${context.booker().getFirstName()} ${context.booker().getLastName()},

Ceci est le dernier rappel concernant votre participation au prochain Devoxx4KidsQC, qui aura lieu à ${context.event().getLocation()}.

Au moment d'envoyer ce courriel, nous confirmons les inscriptions suivantes :

@for(PresentationActivityParticipant activityParticipant: context.participants())
    !{var participant = activityParticipant.participant();}
    !{var activity = activityParticipant.presentationActivity();}
    @if(activityParticipant.isWaiting())
        ${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} ans) - SUR LISTE D'ATTENTE
    @else
        ${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} ans)
    @endif
@endfor

Plus possible pour vous de venir? S'il vous plaît, veuillez mettre à jour votre participation en cliquant sur ce lien: ${context.registrationLink()}

Cela permettra d’offrir la place à un autre enfant et de garantir à nos bénévoles que ceux-ci ne viendront pas pour rien.

Merci de bien vérifier l'heure de début et arriver un peu en avance.

Des enfants sur liste d'attente? Tout n'est pas perdu, il est encore possible que de la place se libère d'ici Samedi; vous pouvez vérifier à tout moment le statut de vos inscriptions en cliquant sur le lien précédent.

Cependant, si vous avez un enfant encore sur liste d'attente Samedi, nous ne pouvons garantir son accès à l'activité; merci de votre compréhension.

L'équipe Devoxx4Kids Québec.

---
English version:
${context.event().getTitle()} participation

Hi ${context.booker().getFirstName()} ${context.booker().getLastName()},

This is the last reminder regarding your participation to the next Devoxx4KidsQC, that will take place at ${context.event().getLocation()}.

At time of sending this email, these were the bookings we had for you in our system:

@for(PresentationActivityParticipant activityParticipant: context.participants())
    !{var participant = activityParticipant.participant();}
    !{var activity = activityParticipant.presentationActivity();}
    @if(activityParticipant.isWaiting())
        ${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} year old) - ON WAITING LIST
    @else
        ${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} year old)
    @endif
@endfor

No longer possible for you or one of your children to come? Please update your registration, clicking on this link: ${context.registrationLink()}

This will allow another child to take your place and ensure that our volunteers’ efforts are not in vain.

Thanks for double-checking the starting time of the activity and coming a little bit early.

You have children on waiting list? There's still a possibility to have seats free up by Saturday; in any case you can click on the previous link to check the updated status of your registration.

However, if you have a child still on waiting list on Saturday, we can not guarantee their access to the activity; thank you for your understanding.

The Devoxx4Kids Québec team.
