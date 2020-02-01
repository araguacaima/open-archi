package com.araguacaima.open_archi.persistence.commons.exceptions;

public class EntityDeletionError extends EntityError {
    public EntityDeletionError(String message) {
        super(message);
    }

    public EntityDeletionError(String message, Throwable cause) {
        super(message, cause);
    }
}
