package com.araguacaima.open_archi.web.controller.email;

import com.araguacaima.open_archi.persistence.commons.Config;
import com.araguacaima.open_archi.persistence.commons.ConfigWrapper;
import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;

import java.io.IOException;
import java.util.Collection;

public class MailSenderFactory {
    private static final MailSenderFactory INSTANCE = new MailSenderFactory();

    public static MailSenderFactory getInstance() {
        return INSTANCE;
    }

    public MailSender getMailSender(MailType type) throws IOException {
        Collection<Config> configs = OrpheusDbJPAEntityManagerUtils.executeQuery(Config.class, Config.FIND_ALL);
        switch (type) {
            case SMTP:
                return new SendEmailSMTP(ConfigWrapper.toProperties(configs));
            case ATTACHMENT:
                return new SendEmailAttachment(ConfigWrapper.toProperties(configs));
            default:
                return new SendEmailHTML(ConfigWrapper.toProperties(configs));
        }
    }
}
