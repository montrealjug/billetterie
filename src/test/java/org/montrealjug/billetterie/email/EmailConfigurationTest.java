package org.montrealjug.billetterie.email;

import gg.jte.TemplateEngine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.montrealjug.billetterie.email.EmailConfiguration.EmailProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class EmailConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withUserConfiguration(EmailConfiguration.class)
            .withBean(TemplateEngine.class, () -> mock(TemplateEngine.class))
            .withBean(ResourceBundleMessageSource.class, () -> mock(ResourceBundleMessageSource.class));

    @Test
    void emailConfiguration_should_provide_NO_OP_EmailSender_if_no_JavaMailSender_bean_is_available() {
        contextRunner
                .withPropertyValues(VALID_PROPERTIES)
                .run(context -> assertThat(context)
                        .hasSingleBean(EmailService.class)
                        .hasSingleBean(EmailSender.class)
                        .doesNotHaveBean(JavaMailSender.class)
                        .getBean(EmailSender.class).isSameAs(EmailSender.NO_OP)
                );
    }

    @ParameterizedTest
    @ValueSource(strings = {"SMTP"})
    @EmptySource
    void emailConfiguration_should_provide_a_SmtpEmailSender_if_mode_is_SMTP_or_not_defined(String mode) {
        contextRunner
                .withPropertyValues(VALID_PROPERTIES)
                .withPropertyValues("app.mail.mode=" + mode)
                .withBean(JavaMailSender.class, () -> mock(JavaMailSender.class))
                .run(context -> assertThat(context)
                        .hasSingleBean(EmailService.class)
                        .hasSingleBean(EmailSender.class)
                        .getBean(EmailSender.class).isInstanceOf(SmtpEmailSender.class)
                );
    }

    @Test
    void emailConfiguration_should_provide_NO_OP_EmailSender_if_mode_is_NO_OP() {
        contextRunner
                .withPropertyValues(VALID_PROPERTIES)
                .withPropertyValues("app.mail.mode=no-op")
                .withBean(JavaMailSender.class, () -> mock(JavaMailSender.class))
                .run(context -> assertThat(context)
                        .hasSingleBean(EmailService.class)
                        .hasSingleBean(EmailSender.class)
                        .getBean(EmailSender.class).isSameAs(EmailSender.NO_OP)
                );
    }

    @Test
    void emailConfiguration_should_validate_EmailProperties() {
        var missingProperties = new String[] {
                VALID_PROPERTIES[0],
                VALID_PROPERTIES[2],
                VALID_PROPERTIES[3]
        };
        contextRunner.withPropertyValues(missingProperties)
                .run(context -> assertThat(context).hasFailed());

        var invalidProperties = new String[] {
                "this_is_not_a_valid_email",
                VALID_PROPERTIES[1],
                VALID_PROPERTIES[2],
                VALID_PROPERTIES[3]
        };
        contextRunner.withPropertyValues(invalidProperties)
                .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void valid_EmailProperties_should_have_valid_asInternetAddress() {
        contextRunner
                .withPropertyValues(VALID_PROPERTIES)
                .run(context -> {
                    var emailProperties = context.getBean(EmailProperties.class);
                    var from = emailProperties.from();
                    assertThat(from.asInternetAddress())
                            .satisfies(ia ->  assertThat(ia.getAddress()).isEqualTo(from.address()))
                            .satisfies(ia ->  assertThat(ia.getPersonal()).isEqualTo(from.name()));
                    var replyTo = emailProperties.replyTo();
                    assertThat(replyTo.asInternetAddress())
                            .satisfies(ia ->  assertThat(ia.getAddress()).isEqualTo(replyTo.address()))
                            .satisfies(ia ->  assertThat(ia.getPersonal()).isEqualTo(replyTo.name()));
                });
    }

    private static final String[] VALID_PROPERTIES = {
            "app.mail.from.address=from@test.org",
            "app.mail.from.name=From Test",
            "app.mail.reply-to.address=reply-to@test.org",
            "app.mail.reply-to.name=Reply To Test",
    };
}
