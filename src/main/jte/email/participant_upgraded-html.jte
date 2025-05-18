@import org.montrealjug.billetterie.email.EmailModel.ParticipantUpgraded

@param ParticipantUpgraded context
!{var title = "Participation à " + context.event().getTitle() + " participation";}
!{var h1 = "Un participant en liste d'attente est maintenant inscrit ! / A waiting-line participant had now a seat!";}
!{var participant = context.participant().participant();}

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
      <td>Une place s'est libérée pour un enfant participant à Devoxx4Kids</td>
    </tr>
    <tr>
      <td>
        <ul>
          <li>${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} ans)</li>
        </ul>
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
          Vérifier ou modifier la participation
          <!--[if mso]></span><i style="mso-font-width:80%;" hidden>&emsp;&#8203;</i><![endif]-->
          @endraw
        </a>
      </td>
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
      <td>A seat freed up for one the children you registered.</td>
    </tr>
    <tr>
      <td>
        <ul>
          <li>${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} ans)</li>
        </ul>
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
          View or update the participation
          <!--[if mso]></span><i style="mso-font-width:80%;" hidden>&emsp;&#8203;</i><![endif]-->
          @endraw
        </a>
      </td>
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

