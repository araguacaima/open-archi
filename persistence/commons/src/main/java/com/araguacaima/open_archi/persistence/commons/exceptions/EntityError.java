package com.araguacaima.open_archi.persistence.commons.exceptions;

public class EntityError extends Error {

    public EntityError(String message) {
        super(message);
    }

    public EntityError(String message, Throwable cause) {
        super(message, cause);
    }
}
