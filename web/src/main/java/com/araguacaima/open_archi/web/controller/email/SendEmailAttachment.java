package com.araguacaima.open_archi.web.controller.email;

import com.sun.mail.smtp.SMTPTransport;
import org.apache.commons.lang.StringUtils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Properties;
import java.util.Set;

import static com.araguacaima.open_archi.web.common.Commons.jsonUtils;
import static com.araguacaima.open_archi.web.common.Commons.reflectionUtils;

public class SendEmailAttachment extends SendEmail {
    private static final String OCTET_STREAM_MIME = "application/octet-stream";
    private final String host;
    private final String username;
    private final String password;

    SendEmailAttachment(Properties properties) {
        this.host = properties.getProperty("mail.server.host");
        this.username = properties.getProperty("mail.server.username");
        this.password = properties.getProperty("mail.server.password");
    }

    @Override
    public String sendMessage(String recipientsList,
                              String cc, String bcc, String from, String subject,
                              Collection<Object> messages) {
        String response = null;
        Properties prop = System.getProperties();
        prop.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);

        try {
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientsList, false));
            msg.setSubject(subject);

            Set<String> messagesString = new LinkedHashSet<>();
            Set<InputStream> messagesStream = new LinkedHashSet<>();
            messages.forEach(message -> {
                if (String.class.isAssignableFrom(message.getClass())) {
                    messagesString.add(message.toString());
                } else if (InputStream.class.isAssignableFrom(message.getClass())) {
                    messagesStream.add((InputStream) message);
                } else if (reflectionUtils.getFullyQualifiedJavaTypeOrNull(message.getClass()) != null) {
                    try {
                        messagesString.add(jsonUtils.toJSON(message));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    messagesString.add(String.valueOf(message));
                }
            });


            MimeBodyPart p1 = new MimeBodyPart();
            p1.setText(StringUtils.join(messagesString, "\n"));
            Multipart mp = new MimeMultipart();
            mp.addBodyPart(p1);

            for (InputStream stream : messagesStream) {
                MimeBodyPart p_ = new MimeBodyPart();
                StreamDataSource fds = new StreamDataSource(stream);
                p_.setDataHandler(new DataHandler(fds));
                p_.setFileName(fds.getName());
                mp.addBodyPart(p_);
            }

            msg.setContent(mp);
            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            t.connect(host, username, password);
            t.sendMessage(msg, msg.getAllRecipients());
            response = t.getLastServerResponse();
            System.out.println("Response: " + response);
            t.close();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
        return response;
    }

    static class StreamDataSource implements DataSource {

        private InputStream inputStream;

        public StreamDataSource(InputStream htmlString) {
            inputStream = htmlString;
        }

        @Override
        public InputStream getInputStream() throws IOException {
            if (inputStream == null) throw new IOException("html message is null!");
            return inputStream;
        }

        @Override
        public OutputStream getOutputStream() throws IOException {
            throw new IOException("This DataHandler cannot write HTML");
        }

        @Override
        public String getContentType() {
            return OCTET_STREAM_MIME;
        }

        @Override
        public String getName() {
            return "StreamDataSource";
        }
    }
}