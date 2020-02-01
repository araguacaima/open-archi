package com.araguacaima.open_archi.web.common;

import org.hibernate.ogm.cfg.Configurable;
import org.hibernate.ogm.cfg.OptionConfigurator;
import org.hibernate.ogm.datastore.mongodb.MongoDB;
import org.hibernate.ogm.datastore.mongodb.options.ReadPreferenceType;
import org.hibernate.ogm.datastore.mongodb.options.WriteConcernType;

public class OpenArchiOptionConfigurator extends OptionConfigurator {

    @Override
    public void configure(Configurable configurable) {
        configurable.configureOptionsFor( MongoDB.class )
                .writeConcern( WriteConcernType.REPLICA_ACKNOWLEDGED )
                .readPreference( ReadPreferenceType.NEAREST );
    }
}