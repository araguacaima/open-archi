package com.araguacaima.open_archi.persistence.diagrams.core;


import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.PersistenceUnit;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Comparator;

/**
 * A point representing a location in {@code (x,y)} coordinate space,
 * specified in integer precision.
 *
 * @author Sami Shaio
 * @since 1.0
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "Point", schema = "Diagrams")
@DynamicUpdate
public class Point extends BaseEntity implements Serializable, Cloneable {

    private static final long serialVersionUID = -5276940640259749850L;
    /**
     * The X coordinate of this <code>Point</code>.
     * If no X coordinate is set it will default to 0.
     */
    @Column
    private double x;
    /**
     * The Y coordinate of this <code>Point</code>.
     * If no Y coordinate is set it will default to 0.
     */
    @Column
    private double y;

    /**
     * The Z coordinate of this <code>Point</code>.
     * If no Z coordinate is set it will default to 0.
     */
    @Column
    private double z;

    /**
     * Constructs and initializes a point at the origin
     * (0,&nbsp;0) of the coordinate space.
     */
    public Point() {
        this(0, 0, 0);
    }


    /**
     * Constructs and initializes a point at the specified
     * {@code (x,y)} location in the coordinate space.
     *
     * @param x the X coordinate of the newly constructed <code>Point</code>
     * @param y the Y coordinate of the newly constructed <code>Point</code>
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Constructs and initializes a point at the specified
     * {@code (x,y,z)} location in the coordinate space.
     *
     * @param x the X coordinate of the newly constructed <code>Point</code>
     * @param y the Y coordinate of the newly constructed <code>Point</code>
     * @param z the Z coordinate of the newly constructed <code>Point</code>
     */
    public Point(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.2
     */
    public double getX() {
        return x;
    }

    /**
     * {@inheritDoc}
     *
     * @since 1.2
     */
    public double getY() {
        return y;
    }


    /**
     * {@inheritDoc}
     *
     * @since 1.2
     */
    public double getZ() {
        return z;
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
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        Point source1 = (Point) source;
        this.x = source1.getX();
        this.y = source1.getY();
        this.z = source1.getZ();
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, null);
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
        Point source1 = (Point) source;
        if (source1.getX() != 0) {
            this.x = source1.getX();
        }
        if (source1.getY() != 0) {
            this.y = source1.getY();
        }
        if (source1.getZ() != 0) {
            this.z = source1.getZ();
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !Point.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
