// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.AdditionalMatchers.not;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.mail.Address;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.Part;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.montrealjug.billetterie.email.EmailConfiguration.EmailProperties;
import org.montrealjug.billetterie.email.EmailModel.EmailToSend;
import org.springframework.mail.javamail.JavaMailSender;

@ExtendWith(MockitoExtension.class)
class SmtpEmailSenderTest {

    private static final EmailToSend TEST_DATA = testData();
    private static final EmailProperties EMAIL_PROPERTIES = EmailTestHelper.emailProperties();

    @Mock
    JavaMailSender javaMailSender;

    @Mock
    MimeMessage mimeMessage;

    SmtpEmailSender emailSender;

    @BeforeEach
    void setUp() {
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage).thenReturn(mock(MimeMessage.class));
        doNothing().when(javaMailSender).send(same(mimeMessage));
        lenient()
            .doThrow(new IllegalArgumentException("unexpected message send!"))
            .when(javaMailSender)
            .send(not(same(mimeMessage)));
        this.emailSender = new SmtpEmailSender(javaMailSender, EMAIL_PROPERTIES);
    }

    @Test
    void test_mock_setup() {
        // first call of this method should return our mock
        assertThat(javaMailSender.createMimeMessage()).isSameAs(mimeMessage);
        // second call of this method should NOT return our mock (just to ensure it's been called
        // once)
        assertThat(javaMailSender.createMimeMessage()).isNotNull().isNotSameAs(mimeMessage);
        // calling send with another message than our mock should throw expected Exception
        assertThatThrownBy(() -> this.javaMailSender.send(mock(MimeMessage.class)))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("unexpected message send!");
        // calling send with our mock message should not throw any Exception
        assertThatCode(() -> this.javaMailSender.send(mimeMessage)).doesNotThrowAnyException();
    }

    @Test
    void send_should_set_expected_from_address() throws Exception {
        emailSender.send(TEST_DATA);

        var captor = ArgumentCaptor.forClass(InternetAddress.class);
        verify(mimeMessage).setFrom(captor.capture());
        assertThat(captor.getValue()).isEqualTo(EMAIL_PROPERTIES.from().asInternetAddress());
    }

    @Test
    void send_should_set_expected_to_address() throws Exception {
        emailSender.send(TEST_DATA);

        var captor = ArgumentCaptor.forClass(InternetAddress.class);
        verify(mimeMessage).setRecipient(eq(MimeMessage.RecipientType.TO), captor.capture());
        assertThat(captor.getValue()).isSameAs(TEST_DATA.to());
    }

    @Test
    void send_should_set_expected_reply_to_address() throws Exception {
        emailSender.send(TEST_DATA);

        var captor = ArgumentCaptor.forClass(Object.class);
        verify(mimeMessage).setReplyTo((Address[]) captor.capture());
        var addresses = (Address[]) captor.getValue();
        assertThat(addresses).containsExactly(EMAIL_PROPERTIES.replyTo().asInternetAddress());
    }

    @Test
    void send_should_set_expected_subject() throws Exception {
        emailSender.send(TEST_DATA);

        var captor = ArgumentCaptor.forClass(String.class);
        verify(mimeMessage).setSubject(captor.capture(), eq(StandardCharsets.UTF_8.name()));
        assertThat(captor.getValue()).isSameAs(TEST_DATA.subject());
    }

    @Test
    void send_should_add_expected_body_part() throws Exception {
        emailSender.send(TEST_DATA);

        var captor = ArgumentCaptor.forClass(Multipart.class);
        verify(mimeMessage).setContent(captor.capture());
        // this is the `rootPart` part of our email
        var rootPart = captor.getValue();
        // `rootPart` should have a contentType starting by `multipart/mixed;` to be valid
        assertThat(rootPart.getContentType()).startsWith("multipart/mixed;");
        // our `rootPart` should have a contentType containing `boundary="..."` to be valid
        assertThat(rootPart.getContentType()).containsPattern("boundary=\".*\"");
        // our `rootPart` should contain one part, our `bodyPart` that should be an instance of
        // MimeBodyPart
        assertThat(rootPart.getCount()).isEqualTo(1);
        assertThat(rootPart.getBodyPart(0)).isInstanceOf(MimeBodyPart.class);
        var bodyPart = (MimeBodyPart) rootPart.getBodyPart(0);
        // our `bodyPart` should have a MimeMultiPart as content, our `contentPart`
        assertThat(bodyPart.getContent()).isNotNull().isInstanceOf(MimeMultipart.class);
        var contentPart = (MimeMultipart) bodyPart.getContent();
        // our `contentPart` should have a contentType starting by `multipart/related;` to be valid
        assertThat(contentPart.getContentType()).startsWith("multipart/related;");
        // our `contentPart` should have a contentType containing `boundary="..."` to be valid
        assertThat(contentPart.getContentType()).containsPattern("boundary=\".*\"");
        // our `contentPart` should contain one part, our `contentWrapperPart` that should be an
        // instance of MimeBodyPart
        // ... I know, email structures are cumbersome...
        assertThat(contentPart.getCount()).isEqualTo(1);
        assertThat(contentPart.getBodyPart(0)).isInstanceOf(MimeBodyPart.class);
        var contentWrapperPart = (MimeBodyPart) contentPart.getBodyPart(0);
        // our `contentWrapperPart` should have a MimeMultiPart as content, our
        // `alternativeContentPart`
        assertThat(contentWrapperPart.getContent()).isNotNull().isInstanceOf(MimeMultipart.class);
        var alternativeContentPart = (MimeMultipart) contentWrapperPart.getContent();
        // our `alternativeContentPart` should have a contentType starting by
        // `multipart/alternative;` to be valid
        assertThat(alternativeContentPart.getContentType()).startsWith("multipart/alternative;");
        // our `alternativeContentPart` should have a contentType containing `boundary="..."` to be
        // valid
        assertThat(rootPart.getContentType()).containsPattern("boundary=\".*\"");
        // our `alternativeContentPart` should contain two parts
        assertThat(alternativeContentPart.getCount()).isEqualTo(2);
        // the first part of our `alternativeContentPart` should be our `textPlainPart`
        var textPlainPart = alternativeContentPart.getBodyPart(0);
        // our `textPlainPart` should have a dataHandler with contentType `text/plain;
        // charset=UTF-8`
        assertThat(textPlainPart.getDataHandler().getContentType()).isEqualTo("text/plain; charset=UTF-8");
        // our `textPlainPart` should contain our text plain content
        assertThat(textPlainPart.getContent()).isEqualTo(TEST_DATA.plainText());
        // the second part of our `alternativeContentPart` should be our `htmlPart`
        var htmlPart = alternativeContentPart.getBodyPart(1);
        // our `htmlPart` should have a dataHandler with contentType `text/html;charset=UTF-8` as
        // contentType
        assertThat(htmlPart.getDataHandler().getContentType()).isEqualTo("text/html;charset=UTF-8");
        // our `htmlPart` should contain our html content
        assertThat(htmlPart.getContent()).isEqualTo(TEST_DATA.html());
    }

    @Test
    void send_should_add_expected_body_parts_with_attachment() throws MessagingException, IOException {
        emailSender.send(testDataWithAttachment());

        var captor = ArgumentCaptor.forClass(Multipart.class);
        verify(mimeMessage).setContent(captor.capture());

        var rootPart = captor.getValue();
        // `rootPart` should have a contentType starting by `multipart/mixed;` to be valid
        assertThat(rootPart.getContentType()).startsWith("multipart/mixed;");
        // our `rootPart` should have a contentType containing `boundary="..."` to be valid
        assertThat(rootPart.getContentType()).containsPattern("boundary=\".*\"");
        // our `rootPart` should contain two parts, our `bodyPart` and our `attachment` that should both be an instance of
        // MimeBodyPart, the second one being our `attachmentPart`
        assertThat(rootPart.getCount()).isEqualTo(2);
        assertThat(rootPart.getBodyPart(0)).isInstanceOf(MimeBodyPart.class);
        assertThat(rootPart.getBodyPart(1)).isInstanceOf(MimeBodyPart.class);
        var attachmentPart = (MimeBodyPart) rootPart.getBodyPart(1);
        assertThat(attachmentPart.getDisposition()).isEqualTo(Part.ATTACHMENT);
        // our `attachmentPart` should have the content of our qrCode
        var attachmentContent = attachmentPart.getContent();
        assertThat(attachmentContent).isInstanceOf(ByteArrayInputStream.class);
        var attachmentBytes = ((ByteArrayInputStream) attachmentContent).readAllBytes();
        var expectedBytes = EmailTestHelper.loadResourceBinaryContent("email/qr_code.jpg");
        assertThat(attachmentBytes).isEqualTo(expectedBytes);
    }

    private static EmailToSend testData() {
        try {
            var from = new InternetAddress("from@test.org");
            var subject = "subject";
            var plainText = "plain_text";
            var htmlText = "html_text";
            return new EmailToSend(from, subject, plainText, htmlText, Optional.empty());
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static EmailToSend testDataWithAttachment() {
        var withoutAttachment = testData();
        var attachment = EmailTestHelper.loadResourceBinaryContent("email/qr_code.jpg");
        return new EmailToSend(
            withoutAttachment.to(),
            withoutAttachment.subject(),
            withoutAttachment.plainText(),
            withoutAttachment.html(),
            Optional.of(attachment)
        );
    }
}
