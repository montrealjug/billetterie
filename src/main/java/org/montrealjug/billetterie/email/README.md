# Email module

Encapsulate logic to send emails.

## How to use it in code to send email(s)

Inject an instance of `EmailService`.

Build the `Email`(s) using one of the `static` factory methods and call `sendEmail` or `sendEmails`.

```java
import org.montrealjug.billetterie.email.EmailModel.Email;
import org.montrealjug.billetterie.email.EmailService;
import org.montrealjug.billetterie.entity.ActivityParticipant;
import org.montrealjug.billetterie.entity.Event;
import org.montrealjug.billetterie.entity.Participant;

class MyService {

 private final EmailService emailService;

 //...
 public void doStuff(Booker booker, Event event, Set<ActivityParticipant> participants) {
  // ...
  var email = Email.afterBooking(booker, event, participants);
  emailService.sendEmail(email);
  //...
 }
 //...
}
```
Both `sendEmail` and batch `sendEmails` are asynchronous, so they will return immediately while logging any `Exception`.

## How to add an EmailType (aka a new email model)

The model is exposed in a *model* `class`: [`EmailModel`](./EmailModel.java).

It contains an `enum` `EmailType` that enforces a convention to link an `EmailType` with two `jte` templates,
one for `text/plain`, another for `text/html`, and a `key` in the `messages.properties` default bundle for the email subject. 

This convention is that for a member `MY_EMAIL_TYPE` of the `enum` `EmailType`:
- the key in the bundle for the subject is `my_email_type`
- the `text/html` template will be resolved in the default `jte` package at `email/my_email_type-html.jte` 
- the `text/plain` template will be resolved in the default `jte` package at `email/my_email_type-plain.jte`

To provide a `context` to your template, you must define an implementation of `Email`.

This implementation will provide an `EmailType` via its 
 `type()` method.  
Each template (`text/html` and `text/plain`) associated with this `EmailType` will be called with the `Email` instance as `@param` to build the content of the email.  
The email will be sent to the `InternetAddress` returned by the `to()` method of your implementation.

The easiest way to define an implementation is to use a `record` to wrap the data you need as it is done with `AfterBookingEmail` 
in the *model* `class` [`EmailModel`](./EmailModel.java).  
To avoid using each `Email` implementation outside of this module, add a `static` factory `method` as `Email.afterBooking()`.  
This implementation must be `public` for the import in the template to work (see [`after_booking-plain.jte`](../.././../../../jte/email/after_booking-plain.jte)).

To have coherent `html` emails, a common `layout` is available at [`email_html_layout.jte.jte`](../.././../../../jte/layouts/email_html_layout.jte).  
This common `layout` has 4 parameters:
- a mandatory `title` for the `html` `title` (in the `head` of the document)
- a mandatory `h1`... for the `h1` of the document
- a mandatory `mainContent`: this is a `Content` from `jte` API that will be inlined in the `main` part of the document
- an optional `additionalHeadContent`: this is a `Content` from `jte` API that will be inlined, if defined, at the end of the `head` tag in the document

See the [`Content` part of the `jte` documentation for more](https://jte.gg/syntax/#content).

An exemple of usage can be found in [`after_booking-html.jte`](../.././../../../jte/email/after_booking-html.jte).

While not being mandatory, the use of this common `layout` is encouraged to:
- ease development and maintenance of new templates
- ensure a graphic coherence between our emails

### How to test your new Email type

The test class [`EmailWriterTest`](../.././../../../../test/java/org/montrealjug/billetterie/email/EmailWriterTest.java) is built to ease validation of template executions.

To add your new `EmailType` in the test suite, build an instance of your `Email` and add it in the `Stream` returned by the `emails` method of the test.

The generated content will be validated against:
- a `test resource` file `my_email_type.html` for `text/html` (content will be compared after `Jsoup.parse(content).html()` to avoid formatting issues)
- a `test resource` file `my_email_type.txt` for `text/plain` (content will be trimmed before comparaison to avoid formatting issues)

## How to test locally

The `local` profile deactivate email emission by setting the property `app.mail.mode` to `no-op`.  
Running the app with the `local` profile will send emails to the `console` via logs.

Another profile has been added for both `spring` and `docker compose` configuration: `mailhog`.  
Because this `mailhog` profile should be used on top of the `local` profile and [some limitations on dynamic profile registration](https://docs.spring.io/spring-boot/reference/features/profiles.html) in `SpringBoot`,
a [`group`](https://docs.spring.io/spring-boot/reference/features/profiles.html#features.profiles.groups) `local-mail` is available to set everything up in one shot:
```shell
./mvnw spring-boot:run -Dspring-boot.run.profiles=local-mail
```

When activated in `spring` (from the command line or from your `IDE`) this `local-mail` profile will:
- activate the `local` and the `mailhog` profile in the expected order
- start a [`MailHog`](https://github.com/mailhog/MailHog) container listening for `SMTP` on port `21025` and exposing its web-ui on port `28025`
- configure the app to send emails to this `MailHog` container
- exposes a `rest controller` to send emails on demand (see [`TestMailController`](./TestMailController.java))

Calling the [`TestMailController`](./TestMailController.java):
```shell
http POST http://localhost:8080/test/email/after-booking
```
will send an email that can be seen in `MailHog` web-ui at [`http://localhost:28025`](http://localhost:28025).

The web-ui lets us check the conformity of the email.

**BUT** html email is a big mess! 

Especially `Outlook (classic)` is a massive **PAIN** because it uses `Word` html rendering engine (😱🤦🤷).  
This situation has spawned an entire industry of tools and services to ensure that `Outlook (classic)` behaves as expected.  
Don't go through this rabbit hole, there's nothing valuable to learn there.  
If you're lucky enough to never have suffered the `IE` pain to code webapps, you can experiment it with `html` email and `Outlook (classic)`. Trigger warning for everyone that has suffered the `IE` pain coding webapps a decade ago...
The only good news is that `Outlook (new)` almost follows standards...

To check rendering in an email client:
- send an email to the `MailHog` container (you can use the [`TestMailController`](./TestMailController.java))
- open this email in [`MailHog` web-ui](http://localhost:28025) (select the email in the list)
- go to the `Source` tab, copy its content to a local `.eml` file (i.e. `my-test.eml`).
- open this `.eml` in a local mail client and check the rendering in this client.

You can edit the `html` part of the `.eml` directly with your favorite text editor, save and check again in your local mail client.
When you're satisfied with the result, backport your changes in the associated `jte` template, update your expected `html` file in the test resources, check again the result via `MailHog`...

You know understand the rant about `Outlook` html rendering...
