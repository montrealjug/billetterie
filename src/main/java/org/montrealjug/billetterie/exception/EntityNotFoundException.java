// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.exception;

public class EntityNotFoundException extends RuntimeException {

    private String message;
    private String viewName;

    public EntityNotFoundException(String message, String viewName) {
        this.message = message;
        this.viewName = viewName;
    }

    public EntityNotFoundException(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
