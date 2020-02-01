package com.araguacaima.open_archi.web.controller;

import java.util.ArrayList;
import java.util.Collection;

public class SendEmailSetup {

    private Collection<String> recipients = new ArrayList<>();
    private String from;
    private String sender;
    private Collection<String> cc = new ArrayList<>();
    private Collection<String> bcc = new ArrayList<>();

    public Collection<String> getRecipients() {
        return recipients;
    }

    public void setRecipients(Collection<String> recipients) {
        this.recipients = recipients;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public Collection<String> getCc() {
        return cc;
    }

    public void setCc(Collection<String> cc) {
        this.cc = cc;
    }

    public Collection<String> getBcc() {
        return bcc;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setBcc(Collection<String> bcc) {
        this.bcc = bcc;
    }

    public void addRecipient(String recipient) {
        this.recipients.add(recipient);
    }

    public void addCc(String cc) {
        this.cc.add(cc);
    }

    public void addCco(String cco) {
        this.bcc.add(cco);
    }
}
