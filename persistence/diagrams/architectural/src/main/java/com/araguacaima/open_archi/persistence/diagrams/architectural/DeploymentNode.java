package com.araguacaima.open_archi.persistence.diagrams.architectural;

import com.araguacaima.open_archi.persistence.diagrams.core.*;
import org.apache.commons.collections4.IterableUtils;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>
 * Represents a deployment node, which is something like:
 * </p>
 * <p>
 * <ul>
 * <li>Physical infrastructure (e.g. a physical server or device)</li>
 * <li>Virtualised infrastructure (e.g. IaaS, PaaS, a virtual machine)</li>
 * <li>Containerised infrastructure (e.g. a Docker container)</li>
 * <li>Database server</li>
 * <li>Java EE web/application server</li>
 * <li>Microsoft IIS</li>
 * <li>etc</li>
 * </ul>
 */
@Entity
@PersistenceUnit(unitName = "open-archi")
public class DeploymentNode extends Element implements DiagramableElement<DeploymentNode> {

    @Column
    private String technology;

    @Column
    private int instances = 0;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "DeploymentNode_ContainerInstances",
            joinColumns = {@JoinColumn(name = "DeploymentNode_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "ContainerInstance_Id",
                    referencedColumnName = "Id")})
    private Set<ContainerInstance> containerInstances = new LinkedHashSet<>();

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public int getInstances() {
        return instances;
    }

    public void setInstances(int instances) {
        this.instances = instances;
    }


    @Override
    public ElementKind getKind() {
        return ElementKind.DEPLOYMENT;
    }

    public Set<ContainerInstance> getContainerInstances() {
        return containerInstances;
    }

    public void setContainerInstances(Set<ContainerInstance> containerInstances) {
        this.containerInstances.clear();
        this.containerInstances.addAll(containerInstances);
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
        DeploymentNode source1 = (DeploymentNode) source;
        this.setInstances(source1.getInstances());
        this.setTechnology(source1.getTechnology());
        Set<ContainerInstance> containerInstances = source1.getContainerInstances();
        Helper.fixCollection(containerInstances, this.containerInstances, keepMeta, suffix, clonedFrom, comparator);
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
        DeploymentNode source1 = (DeploymentNode) source;
        this.setInstances(source1.getInstances());
        this.setTechnology(source1.getTechnology());
        Set<ContainerInstance> containerInstances = source1.getContainerInstances();
        Helper.fixCollection(containerInstances, this.containerInstances, keepMeta, comparator);
    }

    @Override
    public boolean isIsGroup() {
        return true;
    }

    @Override
    public void setIsGroup(boolean container) {

    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null) {
            return -1;
        }
        return Comparator.comparing(DeploymentNode::getKind)
                .thenComparing(DeploymentNode::getName)
                .compare(this, (DeploymentNode) o);
    }
}