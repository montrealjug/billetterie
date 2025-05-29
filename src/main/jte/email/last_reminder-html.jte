@import org.montrealjug.billetterie.email.EmailModel.LastReminderEmail
@import org.montrealjug.billetterie.ui.PresentationActivityParticipant

@param LastReminderEmail context
!{var title = "Dernier rappel pour le prochain Devoxx4Kids Québec/Last reminder for the upcoming Devoxx4Kids Québec event";}
!{var h1 = "Dernière chance de mettre à jour votre participation";}
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
      <td>Ceci est le dernier rappel concernant votre participation au prochain Devoxx4KidsQC qui aura lieu à ${context.event().getLocation()}.</td>
    </tr>
    <tr>
      <td>Au moment d'envoyer ce courriel, nous confirmons les inscriptions suivantes :</td>
    </tr>
    <tr>
      <td>
        <ul>
          @for(PresentationActivityParticipant activityParticipant: context.participants())
            !{var participant = activityParticipant.participant();}
            !{var activity = activityParticipant.presentationActivity();}
            @if(activityParticipant.isWaiting())
              <li>${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} ans) - SUR LISTE D'ATTENTE</li>
            @else
              <li>${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} ans)</li>
            @endif
          @endfor
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
          Plus possible pour vous de venir? S'il vous plaît, veuillez mettre à jour votre participation en cliquant sur ce lien
          <!--[if mso]></span><i style="mso-font-width:80%;" hidden>&emsp;&#8203;</i><![endif]-->
          @endraw
        </a>  Cela permettra d’offrir la place à un autre enfant et de garantir à nos bénévoles que ceux-ci ne viendront pas pour rien.
      </td>
    </tr>
    <tr>
      <td>Merci de bien vérifier l'heure de début et arriver un peu en avance.</td>
    </tr>
    <tr>
      <td>Des enfants sur liste d'attente? Tout n'est pas perdu, il est encore possible que de la place se libère d'ici Samedi; vous pouvez vérifier à tout moment le statut de vos inscriptions en cliquant sur le lien précédent.
      </td>
    </tr>
    <tr>
      <td>Cependant, si vous avez un enfant encore sur liste d'attente Samedi, nous ne pouvons garantir son accès à l'activité; merci de votre compréhension.
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
      <td>This is the last reminder regarding your participation to the next Devoxx4KidsQC, that will take place at ${context.event().getLocation()}</td>
    </tr>
    <tr>
      <td>At time of sending this email, these were the bookings we had for you in our system:</td>
    </tr>
    <tr>
      <td>
        <ul>
          @for(PresentationActivityParticipant activityParticipant: context.participants())
            !{var participant = activityParticipant.participant();}
            !{var activity = activityParticipant.presentationActivity();}
            @if(activityParticipant.isWaiting())
              <li>${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} year old) - ON WAITING LIST</li>
            @else
              <li>${participant.getFirstName()} ${participant.getLastName()} (${participant.getAge()} year old)</li>
            @endif
          @endfor
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
          No longer possible for you or one of your children to come? Please update your registration, clicking on this link
          <!--[if mso]></span><i style="mso-font-width:80%;" hidden>&emsp;&#8203;</i><![endif]-->
          @endraw
        </a> This will allow another child to take your place and ensure that our volunteers’ efforts are not in vain.
      </td>
    </tr>
    <tr>
      <td>Thanks for double-checking the starting time of the activity and coming a little bit early.</td>
    </tr>
    <tr>
      <td>You have children on waiting list? There's still a possibility to have seats free up by Saturday; in any case you can click on the previous link to check the updated status of your registration. </td>
    </tr>
    <tr>
      <td>However, if you have a child still on waiting list on Saturday, we can not guarantee their access to the activity; thank you for your understanding.</td>
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


