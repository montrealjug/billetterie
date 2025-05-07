// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import static org.montrealjug.billetterie.email.EmailModel.EmailToSend;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

interface EmailSender {
    Logger LOGGER = LoggerFactory.getLogger(EmailSender.class.getName());

    void send(EmailToSend emailToSend) throws MessagingException;

    EmailSender NO_OP = emailToSend -> LOGGER.info("sending email: {}", emailToSend);
}
