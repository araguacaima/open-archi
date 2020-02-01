package com.araguacaima.open_archi.persistence.diagrams.core;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "ColorScheme", schema = "Diagrams")
@DynamicUpdate
@NamedQueries({@NamedQuery(name = ColorScheme.FIND_BY_ATTRIBUTES_,
        query = "select cs from ColorScheme cs where cs.name=:name and cs.fillColor=:fillColor and cs.strokeColor=:strokeColor and cs.textColor=:textColor")})
public class ColorScheme extends BaseEntity {

    public static final String FIND_BY_ATTRIBUTES = "{ $and: [ { name : '_name_' }, { fillColor : '_fillColor_' }, { strokeColor : '_strokeColor_' }, { textColor : '_textColor_' } ] }";
    public static final String FIND_BY_ATTRIBUTES_ = "color.scheme.find.by.attributes";
    public static final Comparator<ColorScheme> colorSchemeComparator = Comparator.comparing(ColorScheme::getName,
            Comparator.nullsFirst(ColorSchemeOption::compareTo));

    @Column(nullable = false)
    @NotNull
    @Enumerated(EnumType.STRING)
    private ColorSchemeOption name;

    @Column
    private String fillColor;

    @Column
    private String strokeColor;

    @Column
    private String textColor;

    public ColorScheme() {
    }

    public ColorScheme(ColorSchemeOption name, String fillColor, String strokeColor, String textColor) {
        this.name = name;
        this.fillColor = fillColor;
        this.strokeColor = strokeColor;
        this.textColor = textColor;
    }

    public ColorSchemeOption getName() {
        return name;
    }

    public void setName(ColorSchemeOption name) {
        this.name = name;
    }

    public String getFillColor() {
        return fillColor;
    }

    public void setFillColor(String primaryFillColor) {
        this.fillColor = primaryFillColor;
    }

    public String getStrokeColor() {
        return strokeColor;
    }

    public void setStrokeColor(String primaryStrokeColor) {
        this.strokeColor = primaryStrokeColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String primaryTextColor) {
        this.textColor = primaryTextColor;
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
        super.override(source, keepMeta, suffix, clonedBy, comparator);
        ColorScheme source1 = (ColorScheme) source;
        this.setName(source1.getName());
        this.setFillColor(source1.getFillColor());
        this.setStrokeColor(source1.getStrokeColor());
        this.setTextColor(source1.getTextColor());
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
        ColorScheme source1 = (ColorScheme) source;
        if (source1.getName() != null) {
            this.setName(source1.getName());
        }
        if (StringUtils.isNotBlank(source1.getFillColor())) {
            this.setFillColor(source1.getFillColor());
        }
        if (StringUtils.isNotBlank(source1.getStrokeColor())) {
            this.setStrokeColor(source1.getStrokeColor());
        }
        if (StringUtils.isNotBlank(source1.getTextColor())) {
            this.setTextColor(source1.getTextColor());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ColorScheme that = (ColorScheme) o;

        return getName() == that.getName();
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !ColorScheme.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        ColorScheme colorScheme = (ColorScheme) o;
        return this.name.compareTo(colorScheme.getName());
    }
}
