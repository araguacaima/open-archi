package com.araguacaima.open_archi.persistence.diagrams.meta;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@PersistenceUnit(unitName = "open-archi")
@Entity
@Table(schema = "Meta", name = "ApprovalStep")
@DynamicUpdate
public class ApprovalStep implements Storable, Comparable<ApprovalStep> {

    @Id
    private String id;

    @Column
    private short rank;

    @Column
    private boolean lastStep = true;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    @JoinTable(schema = "Meta",
            name = "ApprovalStep_Approvers",
            joinColumns = {@JoinColumn(name = "ApprovalStep_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Approvers_Id",
                    referencedColumnName = "Id")})
    private Set<Approver> approvers = new HashSet<>();

    public ApprovalStep() {
        this.id = UUID.randomUUID().toString();
        this.rank = 0;
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public short getRank() {
        return rank;
    }

    public void setRank(short rank) {
        this.rank = rank;
    }

    public Set<Approver> getApprovers() {
        return approvers;
    }

    public void setApprovers(Set<Approver> approvers) {
        this.approvers = approvers;
    }

    public boolean isLastStep() {
        return lastStep;
    }

    public void setLastStep(boolean lastStep) {
        this.lastStep = lastStep;
    }

    @Override
    public int compareTo(ApprovalStep approvalStep) {
        if (approvalStep == null)
            return 0;

        return this.rank - approvalStep.rank;
    }
}
