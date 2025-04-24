@import org.montrealjug.billetterie.email.EmailModel.AfterBookingEmail
@import org.montrealjug.billetterie.entity.ActivityParticipant

@param AfterBookingEmail context
Devoxx4Kids Québec, confirmation d'inscription à une activité (English version will follow):

Bonjour ${context.booker().getFirstName()} ${context.booker().getLastName()},
Nous vous confirmons votre inscription pour l'événement Devoxx4kids ${context.event().getTitle()} le ${context.formatDateFr(context.event().getDate())}.
Lors de cet événement,
@for (ActivityParticipant registration : context.participants())
  ${registration.getParticipant().getFirstName()} ${registration.getParticipant().getLastName()}
  participera à l'activité:
  ${registration.getActivity().getTitle()} démarrant à ${context.formatTime(registration.getActivity().getStartTime())}
@endfor
Vous pouvez modifier votre inscription en utilisant le lien suivant: ${context.registrationLink()}

On s'y voit bientôt!
L'équipe Devoxx4Kids Québec.

English version:
Devoxx4Kids Québec, activity booking confirmation:

Hi ${context.booker().getFirstName()} ${context.booker().getLastName()},
We confirm your activity booking for the Devoxx4Kids event ${context.event().getTitle()} on ${context.formatDateEn(context.event().getDate())}.
During this event,
@for (ActivityParticipant registration : context.participants())
  ${registration.getParticipant().getFirstName()} ${registration.getParticipant().getLastName()}
  will attend the activity:
  ${registration.getActivity().getTitle()} starting at ${context.formatTime(registration.getActivity().getStartTime())}
@endfor
You can manage your booking using the following link: ${context.registrationLink()}

See you soon there!
The Devoxx4Kids Québec team.
