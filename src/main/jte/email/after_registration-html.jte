@import org.montrealjug.billetterie.email.EmailModel.AfterRegistrationEmail

@param AfterRegistrationEmail context
!{var title = "Confirmation d'inscription à Devoxx4Kids Québec/Registration confirmation for Devoxx4Kids Québec";}
!{var h1 = "Courriel bien enregistré ! /  Email successfully registered";}
@template.layouts.email_html_layout(
  title = title,
  h1 = h1,
  additionalHeadContent = @`
  `,
  mainContent = @`
  <table class="main-table" lang="fr-CA" xml:lang="fr-CA">
    <tbody>
    <tr>
      <td>
        Bonjour ${context.booker().getFirstName()} ${context.booker().getLastName()},
      </td>
    </tr>
    <tr>
      <td>Vous avez partagé votre adresse courriel avec Devoxx4Kids Québec</td>
    </tr>
    <tr>
      <td>Si c'est bien vous qui avez effectué l'action, merci de confirmer votre courriel en cliquant sur ce lien :</td>
    </tr>
    <tr>
      <td class="with-top-bottom-padding">
        <a href="${context.registrationLink()}"
           class="link-button"
           target="_blank"
        >
          @raw
          <!--[if mso]><i style="mso-font-width:80%;mso-text-raise:40%" hidden>&emsp;</i><span style="mso-text-raise:20%;"><![endif]-->
          Confirmer mon courriel
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
      <td>You have shared your email address with Devoxx4Kids Québec</td>
    </tr>
    <tr>
      <td>
        If it was you who registered the email address, then please confirm it following this link:
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
          Confirm my email
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

