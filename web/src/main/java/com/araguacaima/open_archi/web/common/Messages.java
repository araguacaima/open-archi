package com.araguacaima.open_archi.web.common;

import java.util.ArrayList;
import java.util.Collection;

public class Messages {

    private MessageSummary summary;
    private Collection<Message> messages = new ArrayList<>();

    public MessageSummary getSummary() {
        return summary;
    }

    public void setSummary(MessageSummary summary) {
        this.summary = summary;
    }

    public Collection<Message> getMessages() {
        return messages;
    }

    public void setMessages(Collection<Message> messages) {
        this.messages = messages;
    }

    public void addMessage(Message message) {
        this.messages.add(message);
    }
}
