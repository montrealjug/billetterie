@import org.montrealjug.billetterie.email.EmailModel.ParticipantUpgraded

@param ParticipantUpgraded context
!{var participant = context.participant().participant();}

Participation à ${context.event().getTitle()}

Bonjour ${context.booker().getFirstName()} ${context.booker().getLastName()},

Une place s'est libérée pour un enfant participant à Devoxx4Kids.

${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} ans)


Vérifier ou modifier la participation : ${context.registrationLink()}

L'équipe Devoxx4Kids Québec.

English version:
${context.event().getTitle()} participation

Hi ${context.booker().getFirstName()} ${context.booker().getLastName()},
A seat freed up for one the children you registered.

${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} years old)

View or update the participation : ${context.registrationLink()}
The Devoxx4Kids Québec team.
