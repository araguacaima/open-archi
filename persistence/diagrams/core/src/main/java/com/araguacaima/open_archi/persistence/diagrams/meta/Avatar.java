package com.araguacaima.open_archi.persistence.diagrams.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.UUID;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "Avatar", schema = "Meta")
@DynamicUpdate
public class Avatar implements Storable {

    @Id
    private String id;

    @Column
    @Convert(converter = MimeTypeConverter.class)
    @JsonDeserialize(using = MimeTypeDeserializer.class)
    private MimeType type;

    @Column
    @Lob
    private String raw;

    @Column
    private String url;

    public Avatar(MimeType type) {
        this();
        this.type = type;
    }

    public Avatar() {
        this.id = generateId();
    }

    @JsonIgnore
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    public MimeType getType() {
        return type;
    }

    public void setType(MimeType type) {
        this.type = type;
    }

    public String getRaw() {
        return raw;
    }

    public void setRaw(String raw) {
        this.raw = raw;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

}
