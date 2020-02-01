package com.araguacaima.open_archi.persistence.diagrams.core;


import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.araguacaima.open_archi.persistence.diagrams.core.ConnectTrigger.connectTriggerComparator;
import static com.araguacaima.open_archi.persistence.diagrams.core.Relationship.relationshipComparator;

/**
 * This is the superclass for all model elements.
 */

@Entity
@PersistenceUnit(unitName = "open-archi")
@NamedQueries({@NamedQuery(name = Item.GET_ITEM_ID_BY_NAME,
        query = "select a " +
                "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.name=:name and a.kind=:kind"),
        @NamedQuery(name = Item.GET_ALL_CHILDREN,
                query = "select a.children " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.id=:id"),
        @NamedQuery(name = Item.GET_META_DATA,
                query = "select a.metaData " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.id=:id"),
        @NamedQuery(name = Item.GET_ALL_PROTOTYPES,
                query = "select a " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.prototype=true order by a.kind, a.shape.type, a.name"),
        @NamedQuery(name = Item.GET_ALL_PROTOTYPES_OF_TYPES,
                query = "select a " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.prototype=true and a.kind in(:kindList) order by a.kind, a.shape.type, a.name"),
        @NamedQuery(name = Item.GET_ALL_PROTOTYPE_NAMES,
                query = "select new com.araguacaima.open_archi.persistence.commons.IdName(a.id, a.name, TYPE(a), a.kind) " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.prototype=true"),
        @NamedQuery(name = Item.GET_ALL_CONSUMER_NAMES,
                query = "select new com.araguacaima.open_archi.persistence.commons.IdName(a.id, a.name, TYPE(a), a.kind) " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.kind=Consumer"),
        @NamedQuery(name = Item.GET_ALL_DIAGRAM_NAMES,
                query = "select new com.araguacaima.open_archi.persistence.commons.IdName(a.id, a.name, TYPE(a), a.kind) " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.prototype=false"),
        @NamedQuery(name = Item.GET_ALL_MODEL_NAMES_BY_TYPE,
                query = "select new com.araguacaima.open_archi.persistence.commons.IdName(a.id, a.name, TYPE(a), a.kind) " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where TYPE(a)=:type"),
        @NamedQuery(name = Item.GET_ALL_PROTOTYPE_NAMES_BY_TYPE,
                query = "select new com.araguacaima.open_archi.persistence.commons.IdName(a.id, a.name, TYPE(a), a.kind) " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where TYPE(a)=:type " +
                        "and a.prototype=true"),
        @NamedQuery(name = Item.GET_ALL_NON_CLONED_PROTOTYPE_NAMES_BY_TYPE,
                query = "select new com.araguacaima.open_archi.persistence.commons.IdName(a.id, a.name, TYPE(a), a.kind) " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where TYPE(a)=:type " +
                        "and a.prototype=true and a.clonedFrom IS NULL"),
        @NamedQuery(name = Item.GET_MODEL_NAMES_BY_NAME_AND_TYPE,
                query = "select new com.araguacaima.open_archi.persistence.commons.IdName(a.id, a.name, TYPE(a), a.kind) " +
                        "from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.name like concat(:name,'%') and TYPE(a)=:type "),
        @NamedQuery(name = Item.GET_ALL_CONSUMERS,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.kind='CONSUMER'"),
        @NamedQuery(name = Item.GET_PROTOTYPES_BY_NAME_AND_KIND,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.kind=:kind and a.name=:name and a.prototype=true"),
        @NamedQuery(name = Item.GET_MODELS_BY_NAME_AND_KIND,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.kind=:kind and a.name=:name and a.prototype=false"),
        @NamedQuery(name = Item.GET_ITEMS_BY_ID_OR_NAME_AND_KIND,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.id=:id or (a.kind=:kind and a.name=:name)"),
        @NamedQuery(name = Item.GET_ITEMS_BY_NAME_AND_KIND,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.kind=:kind and a.name=:name"),
        @NamedQuery(name = Item.GET_ITEMS_BY_NAME,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.name=:name"),
        @NamedQuery(name = Item.GET_ITEM_BY_ID,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.id=:id"),
        @NamedQuery(name = Item.GET_ALL_MODELS,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a "),
        @NamedQuery(name = Item.GET_MODELS_BY_TYPE,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a where TYPE(a)=:modelType"),
        @NamedQuery(name = Item.GET_MODELS_BY_STATUS,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.core.Item a where a.status=:status")
})
public abstract class Item extends Taggable {

    public static final String GET_ALL_MODELS = "get.all.models";
    public static final String GET_MODELS_BY_TYPE = "get.models.by.type";
    public static final String GET_MODELS_BY_STATUS = "get.models.by.status";
    public static final String GET_ALL_CHILDREN = "get.all.children";
    public static final String GET_META_DATA = "get.meta.data";
    public static final String GET_ALL_PROTOTYPES = "get.all.prototypes";
    public static final String GET_ALL_PROTOTYPES_OF_TYPES = "get.all.prototypes.of.types";
    public static final String GET_ALL_PROTOTYPE_NAMES = "get.all.prototype.names";
    public static final String GET_ALL_DIAGRAM_NAMES = "get.all.diagram.names";
    public static final String GET_ITEM_ID_BY_NAME = "get.item.id.by.name";
    public static final String GET_ITEM_BY_ID = "get.item.by.id";
    public static final String GET_ALL_CONSUMER_NAMES = "get.all.consumer.names";
    public static final String GET_ALL_CONSUMERS = "get.all.consumers";
    public static final String GET_MODEL_NAMES_BY_NAME_AND_TYPE = "get.model.names.by.name.and.type";
    public static final String GET_ALL_MODEL_NAMES_BY_TYPE = "get.all.model.names.by.type";
    public static final String GET_ALL_PROTOTYPE_NAMES_BY_TYPE = "get.all.prototype.names.by.type";
    public static final String GET_ALL_NON_CLONED_PROTOTYPE_NAMES_BY_TYPE = "get.all.non.cloned.prototype.names.by.type";
    public static final String GET_ITEMS_BY_NAME_AND_KIND = "get.items.by.name.and.kind";
    public static final String GET_ITEMS_BY_ID_OR_NAME_AND_KIND = "get.items.by.id.or.name.and.kind";
    public static final String GET_PROTOTYPES_BY_NAME_AND_KIND = "get.prototypes.by.name.and.kind";
    public static final String GET_MODELS_BY_NAME_AND_KIND = "get.models.by.name.and.kind";
    public static final String GET_ITEMS_BY_NAME = "get.items.by.name";
    public static final String ELEMENTS_USAGE_PARAM = "elementIds";
    public static final Comparator<Item> itemComparator = Comparator.comparing(Item::getKind,
            Comparator.nullsFirst(ElementKind::compareTo)).thenComparing(Item::getName,
            Comparator.nullsFirst(String::compareTo));

    @Column
    protected String name;
    @Column
    @Enumerated(EnumType.STRING)
    protected ElementKind kind = ElementKind.ITEM;
    @Column
    protected String description;
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    protected CompositeElement parent;
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Item_Children_Ids",
            joinColumns = {@JoinColumn(name = "Item_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Child_Id",
                    referencedColumnName = "Id")})
    protected Set<CompositeElement> children = new LinkedHashSet<>();
    @OneToOne(cascade = {CascadeType.MERGE, CascadeType.PERSIST}, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.MERGE, org.hibernate.annotations.CascadeType.PERSIST})
    protected Shape shape;
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    protected Image image;
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Item_Can_Be_Connected_From_Ids",
            joinColumns = {@JoinColumn(name = "Item_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Can_Be_Connected_From_Id",
                    referencedColumnName = "Id")})
    protected Set<ConnectTrigger> canBeConnectedFrom = new LinkedHashSet<>();
    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Item_Can_Be_Connected_To_Ids",
            joinColumns = {@JoinColumn(name = "Item_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Can_Be_Connected_To_Id",
                    referencedColumnName = "Id")})
    protected Set<ConnectTrigger> canBeConnectedTo = new LinkedHashSet<>();
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    protected MetaData metaData;
    @Column
    protected boolean prototype;
    @LazyCollection(LazyCollectionOption.FALSE)
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinTable(schema = "Diagrams",
            name = "Item_Relationships",
            joinColumns = {@JoinColumn(name = "Item_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Relationship_Id",
                    referencedColumnName = "Id")})
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    private Set<Relationship> relationships = new LinkedHashSet<>();

    @ManyToOne(cascade = CascadeType.REMOVE)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    @NotNull
    protected DiagramType diagramType;


    public Item() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ElementKind getKind() {
        return kind;
    }

    public void setKind(ElementKind kind) {
        this.kind = kind;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CompositeElement getParent() {
        return parent;
    }

    public void setParent(CompositeElement parent) {
        this.parent = parent;
    }

    public Shape getShape() {
        return shape;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }

    public Set<ConnectTrigger> getCanBeConnectedFrom() {
        return canBeConnectedFrom;
    }

    public void setCanBeConnectedFrom(Set<ConnectTrigger> canBeConnectedFrom) {
        this.canBeConnectedFrom.clear();
        this.canBeConnectedFrom.addAll(canBeConnectedFrom);
    }

    public Set<ConnectTrigger> getCanBeConnectedTo() {
        return canBeConnectedTo;
    }

    public void setCanBeConnectedTo(Set<ConnectTrigger> canBeConnectedTo) {
        this.canBeConnectedTo.clear();
        this.canBeConnectedTo.addAll(canBeConnectedTo);
    }

    public MetaData getMetaData() {
        return metaData;
    }

    public void setMetaData(MetaData metaData) {
        this.metaData = metaData;
    }

    public Set<CompositeElement> getChildren() {
        return children;
    }

    public void setChildren(Set<CompositeElement> children) {
        this.children.clear();
        this.children.addAll(children);
    }

    public Image getImage() {
        return image;
    }

    public void setImage(Image image) {
        this.image = image;
    }

    public boolean isPrototype() {
        return prototype;
    }

    public void setPrototype(boolean prototype) {
        this.prototype = prototype;
    }

    public Set<Relationship> getRelationships() {
        return relationships;
    }

    public void setRelationships(Set<Relationship> relationships) {
        this.relationships.clear();
        this.relationships.addAll(relationships);
    }

    public DiagramType getDiagramType() {
        return diagramType;
    }

    public void setDiagramType(DiagramType diagramType) {
        this.diagramType = diagramType;
    }


    @Override
    public void override(BaseEntity source) {
        override(source, false, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta) {
        override(source, keepMeta, null, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        if (clonedFrom != null && clonedFrom.getType() != null) {
            this.setClonedFrom(clonedFrom);
        }
        Item source1 = (Item) source;
        this.name = StringUtils.isNotBlank(suffix) ? source1.getName() + " " + suffix : source1.getName();
        this.description = source1.getDescription();
        this.parent = source1.getParent();
        this.children = source1.getChildren();
        Shape shape1 = source1.getShape();
        Image image1 = source1.getImage();
        this.shape = Helper.fixItem(shape1, this.shape, keepMeta, suffix, clonedFrom, true);
        this.image = Helper.fixItem(image1, this.image, keepMeta, suffix, clonedFrom, true);
        Set<ConnectTrigger> canBeConnectedFrom = source1.getCanBeConnectedFrom();
        Collection<ConnectTrigger> remainingCanBeConnectedFrom = CollectionUtils.disjunction(this.canBeConnectedFrom, canBeConnectedFrom);
        for (ConnectTrigger item : this.canBeConnectedFrom) {
            ConnectTrigger found = IterableUtils.find(remainingCanBeConnectedFrom, item_ -> connectTriggerComparator.compare(item_, item) == 0);
            if (found != null) {
                this.canBeConnectedFrom.remove(found);
            }
        }
        for (ConnectTrigger item : canBeConnectedFrom) {
            ConnectTrigger newConnectTrigger = new ConnectTrigger();
            newConnectTrigger.override(item);
            ConnectTrigger found = IterableUtils.find(this.canBeConnectedFrom, item_ -> connectTriggerComparator.compare(item_, newConnectTrigger) == 0);
            if (found != null) {
                this.canBeConnectedFrom.add(found);
            } else {
                this.canBeConnectedFrom.add(item);
            }
        }
        Set<ConnectTrigger> canBeConnectedTo = source1.getCanBeConnectedTo();
        Collection<ConnectTrigger> remainingCanBeConnectedTo = CollectionUtils.disjunction(this.canBeConnectedTo, canBeConnectedTo);
        for (ConnectTrigger item : this.canBeConnectedTo) {
            ConnectTrigger found = IterableUtils.find(remainingCanBeConnectedTo, item_ -> connectTriggerComparator.compare(item_, item) == 0);
            if (found != null) {
                this.canBeConnectedTo.remove(found);
            }
        }
        for (ConnectTrigger item : canBeConnectedTo) {
            ConnectTrigger newConnectTrigger = new ConnectTrigger();
            newConnectTrigger.override(item);
            ConnectTrigger found = IterableUtils.find(this.canBeConnectedTo, item_ -> connectTriggerComparator.compare(item_, newConnectTrigger) == 0);
            if (found != null) {
                this.canBeConnectedTo.add(found);
            } else {
                this.canBeConnectedTo.add(item);
            }
        }
        MetaData metaData1 = source1.getMetaData();
        this.metaData = Helper.fixItem(metaData1, this.metaData, keepMeta, suffix, clonedFrom, true);
        Set<Relationship> relationships = source1.getRelationships();
        Collection<Relationship> remainingRelationships = CollectionUtils.disjunction(this.relationships, relationships);
        for (Relationship item : this.relationships) {
            Relationship found = IterableUtils.find(remainingRelationships, item_ -> relationshipComparator.compare(item_, item) == 0);
            if (found != null) {
                this.relationships.remove(found);
            }
        }
        for (Relationship item : relationships) {
            Relationship newRelationship = new Relationship();
            newRelationship.override(item);
            Relationship found = IterableUtils.find(this.relationships, item_ -> relationshipComparator.compare(item_, newRelationship) == 0);
            if (found != null) {
                this.relationships.add(found);
            } else {
                this.relationships.add(item);
            }
        }
        this.diagramType = source1.getDiagramType();
        this.prototype = source1.isPrototype();
    }

    @Override
    public void copyNonEmpty(BaseEntity source) {
        copyNonEmpty(source, false);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta) {
        copyNonEmpty(source, keepMeta, itemComparator);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        super.copyNonEmpty(source, keepMeta, comparator);
        Item source_ = (Item) source;
        if (source_.getName() != null) {
            this.name = source_.getName();
        }
        if (source_.getDescription() != null) {
            this.description = source_.getDescription();
        }
        if (source_.getParent() != null) {
            this.parent = source_.getParent();
        }
        if (source_.getChildren() != null && !source_.getChildren().isEmpty()) {
            this.children = source_.getChildren();
        }
        if (source_.getShape() != null) {
            Shape shape = this.shape;
            if (shape == null) {
                shape = new Shape();
            }
            shape.copyNonEmpty(source_.getShape(), keepMeta, comparator);
            this.setShape(shape);
        }
        if (source_.getImage() != null) {
            Image image = new Image();
            image.copyNonEmpty(source_.getImage(), keepMeta, comparator);
            this.image = image;
        }
        if (source_.getCanBeConnectedFrom() != null && !source_.getCanBeConnectedFrom().isEmpty()) {
            this.canBeConnectedFrom = source_.getCanBeConnectedFrom();
        }
        if (source_.getCanBeConnectedTo() != null && !source_.getCanBeConnectedTo().isEmpty()) {
            this.canBeConnectedTo = source_.getCanBeConnectedTo();
        }
        if (source_.getMetaData() != null) {
            MetaData metaData = this.metaData;
            if (metaData == null) {
                metaData = new MetaData();
            }
            metaData.copyNonEmpty(source_.getMetaData(), keepMeta, comparator);
            this.setMetaData(metaData);

        }
        this.prototype = source_.isPrototype();
        Set<Relationship> relationships = source_.getRelationships();
        if (relationships != null && !relationships.isEmpty()) {
            for (Relationship relationship : relationships) {
                Relationship newRelationship = new Relationship();
                newRelationship.copyNonEmpty(relationship, keepMeta, comparator);
                if (!this.relationships.add(newRelationship)) {
                    this.relationships.remove(newRelationship);
                    this.relationships.add(newRelationship);
                }

            }
        }
        if (source_.getDiagramType() != null) {
            this.diagramType = source_.getDiagramType();
        }
    }

    public CompositeElement buildCompositeElement() {
        CompositeElement compositeElement = new CompositeElement();
        compositeElement.setId(this.getId());
        compositeElement.setType(this.getKind());
        compositeElement.setLink("/models/" + this.getId());
        compositeElement.setVersion(this.getMeta().getActiveVersion());
        return compositeElement;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Item item = (Item) o;
        return itemComparator.compare(this, item) == 0;
    }

    @Override
    public int hashCode() {
        int result = getName() != null ? getName().hashCode() : 0;
        result = 31 * result + (getKind() != null ? getKind().hashCode() : 0);
        result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
        result = 31 * result + (getParent() != null ? getParent().hashCode() : 0);
        result = 31 * result + (getChildren() != null ? getChildren().hashCode() : 0);
        result = 31 * result + (getShape() != null ? getShape().hashCode() : 0);
        result = 31 * result + (getImage() != null ? getImage().hashCode() : 0);
        result = 31 * result + (getCanBeConnectedFrom() != null ? getCanBeConnectedFrom().hashCode() : 0);
        result = 31 * result + (getCanBeConnectedTo() != null ? getCanBeConnectedTo().hashCode() : 0);
        result = 31 * result + (getMetaData() != null ? getMetaData().hashCode() : 0);
        result = 31 * result + (isPrototype() ? 1 : 0);
        result = 31 * result + (getRelationships() != null ? getRelationships().hashCode() : 0);
        result = 31 * result + (getDiagramType() != null ? getDiagramType().hashCode() : 0);
        return result;
    }
}