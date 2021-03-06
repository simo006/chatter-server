package com.project.chatter.web.exception;


public class NotFoundError extends RuntimeException {

    public NotFoundError(String message) {
        super(message);
    }

    public NotFoundError() {
        super("Object not found");
    }
}
