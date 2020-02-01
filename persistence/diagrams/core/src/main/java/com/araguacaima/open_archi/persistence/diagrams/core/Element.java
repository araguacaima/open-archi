package com.araguacaima.open_archi.persistence.diagrams.core;

import org.apache.commons.collections4.IterableUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;

import static com.araguacaima.open_archi.persistence.diagrams.core.Helper.fixMap;

/**
 * This is the superclass for all model elements.
 */

@Entity
@PersistenceUnit(unitName = "open-archi")
@NamedQueries({@NamedQuery(name = Element.GET_ALL_FEATURES,
        query = "select a.features from Element a where a.id=:id")})
public abstract class Element extends Item {

    public static final String GET_ALL_FEATURES = "get.all.features";

    @Column
    protected String url;

    @ElementCollection
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    @CollectionTable(schema = "Diagrams", name = "Element_Properties", joinColumns = @JoinColumn(name = "Property_Id"))
    protected Map<String, String> properties = new HashMap<>();

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Element_FeatureIds",
            joinColumns = {@JoinColumn(name = "Architecture_Model_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "System_Id",
                    referencedColumnName = "Id")})
    protected Set<Feature> features = new LinkedHashSet<>();

    public Element() {
    }

    /**
     * Gets the URL where more information about this element can be found.
     *
     * @return a URL as a String
     */
    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    public Set<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(Set<Feature> features) {
        this.features.clear();
        this.features.addAll(features);
    }

    @Override
    public void override(BaseEntity source) {
        override(source, false);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta) {
        override(source, keepMeta, null, null, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, itemComparator);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        Element source1 = (Element) source;
        this.url = source1.getUrl();
        Map<String, String> properties = source1.getProperties();
        fixMap(properties, this.properties);
        Set<Feature> features = source1.getFeatures();
        Helper.fixCollection(features, this.features, keepMeta, suffix, clonedFrom, comparator);
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
        Element source1 = (Element) source;
        this.url = source1.getUrl();
        Map<String, String> properties = source1.getProperties();
        fixMap(properties, this.properties);
        Set<Feature> features = source1.getFeatures();
        Helper.fixCollection(features, this.features, keepMeta, comparator);
    }
}