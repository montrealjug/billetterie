@import org.montrealjug.billetterie.email.EmailModel.ReturningBookingEmail

@param ReturningBookingEmail context
Devoxx4Kids Québec, dernière étape avant de réserver une activité (English version will follow):

Bonjour ${context.booker().getFirstName()} ${context.booker().getLastName()},
Merci de votre intérêt pour notre prochain événement !
Si c'est bien vous qui souhaitez réserver une activité, merci d'utiliser le lien suivant : ${context.registrationLink()}

Après avoir cliqué sur ce lien, vous pourrez inscrire votre(vos) enfant(s) aux différentes activités du prochain événement.
L'équipe Devoxx4Kids Québec.

English version:
Devoxx4Kids Québec, last step before booking an activity:

Hi ${context.booker().getFirstName()} ${context.booker().getLastName()},
Thanks for your interest for our next event!
If it was you who wanted to book an activity, then please use the following link: ${context.registrationLink()}

After you click on the link, you'll be able to book activities for your child(ren).
The Devoxx4Kids Québec team.
