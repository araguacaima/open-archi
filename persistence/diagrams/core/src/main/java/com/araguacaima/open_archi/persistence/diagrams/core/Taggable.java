package com.araguacaima.open_archi.persistence.diagrams.core;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.*;

import static com.araguacaima.open_archi.persistence.diagrams.core.Item.itemComparator;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@PersistenceUnit(unitName = "open-archi")
@Table(name = "Models", schema = "Diagrams", uniqueConstraints = @UniqueConstraint(columnNames = {"name", "kind"}))
@DynamicUpdate
@DiscriminatorColumn(name = "modelType", discriminatorType = DiscriminatorType.STRING)
public abstract class Taggable extends BaseEntity {

    public static final Integer DEFAULT_RANK = -1;

    @ElementCollection
    @CollectionTable(name = "Tags", schema = "Diagrams")
    protected Set<String> tags = new LinkedHashSet<>();


    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private Status status = Status.INITIAL;

    @OneToOne
    private ElementRole role;

    @OneToOne
    private CompositeElement clonedFrom;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Taggable_Cloned_By_Ids",
            joinColumns = {@JoinColumn(name = "Taggable_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Cloned_Tagabble_Id",
                    referencedColumnName = "Id")})
    private Set<CompositeElement> clonedBy = new LinkedHashSet<>();


    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Taggable_Ranks",
            joinColumns = {@JoinColumn(name = "Taggable_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Rank_Id",
                    referencedColumnName = "Id")})
    @Cascade(org.hibernate.annotations.CascadeType.REMOVE)
    @Transient
    private Set<Rank> ranks = new LinkedHashSet<>();

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags.clear();
        this.tags.addAll(tags);
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ElementRole getRole() {
        return role;
    }

    public void setRole(ElementRole role) {
        this.role = role;
    }

    public CompositeElement getClonedFrom() {
        return clonedFrom;
    }

    public void setClonedFrom(CompositeElement clonedFrom) {
        this.clonedFrom = clonedFrom;
    }

    public Set<CompositeElement> getClonedBy() {
        return clonedBy;
    }

    public void setClonedBy(Set<CompositeElement> clonedBy) {
        this.clonedBy.clear();
        this.clonedBy.addAll(clonedBy);
    }

    abstract public boolean isIsGroup();

    abstract public void setIsGroup(boolean container);

    public Set<Rank> getRanks() {
        return ranks;
    }

    public void setRanks(Set<Rank> ranks) {
        this.ranks = ranks;
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
        if (clonedFrom != null) {
            this.setClonedFrom(clonedFrom);
        }
        this.tags = ((Taggable) source).getTags();
        ElementRole role = ((Taggable) source).getRole();
        if (role != null) {
            if (this.role == null) {
                this.role = role;
            } else {
                this.role.override(role, keepMeta, suffix, clonedFrom, comparator);
            }
        } else {
            this.role = null;
        }
        Set<Rank> ranks = ((Taggable) source).getRanks();
        for (Rank rank : ranks) {
            if (!this.ranks.add(rank)) {
                this.ranks.remove(rank);
                this.ranks.add(rank);
            }
        }

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
        Taggable source1 = (Taggable) source;
        if (source1.getTags() != null && !source1.getTags().isEmpty()) {
            this.tags = source1.getTags();
        }
        if (source1.getRole() != null) {
            this.role = source1.getRole();
        }
        Set<Rank> ranks = source1.getRanks();
        Helper.fixCollection(ranks, this.ranks, keepMeta, comparator);
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !Taggable.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        Taggable t = (Taggable) o;
        if (this.ranks == null) {
            if (t.getRanks() == null) {
                return 0;
            }
        }
        return CompareToBuilder.reflectionCompare(this.ranks, t.getRanks());
    }
}
