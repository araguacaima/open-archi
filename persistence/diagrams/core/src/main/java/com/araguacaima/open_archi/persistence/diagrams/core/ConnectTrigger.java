package com.araguacaima.open_archi.persistence.diagrams.core;


import com.araguacaima.open_archi.persistence.diagrams.meta.Storable;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Comparator;
import java.util.UUID;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "ConnectTrigger", schema = "Diagrams")
@DynamicUpdate
public class ConnectTrigger implements Storable {
    public static final Comparator<ConnectTrigger> connectTriggerComparator = Comparator.comparing(ConnectTrigger::getId,
            Comparator.nullsFirst(String::compareTo));
    @Id
    private String id;

    @Column
    private String triggerById;

    @Column
    private String triggerByName;

    @Column
    private String triggerByExpression;

    public ConnectTrigger() {
        this.id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTriggerById() {
        return triggerById;
    }

    public void setTriggerById(String triggerById) {
        this.triggerById = triggerById;
    }

    public String getTriggerByName() {
        return triggerByName;
    }

    public void setTriggerByName(String triggerByName) {
        this.triggerByName = triggerByName;
    }

    public String getTriggerByExpression() {
        return triggerByExpression;
    }

    public void setTriggerByExpression(String triggerByExpression) {
        this.triggerByExpression = triggerByExpression;
    }

    public void override(ConnectTrigger source) {
        this.triggerById = source.getTriggerById();
        this.triggerByName = source.getTriggerByName();
        this.triggerByExpression = source.getTriggerByExpression();
    }
}
