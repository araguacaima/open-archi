package com.araguacaima.open_archi.persistence.commons;

public enum OperationType {

    CREATION("CreationError"),
    MODIFICATION("ModificationError"),
    REPLACEMENT("ReplacementError");

    private final String value;

    OperationType(String value) {

        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
