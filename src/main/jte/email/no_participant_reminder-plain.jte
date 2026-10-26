@import org.montrealjug.billetterie.email.EmailModel.NoParticipantReminderEmail

@param NoParticipantReminderEmail context
Devoxx4Kids Québec, il est encore temps d'inscrire vos enfants (English version will follow):

Bonjour ${context.booker().getFirstName()} ${context.booker().getLastName()},
Le ${context.formatDateFr(context.bookerRegistrationTime())} à ${context.formatTime(context.bookerRegistrationTime())}, vous avez enregistré votre adresse courriel auprès de Devoxx4Kids Québec, mais vous n'avez encore inscrit aucun enfant à ${context.event().getTitle()}@if(context.event().getDate() != null), le ${context.formatDateFr(context.event().getDate())}@endif.
Il est encore temps de le faire ! Utilisez ce lien personnel pour inscrire votre(vos) enfant(s) aux activités : ${context.registrationLink()}

Vous ne souhaitez plus recevoir nos courriels, ou vous vous êtes inscrit(e) avec une autre adresse ? Vous pouvez supprimer cette adresse de notre système : ${context.deletionLink()}

L'équipe Devoxx4Kids Québec.

English version:
Devoxx4Kids Québec, there's still time to register your children:

Hi ${context.booker().getFirstName()} ${context.booker().getLastName()},
On ${context.formatDateEn(context.bookerRegistrationTime())} at ${context.formatTime(context.bookerRegistrationTime())}, you registered your email address with Devoxx4Kids Québec, but you haven't registered any child to ${context.event().getTitle()}@if(context.event().getDate() != null), on ${context.formatDateEn(context.event().getDate())}@endif yet.
There's still time! Use this personal link to register your child(ren) to the activities: ${context.registrationLink()}

You no longer want to receive our emails, or you registered with another address? You can delete this address from our system: ${context.deletionLink()}

The Devoxx4Kids Québec team.
