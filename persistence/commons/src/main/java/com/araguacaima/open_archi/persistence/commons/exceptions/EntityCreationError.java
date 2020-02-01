package com.araguacaima.open_archi.persistence.commons.exceptions;

public class EntityCreationError extends EntityError {
    public EntityCreationError(String message) {
        super(message);
    }

    public EntityCreationError(String message, Throwable cause) {
        super(message, cause);
    }
}
