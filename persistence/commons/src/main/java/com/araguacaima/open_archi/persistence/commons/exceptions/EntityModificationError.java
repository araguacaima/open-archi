package com.araguacaima.open_archi.persistence.commons.exceptions;

public class EntityModificationError extends EntityError {
    public EntityModificationError(String message) {
        super(message);
    }

    public EntityModificationError(String message, Throwable cause) {
        super(message, cause);
    }
}
