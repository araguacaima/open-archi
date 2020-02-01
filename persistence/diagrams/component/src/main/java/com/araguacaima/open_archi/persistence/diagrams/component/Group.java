package com.araguacaima.open_archi.persistence.diagrams.component;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A system is the highest level of abstraction and describes something
 * that delivers value to its users, whether they are human or not. This includes
 * the system you are modelling, and the other systems upon
 * which your system depends.
 * <p>
 * See <a href="https://structurizr.com/help/model#System">Model - System Group</a>
 * on the Structurizr website for more information.
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@DiscriminatorValue(value = "ComponentModelGroup")
@NamedQueries({
        @NamedQuery(name = Group.GET_ALL_GROUPS,
                query = "select a from com.araguacaima.open_archi.persistence.diagrams.component.Group a"),
        @NamedQuery(name = Group.GET_ALL_GROUPS_FROM_GROUP,
                query = "select a.groups from com.araguacaima.open_archi.persistence.diagrams.component.Group a where a.id=:id"),
        @NamedQuery(name = Group.GET_ALL_ELEMENTS_FROM_GROUP,
                query = "select a.elements from com.araguacaima.open_archi.persistence.diagrams.component.Group a where a.id=:id"),
        @NamedQuery(name = Group.GET_GROUPS_USAGE_BY_ELEMENT_ID_LIST,
                query = "select s " +
                        "from com.araguacaima.open_archi.persistence.diagrams.component.Group s " +
                        "   left join s.groups grp " +
                        "   left join s.elements ele " +
                        "where grp.id in :" + Item.ELEMENTS_USAGE_PARAM +
                        "   or ele.id in :" + Item.ELEMENTS_USAGE_PARAM)})
public class Group extends ComponentGroupStaticElement implements DiagramableElement<Group> {

    public static final String GET_ALL_GROUPS_FROM_GROUP = "get.all.groups.from.group";
    public static final String GET_ALL_ELEMENTS_FROM_GROUP = "get.all.elements.from.group";
    public static final String GET_ALL_GROUPS = "get.all.groups";
    public static final String GET_GROUPS_USAGE_BY_ELEMENT_ID_LIST = "get.groups.usage.by.element.id.list";

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Group_Components",
            joinColumns = {@JoinColumn(name = "Group_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Component_Id",
                    referencedColumnName = "Id")})
    private Set<Group> groups = new LinkedHashSet<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Group_Components",
            joinColumns = {@JoinColumn(name = "Group_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Component_Id",
                    referencedColumnName = "Id")})
    private Set<ComponentElement> elements = new LinkedHashSet<>();

    public Group() {
        setKind(ElementKind.SYSTEM);
    }

    public Set<Group> getGroups() {
        return groups;
    }

    public void setGroups(Set<Group> groups) {
        this.groups.clear();
        this.groups.addAll(groups);
    }

    public Set<ComponentElement> getElements() {
        return elements;
    }

    public void setElements(Set<ComponentElement> elements) {
        this.elements.clear();
        this.elements.addAll(elements);
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
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        Group source1 = (Group) source;
        Set<Group> groups = source1.getGroups();
        Set<ComponentElement> elements = source1.getElements();
        Helper.fixCollection(groups, this.groups, keepMeta, suffix, clonedFrom, comparator);
        Helper.fixCollection(elements, this.elements, keepMeta, suffix, clonedFrom, comparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
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
        Group source1 = (Group) source;
        Set<Group> groups = source1.getGroups();
        Set<ComponentElement> elements = source1.getElements();
        Helper.fixCollection(groups, this.groups, keepMeta, comparator);
        Helper.fixCollection(elements, this.elements, keepMeta, comparator);
    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(Group::getKind)
                .thenComparing(Group::getName)
                .compare(this, (Group) o);
    }
}
