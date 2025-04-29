@import org.montrealjug.billetterie.email.EmailModel.ReturningBookingEmail

@param ReturningBookingEmail context
!{var title = "Dernière étape avant de réserver une activité avec Devoxx4Kids Québec/Last step before booking an activity with Devoxx4Kids Québec";}
!{var h1 = "Vous y êtes presque ! / You're almost there!";}
@template.layouts.email_html_layout(
title = title,
h1 = h1,
mainContent = @`
  <table class="main-table" lang="fr-CA" xml:lang="fr-CA">
    <tbody>
    <tr>
      <td>
        Bonjour ${context.booker().getFirstName()} ${context.booker().getLastName()},
      </td>
    </tr>
    <tr>
      <td>Merci de votre intérêt pour notre prochain événement !</td>
    </tr>
    <tr>
      <td>Si c'est bien vous qui souhaitez réserver une activité, merci d'utiliser le lien suivant :</td>
    </tr>
    <tr>
      <td class="with-top-bottom-padding">
        <a href="${context.registrationLink()}"
           class="link-button"
           target="_blank"
        >
          @raw
          <!--[if mso]><i style="mso-font-width:80%;mso-text-raise:40%" hidden>&emsp;</i><span style="mso-text-raise:20%;"><![endif]-->
          Réserver une activité
          <!--[if mso]></span><i style="mso-font-width:80%;" hidden>&emsp;&#8203;</i><![endif]-->
          @endraw
        </a>
      </td>
    </tr>
    <tr>
      <td class="italic">Après avoir cliqué sur ce lien, vous pourrez inscrire votre(vos) enfant(s) aux différentes activités du prochain événement</td>
    </tr>
    <tr>
      <td class="italic">L'équipe Devoxx4Kids Québec</td>
    </tr>
    </tbody>
  </table>
  <p class="english-version">English version:</p>
  <table class="main-table" lang="en-CA" xml:lang="en-CA">
    <tbody>
    <tr>
      <td>
        Hi ${context.booker().getFirstName()} ${context.booker().getLastName()},
      </td>
    </tr>
    <tr>
      <td>Thanks for your interest for our next event!</td>
    </tr>
    <tr>
      <td>
        If it was you who wanted to book an activity, then please use the following link:
      </td>
    </tr>
    <tr>
      <td class="with-top-bottom-padding">
        <a href="${context.registrationLink()}"
           class="link-button"
           target="_blank"
        >
          @raw
          <!--[if mso]><i style="mso-font-width:80%;mso-text-raise:40%" hidden>&emsp;</i><span style="mso-text-raise:20%;"><![endif]-->
          Book an activity
          <!--[if mso]></span><i style="mso-font-width:80%;" hidden>&emsp;&#8203;</i><![endif]-->
          @endraw
        </a>
      </td>
    </tr>
    <tr>
      <td class="italic">After you click on the link, you'll be able to book activities for your child(ren)</td>
    </tr>
    <tr>
      <td class="italic">The Devoxx4Kids Québec team</td>
    </tr>
    <!-- ghost line to avoid a strange border in Outlook classic 🤷-->
    <tr>
      <td style="padding: 0; line-height: 1em;">&nbsp;</td>
    </tr>
    </tbody>
  </table>
`)

