package org.montrealjug.billetterie.email;

import org.montrealjug.billetterie.email.EmailConfiguration.EmailAddress;
import org.montrealjug.billetterie.email.EmailConfiguration.EmailMode;
import org.montrealjug.billetterie.email.EmailConfiguration.EmailProperties;

import java.nio.file.Files;
import java.nio.file.Path;

class EmailTestHelper {

    static EmailProperties emailProperties() {
        var from = new EmailAddress("from@test.org", "From Test");
        var replyTo = new EmailAddress("reply-to@test.org", "Reply To Test");
        return new EmailProperties(
                EmailMode.NO_OP,
                from,
                replyTo
        );
    }

    static String loadResourceContent(String resourceName) {
        var resource = EmailTestHelper.class.getClassLoader().getResource(resourceName);
        if (resource == null) {
            throw new IllegalArgumentException(resourceName + " not found");
        }
        try {
            return Files.readString(Path.of(resource.toURI()));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }
}
