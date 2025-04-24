package org.montrealjug.billetterie.email;

import jakarta.mail.MessagingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.montrealjug.billetterie.email.EmailModel.EmailToSend;

interface EmailSender {

    Logger LOGGER = LoggerFactory.getLogger(EmailSender.class.getName());

    void send(EmailToSend emailToSend) throws MessagingException;

    EmailSender NO_OP = emailToSend -> LOGGER.info("sending email: {}", emailToSend);

}
