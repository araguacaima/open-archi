package com.araguacaima.open_archi.persistence.commons.exceptions;

public class EntityInsertionError extends EntityError {
    public EntityInsertionError(String message) {
        super(message);
    }

    public EntityInsertionError(String message, Throwable cause) {
        super(message, cause);
    }
}
