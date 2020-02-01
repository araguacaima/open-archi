package com.araguacaima.open_archi.persistence.commons.exceptions;

public class DBError extends Error {

    public DBError(String message) {
        super(message);
    }

    public DBError(String message, Throwable cause) {
        super(message, cause);
    }
}
