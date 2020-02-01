package com.araguacaima.open_archi.persistence.diagrams.classes;

public enum Visibility {
    PUBLIC("public"),
    PRIVATE("private"),
    PROTECTED("protected"),
    PACKAGE("package");

    private String value;

    Visibility(String value) {
        this.value = value;
    }
}
