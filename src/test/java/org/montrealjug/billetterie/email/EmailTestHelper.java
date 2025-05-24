// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.email;

import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.montrealjug.billetterie.email.EmailConfiguration.EmailAddress;
import org.montrealjug.billetterie.email.EmailConfiguration.EmailMode;
import org.montrealjug.billetterie.email.EmailConfiguration.EmailProperties;

class EmailTestHelper {

    static EmailProperties emailProperties() {
        var from = new EmailAddress("from@test.org", "From Test");
        var replyTo = new EmailAddress("reply-to@test.org", "Reply To Test");
        return new EmailProperties(EmailMode.NO_OP, from, replyTo);
    }

    static String loadResourceContent(String resourceName) {
        try {
            return Files.readString(loadResourcePath(resourceName));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    static byte[] loadResourceBinaryContent(String resourceName) {
        try {
            return Files.readAllBytes(loadResourcePath(resourceName));
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    static Path loadResourcePath(String resourceName) throws URISyntaxException {
        var resource = EmailTestHelper.class.getClassLoader().getResource(resourceName);
        if (resource == null) {
            throw new IllegalArgumentException(resourceName + " not found");
        }
        return Path.of(resource.toURI());
    }
}
