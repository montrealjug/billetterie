@import org.montrealjug.billetterie.email.EmailModel.AfterRegistrationEmail

@param AfterRegistrationEmail context
Devoxx4Kids Québec, confirmation d'inscription à une activité (English version will follow):

Bonjour ${context.booker().getFirstName()} ${context.booker().getLastName()},
Vous avez partagé votre adresse courriel avec Devoxx4Kids Québec
Si c'est bien vous qui avez effectué l'action, merci de confirmer votre courriel en cliquant sur ce lien :${context.registrationLink()}

Après avoir cliqué sur ce lien, vous pourrez inscrire votre(vos) enfant(s) aux différentes activités du prochain événement.
L'équipe Devoxx4Kids Québec.

English version:
Devoxx4Kids Québec, activity booking confirmation:

Hi ${context.booker().getFirstName()} ${context.booker().getLastName()},
You have shared your email address with Devoxx4Kids Québec
If it was you who registered the email address, then please confirm it following this link: ${context.registrationLink()}

After you click on the link, you'll be able to book activities for your child(ren).
The Devoxx4Kids Québec team.
