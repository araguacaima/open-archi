package com.araguacaima.open_archi.web.controller.email;

import com.araguacaima.braas.core.MessageType;
import com.sun.mail.smtp.SMTPTransport;
import de.neuland.jade4j.Jade4J;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

public class SendEmailHTML extends SendEmail {
    private static final String TEXT_HTML = "text/html";
    private final String host;
    private final String username;
    private final String password;
    private final boolean starttls;
    private final String imageResourcePath = SendEmailHTML.class.getClassLoader().getResource("./web/public/images/open-archi-26.png").getFile();
    private final String emailJadeTemplate = "web/views/email-template.jade";
    private final Properties properties;
    private String templateFile;

    SendEmailHTML(Properties properties) {
        this.host = properties.getProperty("mail.server.host");
        this.username = properties.getProperty("mail.server.username");
        this.password = properties.getProperty("mail.server.password");
        String starttlsProperty = properties.getProperty("mail.smtp.starttls.enable");
        this.starttls = StringUtils.isBlank(starttlsProperty) || Boolean.parseBoolean(starttlsProperty);
        this.properties = System.getProperties();
        this.properties.putAll(properties);
        this.properties.put("mail.smtp.starttls.enable", this.starttls);
        URL resourceTemplate = SendEmailHTML.class.getClassLoader().getResource(emailJadeTemplate);
        try {
            templateFile = resourceTemplate.toURI().getPath();
            File file = new File(templateFile);
            if (!file.exists()) {
                if (templateFile.startsWith(String.valueOf(File.separatorChar))) {
                    templateFile = templateFile.substring(1);
                }
            }
        } catch (URISyntaxException | NullPointerException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String sendMessage(String recipientsList, String cc, String bcc, String from, String subject, Collection<Object> messages) {

        Map<String, Object> model = new HashMap<>();
        model.put("messages", messages);
        model.put("messageTypeSuccess", MessageType.SUCCESS);
        model.put("messageTypeDebug", MessageType.DEBUG);
        model.put("messageTypeWarning", MessageType.WARNING);
        model.put("messageTypeError", MessageType.ERROR);
        model.put("messageTypeInfo", MessageType.INFO);
        model.put("title", subject);
        String imageResourceName = UUID.randomUUID().toString();
        model.put("imageResourceName", imageResourceName);


        Properties prop = System.getProperties();
        prop.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(prop, null);
        Message msg = new MimeMessage(session);
        String response = null;
        try {
            final String htmlContent = Jade4J.render(templateFile, model);
            msg.setFrom(new InternetAddress(from));
            msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientsList, false));
            if (StringUtils.isNotBlank(cc)) {
                msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, false));
            }
            if (StringUtils.isNotBlank(bcc)) {
                msg.setRecipients(Message.RecipientType.BCC, InternetAddress.parse(bcc, false));
            }
            msg.setSubject(subject);
            msg.setSentDate(new Date());

            // creates message part
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(htmlContent, TEXT_HTML);

            // creates multi-part
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(messageBodyPart);

            // adds inline image attachments
            MimeBodyPart imagePart = new MimeBodyPart();
            imagePart.setHeader("Content-ID", "<" + imageResourceName + ">");
            imagePart.setDisposition(MimeBodyPart.INLINE);
            imagePart.attachFile(imageResourcePath);
            multipart.addBodyPart(imagePart);

            msg.setContent(multipart);

            SMTPTransport t = (SMTPTransport) session.getTransport("smtp");
            t.connect(host, username, password);
            t.sendMessage(msg, msg.getAllRecipients());
            response = t.getLastServerResponse();
            System.out.println("Response: " + response);
            t.close();
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
        return response;
    }
}