package com.araguacaima.open_archi.web.controller.email;

import com.sun.mail.smtp.SMTPTransport;
import org.apache.commons.lang.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

public class SendEmailSMTP extends SendEmail {

    private final String host;
    private final int port;
    private final String username;
    private final String password;

    SendEmailSMTP(Properties properties) {
        this.host = properties.getProperty("mail.server.host");
        this.port = Integer.parseInt(properties.getProperty("mail.server.port"));
        this.username = properties.getProperty("mail.server.username");
        this.password = properties.getProperty("mail.server.password");
    }

    @Override
    public String sendMessage(String recipientsList,
                              String cc, String bcc, String from, String subject,
                              Collection<Object> messages) {

        Properties prop = System.getProperties();
        prop.put("mail.smtp.host", this.host); //optional, defined in SMTPTransport
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.port", this.port); // default port 25
        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);
        String response = null;
        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientsList, false));
            msg.setSubject(subject);
            msg.setText(StringUtils.join(messages, "\n"));
            msg.setSentDate(new Date());
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            t.connect(this.host, this.username, this.password);
            t.sendMessage(msg, msg.getAllRecipients());
            response = t.getLastServerResponse();
            System.out.println("Response: " + response);
            t.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return response;
    }
}