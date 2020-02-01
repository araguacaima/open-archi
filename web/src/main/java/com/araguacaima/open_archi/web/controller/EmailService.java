package com.araguacaima.open_archi.web.controller;

import com.araguacaima.open_archi.persistence.commons.Config;
import com.araguacaima.open_archi.web.controller.email.MailSender;
import com.araguacaima.open_archi.web.controller.email.MailSenderFactory;
import com.araguacaima.open_archi.web.controller.email.MailType;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.araguacaima.braas.core.Commons.DEFAULT_ENCODING;


public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    /**
     * Sends an email from a fired rule
     */
    public static void send(final String message) {
        send(message, null);
    }

    /**
     * Sends an email from a fired rule
     */
    public static void send(final String message, final Map<String, Object> parameters) {
        String message1 = message;
        try {
            message1 = URLDecoder.decode(message, DEFAULT_ENCODING);
        } catch (UnsupportedEncodingException ignored) {
        }
        try {
            MailSender mailSender = MailSenderFactory.getInstance().getMailSender(MailType.HTML);
            Collection<Config> configs = OrpheusDbJPAEntityManagerUtils.executeQuery(Config.class, Config.FIND_ALL);
            String to = IterableUtils.find(configs, (config -> "mail.server.username".equals(config.getKey()))).getValue();
            String from = IterableUtils.find(configs, (config -> "mail.server.username".equals(config.getKey()))).getValue();
            String subject = IterableUtils.find(configs, (config -> "subject".equals(config.getKey()))).getValue();
            String cc = null;
            String bcc = null;
            if (parameters != null) {
                to = StringUtils.defaultIfBlank((String) parameters.get("to"), to);
                from = StringUtils.defaultIfBlank((String) parameters.get("from"), from);
                subject = StringUtils.defaultIfBlank((String) parameters.get("subject"), subject);
                cc = StringUtils.defaultIfBlank((String) parameters.get("from"), from);
                bcc = StringUtils.defaultIfBlank((String) parameters.get("bcc"), null);
            }
            mailSender.sendMessage(to, cc, bcc, from, subject, Collections.singletonList(message1));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(Collection<Object> messages, final Map<String, Object> parameters) {
        try {
            MailSender mailSender = MailSenderFactory.getInstance().getMailSender(MailType.HTML);
            Collection<Config> configs = OrpheusDbJPAEntityManagerUtils.executeQuery(Config.class, Config.FIND_ALL);
            String to = IterableUtils.find(configs, (config -> "mail.server.username".equals(config.getKey()))).getValue();
            String from = IterableUtils.find(configs, (config -> "mail.server.username".equals(config.getKey()))).getValue();
            Config subject_ = IterableUtils.find(configs, (config -> "subject".equals(config.getKey())));
            String subject = subject_ == null ? null : subject_.getValue();
            String cc = null;
            String bcc = null;
            if (parameters != null) {
                to = StringUtils.defaultIfBlank((String) parameters.get("to"), to);
                from = StringUtils.defaultIfBlank((String) parameters.get("from"), from);
                subject = StringUtils.defaultIfBlank((String) parameters.get("subject"), subject);
                cc = StringUtils.defaultIfBlank((String) parameters.get("from"), from);
                bcc = StringUtils.defaultIfBlank((String) parameters.get("bcc"), null);
            }
            mailSender.sendMessage(to, cc, bcc, from, subject, messages);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void send(String[] recipientEmail,
                            String[] cc,
                            String[] bcc,
                            String from,
                            String subject,
                            Collection<Object> messages) {

        Map<String, Object> params = new HashMap<>();
        params.put("to", StringUtils.join(recipientEmail, ","));
        params.put("from", from);
        params.put("subject", subject);
        send(messages, params);
    }
}
