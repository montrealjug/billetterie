@import org.montrealjug.billetterie.email.EmailModel.NoParticipantReminderEmail

@param NoParticipantReminderEmail context
!{var title = "Il est encore temps d'inscrire vos enfants à Devoxx4Kids Québec/There's still time to register your children for Devoxx4Kids Québec";}
!{var h1 = "Il est encore temps ! / There's still time!";}
!{var registrationTime = context.bookerRegistrationTime();}
!{var eventDate = context.event().getDate();}
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
      <td>
        Le <time datetime="${context.formatForDateTimeAttribute(registrationTime)}">${context.formatDateFr(registrationTime)} à ${context.formatTime(registrationTime)}</time>,
        vous avez enregistré votre adresse courriel auprès de Devoxx4Kids Québec, mais vous n'avez encore inscrit aucun enfant à
        <strong>${context.event().getTitle()}</strong>@if(eventDate != null), le <time datetime="${context.formatForDateTimeAttribute(eventDate)}">${context.formatDateFr(eventDate)}</time>@endif.
      </td>
    </tr>
    <tr>
      <td>Il est encore temps de le faire ! Utilisez ce lien personnel pour inscrire votre(vos) enfant(s) aux activités :</td>
    </tr>
    <tr>
      <td class="with-top-bottom-padding">
        <a href="${context.registrationLink()}"
           class="link-button"
           target="_blank"
        >
          @raw
          <!--[if mso]><i style="mso-font-width:80%;mso-text-raise:40%" hidden>&emsp;</i><span style="mso-text-raise:20%;"><![endif]-->
          Inscrire mes enfants
          <!--[if mso]></span><i style="mso-font-width:80%;" hidden>&emsp;&#8203;</i><![endif]-->
          @endraw
        </a>
      </td>
    </tr>
    <tr>
      <td>
        <span class="italic-grey">Vous ne souhaitez plus recevoir nos courriels, ou vous vous êtes inscrit(e) avec une autre adresse ?
        Vous pouvez <a href="${context.deletionLink()}" target="_blank">supprimer cette adresse de notre système</a>.</span>
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
      <td>
        On <time datetime="${context.formatForDateTimeAttribute(registrationTime)}">${context.formatDateEn(registrationTime)} at ${context.formatTime(registrationTime)}</time>,
        you registered your email address with Devoxx4Kids Québec, but you haven't registered any child to
        <strong>${context.event().getTitle()}</strong>@if(eventDate != null), on <time datetime="${context.formatForDateTimeAttribute(eventDate)}">${context.formatDateEn(eventDate)}</time>@endif yet.
      </td>
    </tr>
    <tr>
      <td>There's still time! Use this personal link to register your child(ren) to the activities:</td>
    </tr>
    <tr>
      <td class="with-top-bottom-padding">
        <a href="${context.registrationLink()}"
           class="link-button"
           target="_blank"
        >
          @raw
          <!--[if mso]><i style="mso-font-width:80%;mso-text-raise:40%" hidden>&emsp;</i><span style="mso-text-raise:20%;"><![endif]-->
          Register my children
          <!--[if mso]></span><i style="mso-font-width:80%;" hidden>&emsp;&#8203;</i><![endif]-->
          @endraw
        </a>
      </td>
    </tr>
    <tr>
      <td>
        <span class="italic-grey">You no longer want to receive our emails, or you registered with another address?
        You can <a href="${context.deletionLink()}" target="_blank">delete this address from our system</a>.</span>
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
