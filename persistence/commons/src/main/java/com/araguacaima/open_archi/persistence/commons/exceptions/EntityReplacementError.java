package com.araguacaima.open_archi.persistence.commons.exceptions;

public class EntityReplacementError extends EntityError {
    public EntityReplacementError(String message) {
        super(message);
    }

    public EntityReplacementError(String message, Throwable cause) {
        super(message, cause);
    }
}
