// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import gg.jte.TemplateEngine;
import gg.jte.output.StringOutput;
import java.util.Locale;
import org.montrealjug.billetterie.email.EmailModel.Email;
import org.montrealjug.billetterie.email.EmailModel.EmailToSend;
import org.springframework.context.support.ResourceBundleMessageSource;

class EmailWriter {

    private final TemplateEngine templateEngine;
    private final ResourceBundleMessageSource messageSource;

    EmailWriter(TemplateEngine templateEngine, ResourceBundleMessageSource messageSource) {
        this.templateEngine = templateEngine;
        this.messageSource = messageSource;
    }

    EmailToSend write(Email email) {
        var emailType = email.type();
        var subject = this.messageSource.getMessage(emailType.subjectKey(), null, Locale.CANADA_FRENCH);
        var plainText = this.writeTemplate(emailType.plainTextTemplate(), email);
        var htmlText = this.writeTemplate(emailType.htmlTextTemplate(), email);
        return new EmailToSend(email.to(), subject, plainText, htmlText);
    }

    private String writeTemplate(String templateName, Email email) {
        var templateOutput = new StringOutput();
        templateEngine.render(templateName, email, templateOutput);
        return templateOutput.toString();
    }
}
