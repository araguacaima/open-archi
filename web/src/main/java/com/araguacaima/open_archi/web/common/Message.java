package com.araguacaima.open_archi.web.common;

public class Message {

    private String code;
    private String message;
    private SeverityMessage severity;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SeverityMessage getSeverity() {
        return severity;
    }

    public void setSeverity(SeverityMessage severity) {
        this.severity = severity;
    }
}
