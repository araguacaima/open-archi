package com.araguacaima.open_archi.persistence.diagrams.core;

import com.araguacaima.open_archi.persistence.diagrams.meta.MetaInfo;
import com.araguacaima.open_archi.persistence.diagrams.meta.Storable;
import com.araguacaima.open_archi.persistence.diagrams.meta.Version;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "CompositeElement", schema = "Diagrams")
@DynamicUpdate
public class CompositeElement implements Comparable<CompositeElement>, Storable {

    @Id
    @Type(type = "objectid")
    @NotNull
    @Column(name = "Id")
    private String id;

    @Column
    @Enumerated
    private ElementKind type;

    @Column
    private String link;

    //@OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Transient
    @JsonIgnore
    private Version version;

    public static CompositeElement fromItem(Item item) {
        CompositeElement compositeElement = new CompositeElement();
        MetaInfo meta = item.getMeta();
        if (meta != null) {
            compositeElement.setVersion(meta.getActiveVersion());
        }
        compositeElement.setType(item.getKind());
        compositeElement.setId(item.getId());
        return compositeElement;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        if (StringUtils.isNotBlank(this.id)) {
            setLink("/models/" + this.id);
        } else {
            setLink(null);
        }
    }

    public ElementKind getType() {
        return type;
    }

    public void setType(ElementKind type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public void override(CompositeElement compositeElement) {
        this.id = compositeElement.getId();
        this.type = compositeElement.getType();
        this.link = compositeElement.getLink();
        Version version = compositeElement.getVersion();
        if (version != null) {
            this.version = version;
        }
    }

    public void copyNonEmpty(CompositeElement compositeElement) {
        if (StringUtils.isNotBlank(compositeElement.getId())) {
            this.id = compositeElement.getId();
        }
        this.type = compositeElement.getType();
        if (StringUtils.isNotBlank(compositeElement.getLink())) {
            this.link = compositeElement.getLink();
        }
        Version version = compositeElement.getVersion();
        if (version != null) {
            this.version = version;
        }
    }

    @Override
    public int compareTo(CompositeElement o) {
        if (o == null) {
            return 0;
        } else {
            if (o.getType().equals(this.type)) {
                if (o.getId().equals(this.id)) {
                    return o.getVersion().compareTo(this.version);
                } else {
                    return -1;
                }
            } else {
                return -1;
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompositeElement that = (CompositeElement) o;

        if (getId() != null ? !getId().equals(that.getId()) : that.getId() != null) return false;
        return getType() != null ? getType().equals(that.getType()) : that.getType() == null;
    }

    @Override
    public int hashCode() {
        int result = getId() != null ? getId().hashCode() : 0;
        result = 31 * result + (getType() != null ? getType().hashCode() : 0);
        return result;
    }
}
