package com.araguacaima.open_archi.persistence.diagrams.core;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.util.Comparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "Image", schema = "Diagrams")
@DynamicUpdate
public class Image extends BaseEntity {

    @Column
    @Convert(converter = MimeTypeConverter.class)
    @JsonDeserialize(using = MimeTypeDeserializer.class)
    private MimeType type;

    @Column
    @Lob
    private String raw;

    @Column
    private String url;

    public Image(MimeType type) {
        this.type = type;
    }

    public Image() {
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
    public void override(BaseEntity source) {
        override(source, false, null, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta) {
        override(source, keepMeta, null, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedBy, Comparator comparator) {
        Image source1 = (Image) source;
        this.setType(source1.getType());
        this.setUrl(source1.getUrl());
        this.setRaw(source1.getRaw());
    }

    @Override
    public void copyNonEmpty(BaseEntity source) {
        copyNonEmpty(source, false);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta) {
        copyNonEmpty(source, keepMeta, null);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        Image source1 = (Image) source;
        if (source1.getType() != null) {
            this.setType(source1.getType());
        }
        if (source1.getUrl() != null) {
            this.setUrl(source1.getUrl());
        }
        if (source1.getRaw() != null) {
            this.setRaw(source1.getRaw());
        }
    }


    @Override
    public int compareTo(Object o) {
        if (o == null || !Image.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
