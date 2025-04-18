package org.montrealjug.billetterie.exception;

import org.springframework.http.HttpStatus;

public class RequestException  extends RuntimeException{
    private String message;
    private HttpStatus httpStatus;

    public RequestException(String message, HttpStatus httpStatus) {
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public RequestException() {
    }

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
}
