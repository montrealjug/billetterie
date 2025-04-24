package org.montrealjug.billetterie.email;

import gg.jte.TemplateEngine;
import jakarta.mail.internet.InternetAddress;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.validation.annotation.Validated;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(EmailConfiguration.EmailProperties.class)
@EnableAsync
class EmailConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailConfiguration.class);

    @Bean
    EmailSender emailSender(
            @Autowired(required = false) JavaMailSender javaMailSender, // useful for tests
            EmailProperties emailProperties
    ) {
        final EmailSender emailSender;
        if (javaMailSender != null && emailProperties.mode != EmailMode.NO_OP) {
            emailSender = new SmtpEmailSender(javaMailSender, emailProperties);
        } else {
            LOGGER.warn("no-op EmailSender configured, emails will be sent to the console as logs");
            emailSender = EmailSender.NO_OP;
        }
        return emailSender;
    }

    @Bean
    EmailService emailService(
            EmailSender emailSender,
            TemplateEngine templateEngine,
            ResourceBundleMessageSource messageSource
    ) {
        var emailWriter = new EmailWriter(templateEngine, messageSource);
        return new EmailService(emailSender, emailWriter);
    }

    @Validated
    @ConfigurationProperties(prefix = "app.mail")
    record EmailProperties(
            @DefaultValue("SMTP")
            EmailMode mode,
            @Valid
            @NotNull
            @NestedConfigurationProperty
            EmailAddress from,
            @Valid
            @NotNull
            @NestedConfigurationProperty
            EmailAddress replyTo
    ) {}

    enum EmailMode {
        NO_OP, SMTP
    }

    record EmailAddress(
            @Email
            @NotBlank
            String address,
            @NotBlank
            String name
    ) {
        InternetAddress asInternetAddress() {
            try {
                return new InternetAddress(address, name, StandardCharsets.UTF_8.name());
            } catch (UnsupportedEncodingException e) {
                // should never happen...
                throw new IllegalArgumentException(e);
            }
        }
    }
}
