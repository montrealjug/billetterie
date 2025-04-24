// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.exception;

import org.springframework.http.HttpStatus;

public class RequestException extends RuntimeException {
    private String message;
    private HttpStatus httpStatus;
    private String viewName;

    public RequestException(String message, HttpStatus httpStatus, String viewName) {
        this.message = message;
        this.httpStatus = httpStatus;
        this.viewName = viewName;
    }

    public RequestException() {}

    public String getMessage() {
        return message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setHttpStatus(HttpStatus httpStatus) {
        this.httpStatus = httpStatus;
    }

    public String getViewName() {
        return viewName;
    }

    public void setViewName(String viewName) {
        this.viewName = viewName;
    }
}
