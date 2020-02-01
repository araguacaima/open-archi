package com.araguacaima.open_archi.web.controller.email;

import com.araguacaima.open_archi.web.controller.model.Email;

import java.util.Collection;

public interface MailSender {

    String sendMessage(String recipientEmail,
                       String cc, String bcc, String from,
                       String subject,
                       Collection<Object> messages);

    String sendMessage(Email email);
}
