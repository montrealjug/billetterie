package org.montrealjug.billetterie.exception;

public class EntityNotFoundException extends RuntimeException{
    private String message;

    public EntityNotFoundException(String message) {
        this.message = message;
    }

    public EntityNotFoundException() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
