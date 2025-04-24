// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import java.util.Collection;
import org.montrealjug.billetterie.email.EmailModel.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;

public class EmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

    private final EmailSender emailSender;
    private final EmailWriter emailWriter;

    EmailService(EmailSender emailSender, EmailWriter emailWriter) {
        this.emailSender = emailSender;
        this.emailWriter = emailWriter;
    }

    @Async
    public void sendEmail(Email email) {
        try {
            var emailToSend = this.emailWriter.write(email);
            this.emailSender.send(emailToSend);
        } catch (Exception e) {
            LOGGER.error("error while sending email", e);
        }
    }

    @Async
    public void sendEmails(Collection<? extends Email> emails) {
        for (Email context : emails) {
            this.sendEmail(context);
        }
    }
}
