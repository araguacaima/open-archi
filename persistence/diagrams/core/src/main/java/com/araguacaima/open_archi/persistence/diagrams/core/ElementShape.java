package com.araguacaima.open_archi.persistence.diagrams.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.araguacaima.open_archi.persistence.diagrams.core.Item.itemComparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "ElementShape", schema = "Diagrams")
@DynamicUpdate
@NamedQueries({@NamedQuery(name = ElementShape.GET_ELEMENT_SHAPE_BY_TYPE,
        query = "select a " +
                "from ElementShape a where a.type=:type"),
        @NamedQuery(name = ElementShape.GET_ALL_ELEMENT_SHAPES,
                query = "select a " +
                        "from ElementShape a"),
        @NamedQuery(name = ElementShape.FIND_BY_ATTRIBUTES_,
                query = "select es from ElementShape es where es.type=:type and es.input=:input and es.output=:output and es.figure=:figure and es.isGroup=:isGroup")})
public class ElementShape extends BaseEntity {

    public static final String GET_ELEMENT_SHAPE_BY_TYPE = "get.element.shape.by.type";
    public static final String GET_ALL_ELEMENT_SHAPES = "get.all.element.shapes";
    public static final String FIND_BY_ATTRIBUTES = "{ $and: [ { type : '_type_' }, { input : '_input_' }, { output : '_output_' }, { figure : '_figure_' }, { isGroup : '_isGroup_' } ] }";
    public static final String FIND_BY_ATTRIBUTES_ = "element.shape.find.by.attributes";
    public static final Comparator<ElementShape> elementShapeComparator = Comparator.comparing(ElementShape::getType,
            Comparator.nullsFirst(ElementKind::compareTo));

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private ElementKind type;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Element_Shape_Color_Schemes",
            joinColumns = {@JoinColumn(name = "Element_Shape_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Color_Scheme_id",
                    referencedColumnName = "Id")})
    private Set<ColorScheme> colorSchemes = new LinkedHashSet<>();

    @Column
    private boolean input = true;
    @Column
    private boolean output = true;
    @Column
    private String figure;
    @Column
    private boolean isGroup;

    public ElementShape(ElementKind type) {
        this.type = type;
    }

    public ElementShape() {
    }

    public ElementShape(ElementKind type, boolean input, boolean output, String figure, boolean isGroup) {
        this.type = type;
        this.input = input;
        this.output = output;
        this.figure = figure;
        this.isGroup = isGroup;
    }

    public ElementKind getType() {
        return type;
    }

    public void setType(ElementKind type) {
        this.type = type;
    }

    public Set<ColorScheme> getColorSchemes() {
        return colorSchemes;
    }

    public void setColorSchemes(Set<ColorScheme> colorSchemes) {
        this.colorSchemes.clear();
        this.colorSchemes.addAll(colorSchemes);
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isInput() {
        return input;
    }

    public void setInput(boolean input) {
        this.input = input;
    }

    public boolean isOutput() {
        return output;
    }

    public void setOutput(boolean output) {
        this.output = output;
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public boolean isIsGroup() {
        return isGroup;
    }

    public void setIsGroup(boolean group) {
        this.isGroup = group;
    }

    public void addColorScheme(ColorScheme colorScheme) {
        if (this.colorSchemes == null) {
            this.colorSchemes = new LinkedHashSet<>();
        }
        this.colorSchemes.add(colorScheme);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedBy, Comparator comparator) {
        ElementShape source1 = (ElementShape) source;
        this.setOutput(source1.isOutput());
        this.setInput(source1.isInput());
        this.setFigure(source1.getFigure());
        this.setIsGroup(source1.isIsGroup());
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
        ElementShape source1 = (ElementShape) source;
        this.setOutput(source1.isOutput());
        this.setInput(source1.isInput());
        if (StringUtils.isNotBlank(source1.getFigure())) {
            this.setFigure(source1.getFigure());
        }
        this.setIsGroup(source1.isIsGroup());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ElementShape that = (ElementShape) o;

        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !ElementShape.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        ElementShape elementShape = (ElementShape) o;
        return this.type.compareTo(elementShape.getType());
    }

}
