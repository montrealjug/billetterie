// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.montrealjug.billetterie.email.EmailModel.Email;
import org.montrealjug.billetterie.email.EmailModel.EmailToSend;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock SmtpEmailSender emailSender;

    @Mock EmailWriter emailWriter;

    @Mock Email email;

    @Mock EmailToSend emailToSend;

    EmailService emailService;

    @BeforeEach
    void setUp() {
        emailService = new EmailService(emailSender, emailWriter);
    }

    @Test
    void sendEmail_should_call_emailSender_send_with_the_result_of_emailWriter_write()
            throws Exception {
        when(emailWriter.write(email)).thenReturn(emailToSend);

        emailService.sendEmail(email);

        verify(emailWriter).write(email);
        verify(emailSender).send(emailToSend);
    }

    @Test
    void sendEmail_should_catch_any_exception_thrown_by_emailWriter() {
        var exception = new NullPointerException("...null?");
        when(emailWriter.write(email)).thenThrow(exception);

        assertThatCode(() -> emailService.sendEmail(email)).doesNotThrowAnyException();
    }

    @Test
    void sendEmail_should_catch_any_exception_thrown_by_emailSender() throws Exception {
        when(emailWriter.write(email)).thenReturn(emailToSend);
        var exception = new NullPointerException("...null?");
        doThrow(exception).when(emailSender).send(emailToSend);

        assertThatCode(() -> emailService.sendEmail(email)).doesNotThrowAnyException();
    }
}
