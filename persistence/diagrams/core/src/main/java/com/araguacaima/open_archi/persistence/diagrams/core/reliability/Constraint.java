package com.araguacaima.open_archi.persistence.diagrams.core.reliability;

import com.araguacaima.open_archi.persistence.commons.exceptions.EntityError;
import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.CompositeElement;
import org.apache.commons.lang3.builder.CompareToBuilder;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.Set;


@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(schema = "Diagrams", name = "Constraint")
@DynamicUpdate
public class Constraint extends BaseEntity {

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Constraint_Activities",
            joinColumns = {@JoinColumn(name = "Constraint_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Activities_Id",
                    referencedColumnName = "Id")})
    private Set<ReliabilityActivity> activities;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Constraint_BatchProcessing",
            joinColumns = {@JoinColumn(name = "Constraint_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "BatchProcessing_Id",
                    referencedColumnName = "Id")})
    private Set<BatchProcessing> batchProcessing;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Constraint_BulkProcessing",
            joinColumns = {@JoinColumn(name = "Constraint_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "BulkProcessing_Id",
                    referencedColumnName = "Id")})
    private Set<BulkProcessing> bulkProcessing;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Constraint_Concurrency",
            joinColumns = {@JoinColumn(name = "Constraint_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Concurrency_Id",
                    referencedColumnName = "Id")})
    private Set<Concurrency> concurrencies;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Constraint_Database",
            joinColumns = {@JoinColumn(name = "Constraint_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Database_Id",
                    referencedColumnName = "Id")})
    private Set<Database> dataBase;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Constraint_FileTransfer",
            joinColumns = {@JoinColumn(name = "Constraint_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "FileTransfer_Id",
                    referencedColumnName = "Id")})
    private Set<FileTransfer> fileTransfers;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Diagrams",
            name = "Constraint_Rate",
            joinColumns = {@JoinColumn(name = "Constraint_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Rate_Id",
                    referencedColumnName = "Id")})
    private Set<Rate> rates;

    public Set<ReliabilityActivity> getActivities() {
        return activities;
    }

    public void setActivities(Set<ReliabilityActivity> activities) {
        this.activities.clear();
        this.activities.addAll(activities);
    }

    public Set<BatchProcessing> getBatchProcessing() {
        return batchProcessing;
    }

    public void setBatchProcessing(Set<BatchProcessing> batchProcessing) {
        this.batchProcessing.clear();
        this.batchProcessing.addAll(batchProcessing);
    }

    public Set<BulkProcessing> getBulkProcessing() {
        return bulkProcessing;
    }

    public void setBulkProcessing(Set<BulkProcessing> bulkProcessing) {
        this.bulkProcessing.clear();
        this.bulkProcessing.addAll(bulkProcessing);
    }

    public Set<Concurrency> getConcurrencies() {
        return concurrencies;
    }

    public void setConcurrencies(Set<Concurrency> concurrencies) {
        this.concurrencies.clear();
        this.concurrencies.addAll(concurrencies);
    }

    public Set<Database> getDataBase() {
        return dataBase;
    }

    public void setDataBase(Set<Database> dataBase) {
        this.dataBase.clear();
        this.dataBase.addAll(dataBase);
    }

    public Set<FileTransfer> getFileTransfers() {
        return fileTransfers;
    }

    public void setFileTransfers(Set<FileTransfer> fileTransfers) {
        this.fileTransfers.clear();
        this.fileTransfers.addAll(fileTransfers);
    }

    public Set<Rate> getRates() {
        return rates;
    }

    public void setRates(Set<Rate> rates) {
        this.rates.clear();
        this.rates.addAll(rates);
    }

    @Override
    public <T extends BaseEntity> T validateRequest() throws EntityError {
        //Do nothing. All request are valid on this entity
        return (T) this;
    }

    @Override
    public <T extends BaseEntity> T validateCreation() {
        if (!(activities != null || batchProcessing != null || bulkProcessing != null || concurrencies != null || dataBase != null || fileTransfers != null || rates != null)) {
            throw new EntityError(resourceBundle.getString(getCreationErrorMessageKey()));
        }
        return (T) this;
    }

    @Override
    public <T extends BaseEntity> T validateModification() throws EntityError {
        if (!(id == null && (
                activities != null || batchProcessing != null || bulkProcessing != null || concurrencies != null || dataBase != null || fileTransfers != null || rates != null))) {
            throw new EntityError(resourceBundle.getString(getModificationErrorMessageKey()));
        }
        return (T) this;
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
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedBy, Comparator comparator) {
        this.setActivities(((Constraint) source).getActivities());
        this.setBatchProcessing(((Constraint) source).getBatchProcessing());
        this.setBulkProcessing(((Constraint) source).getBulkProcessing());
        this.setConcurrencies(((Constraint) source).getConcurrencies());
        this.setDataBase(((Constraint) source).getDataBase());
        this.setFileTransfers(((Constraint) source).getFileTransfers());
        this.setRates(((Constraint) source).getRates());
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {

        if (((Constraint) source).getActivities() != null && !((Constraint) source).getActivities().isEmpty()) {
            this.setActivities(((Constraint) source).getActivities());
        }
        if (((Constraint) source).getActivities() != null && !((Constraint) source).getActivities().isEmpty()) {
            this.setActivities(((Constraint) source).getActivities());
        }
        if (((Constraint) source).getActivities() != null && !((Constraint) source).getActivities().isEmpty()) {
            this.setActivities(((Constraint) source).getActivities());
        }
        if (((Constraint) source).getActivities() != null && !((Constraint) source).getActivities().isEmpty()) {
            this.setActivities(((Constraint) source).getActivities());
        }
        if (((Constraint) source).getActivities() != null && !((Constraint) source).getActivities().isEmpty()) {
            this.setActivities(((Constraint) source).getActivities());
        }
        this.setBatchProcessing(((Constraint) source).getBatchProcessing());
        this.setBulkProcessing(((Constraint) source).getBulkProcessing());
        this.setConcurrencies(((Constraint) source).getConcurrencies());
        this.setDataBase(((Constraint) source).getDataBase());
        this.setFileTransfers(((Constraint) source).getFileTransfers());
        this.setRates(((Constraint) source).getRates());

    }

    @Override
    public int compareTo(@NotNull Object o) {
        if (o == null || !Range.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        return CompareToBuilder.reflectionCompare(this, o);
    }
}
