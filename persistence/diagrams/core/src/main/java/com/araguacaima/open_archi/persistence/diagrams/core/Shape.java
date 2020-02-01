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
@Table(name = "Shape", schema = "Diagrams")
@DynamicUpdate
public class Shape extends BaseEntity {

    public static final Comparator<Shape> shapeComparator = Comparator.comparing(Shape::getType,
            Comparator.nullsFirst(ElementKind::compareTo));

    @Column
    @Enumerated(EnumType.STRING)
    private ElementKind type;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany
    @JoinTable(schema = "Diagrams",
            name = "Shape_Color_Schemes",
            joinColumns = {@JoinColumn(name = "Shape_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Shape_Color_Scheme",
                    referencedColumnName = "Id")})
    private Set<ColorScheme> colorSchemes = new LinkedHashSet<>();

    @Column
    private boolean input = true;

    @Column
    private boolean output = true;

    @Column
    private String figure;

    @Column
    private Boolean isGroup;

    public Shape(ElementKind type) {
        this.type = type;
    }

    public Shape() {
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

    public void addColorScheme(ColorScheme colorScheme) {
        if (this.colorSchemes == null) {
            this.colorSchemes = new LinkedHashSet<>();
        }
        this.colorSchemes.add(colorScheme);
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

    public Boolean isGroup() {
        return isGroup;
    }

    public void setGroup(Boolean group) {
        isGroup = group;
    }

    public Boolean getIsGroup() {
        return isGroup;
    }

    public void setIsGroup(Boolean group) {
        isGroup = group;
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
        override(source, keepMeta, suffix, clonedFrom, shapeComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {

        if (!this.equals(source)) {
            super.override(source, keepMeta, suffix, clonedFrom, comparator);
            Shape source1 = (Shape) source;
            this.setType(source1.getType());
            Set<ColorScheme> colorSchemes = source1.getColorSchemes();
            if (colorSchemes != null) {
                Helper.fixCollection(colorSchemes, this.colorSchemes, keepMeta, suffix, clonedFrom, ColorScheme.colorSchemeComparator);
            }
            this.setColorSchemes(source1.getColorSchemes());
            this.setOutput(source1.isOutput());
            this.setInput(source1.isInput());
            this.setFigure(source1.getFigure());
            this.setGroup(source1.isGroup());
        }
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
        super.copyNonEmpty(source, keepMeta, comparator);
        Shape source1 = (Shape) source;
        if (source1.getType() != null) {
            this.setType(source1.getType());
        }
        if (source1.getColorSchemes() != null) {
            Set<ColorScheme> colorSchemes = source1.getColorSchemes();
            if (colorSchemes != null) {
                Helper.fixCollection(colorSchemes, this.colorSchemes, keepMeta, comparator);
            }
        }
        this.setOutput(source1.isOutput());
        this.setInput(source1.isInput());
        if (StringUtils.isNotBlank(source1.getFigure())) {
            this.setFigure(source1.getFigure());
        }
        if (source1.getIsGroup() != null) {
            this.setGroup(source1.isGroup());
        }

    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !Shape.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
