package com.araguacaima.open_archi.persistence.diagrams.core;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.araguacaima.open_archi.persistence.diagrams.core.ColorScheme.colorSchemeComparator;
import static com.araguacaima.open_archi.persistence.diagrams.core.ElementShape.elementShapeComparator;
import static com.araguacaima.open_archi.persistence.diagrams.core.Item.itemComparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "DiagramType", schema = "Diagrams")
@DynamicUpdate
@NamedQueries({
        @NamedQuery(name = DiagramType.GET_ALL_ACTIVE_DIAGRAM_TYPES,
                query = "select dt " +
                        "from DiagramType dt where dt.enabled = true"),
        @NamedQuery(name = DiagramType.GET_ALL_DIAGRAM_TYPES,
                query = "select dt " +
                        "from DiagramType dt"),
        @NamedQuery(name = DiagramType.GET_ACTIVE_DIAGRAM_TYPE_BY_TYPE,
                query = "select dt " +
                        "from DiagramType dt where dt.type=:type and dt.enabled=true"),
        @NamedQuery(name = DiagramType.GET_DIAGRAM_TYPE_BY_TYPE,
                query = "select dt " +
                        "from DiagramType dt where dt.type=:type"),
        @NamedQuery(name = DiagramType.GET_ACTIVE_ELEMENT_SHAPES_BY_DIAGRAM_TYPE,
                query = "select dt.elementShapes " +
                        "from DiagramType dt where dt.type=:type and dt.enabled = true"),
        @NamedQuery(name = DiagramType.GET_ELEMENT_SHAPES_BY_DIAGRAM_TYPE,
                query = "select dt.elementShapes " +
                        "from DiagramType dt where dt.type=:type"),
        @NamedQuery(name = DiagramType.GET_DIAGRAM_TYPE_BY_ELEMENT_SHAPE_TYPE,
                query = "select dt from DiagramType dt where dt.elementShapes in (:elementShapeType)"),
        @NamedQuery(name = DiagramType.FIND_BY_ATTRIBUTES_,
                query = "select dt from DiagramType dt where dt.type=:type and dt.type=:type and dt.figure=:figure")})
public class DiagramType extends BaseEntity {
    public static final String GET_ACTIVE_ELEMENT_SHAPES_BY_DIAGRAM_TYPE = "get.active.element.shapes.by.diagram.type";
    public static final String GET_ELEMENT_SHAPES_BY_DIAGRAM_TYPE = "get.element.shapes.by.diagram.type";
    public static final String GET_ACTIVE_DIAGRAM_TYPE_BY_TYPE = "get.active.diagram.type.by.type";
    public static final String GET_DIAGRAM_TYPE_BY_TYPE = "get.diagram.type.by.type";
    public static final String GET_ALL_ACTIVE_DIAGRAM_TYPES = "get.all.active.diagram.types";
    public static final String GET_ALL_DIAGRAM_TYPES = "get.all.diagram.types";
    public static final String GET_DIAGRAM_TYPE_BY_ELEMENT_SHAPE_TYPE = "get.diagram.type.by.element.shape.type";
    public static final String FIND_BY_ATTRIBUTES = "{ $and: [ { type : '_type_' }, { figure : '_figure_' }, ] }";
    public static final String FIND_BY_ATTRIBUTES_ = "diagram.type.find.by.attributes";

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private ElementKind type;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Diagram_Type_Color_Schemes",
            joinColumns = {@JoinColumn(name = "Diagram_Type_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Color_Scheme_Id",
                    referencedColumnName = "Id")})
    private Set<ColorScheme> colorSchemes = new LinkedHashSet<>();


    @LazyCollection(LazyCollectionOption.TRUE)
    @ManyToMany
    @JoinTable(schema = "Diagrams",
            name = "Diagram_Type_Element_Shapes",
            joinColumns = {@JoinColumn(name = "Diagram_Type_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Element_Shape_Id",
                    referencedColumnName = "Id")})
    private Set<ElementShape> elementShapes = new LinkedHashSet<>();

    @Column
    private String figure;

    @Column
    private boolean enabled = true;


    public DiagramType(ElementKind type) {
        this.type = type;
    }

    public DiagramType() {
        setType(ElementKind.DEFAULT);
    }

    public DiagramType(ElementKind type, String figure, boolean enabled) {
        this.type = type;
        this.figure = figure;
        this.enabled = enabled;
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

    public Set<ElementShape> getElementShapes() {
        return elementShapes;
    }

    public void setElementShapes(Set<ElementShape> elementShapes) {
        this.elementShapes.clear();
        this.elementShapes.addAll(elementShapes);
    }

    public String getFigure() {
        return figure;
    }

    public void setFigure(String figure) {
        this.figure = figure;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
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
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        DiagramType source1 = (DiagramType) source;
        Set<ColorScheme> colorSchemes = source1.getColorSchemes();
        Set<ElementShape> elementShapes = source1.getElementShapes();
        Helper.fixCollection(colorSchemes, this.colorSchemes, keepMeta, suffix, clonedFrom, colorSchemeComparator);
        Helper.fixCollection(elementShapes, this.elementShapes, keepMeta, suffix, clonedFrom, elementShapeComparator);
        this.setFigure(source1.getFigure());
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
        DiagramType source1 = (DiagramType) source;
        Set<ColorScheme> colorSchemes = source1.getColorSchemes();
        Set<ElementShape> elementShapes = source1.getElementShapes();
        Helper.fixCollection(colorSchemes, this.colorSchemes, keepMeta, comparator);
        Helper.fixCollection(elementShapes, this.elementShapes, keepMeta, comparator);
        if (StringUtils.isNotBlank(source1.getFigure())) {
            this.setFigure(source1.getFigure());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiagramType that = (DiagramType) o;

        return getType() == that.getType();
    }

    @Override
    public int hashCode() {
        return getType().hashCode();
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !DiagramType.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        DiagramType elementShape = (DiagramType) o;
        return this.type.compareTo(elementShape.getType());
    }
}
