// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.exception;

public class RedirectableNotFoundException extends RuntimeException {
    private final String redirectUrl;
    private final String flashMessage;

    public RedirectableNotFoundException(String message, String redirectUrl) {
        super(message);
        this.redirectUrl = redirectUrl;
        this.flashMessage = message;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }

    public String getFlashMessage() {
        return flashMessage;
    }
}
