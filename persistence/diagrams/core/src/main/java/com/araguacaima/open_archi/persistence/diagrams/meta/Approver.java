package com.araguacaima.open_archi.persistence.diagrams.meta;

import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@PersistenceUnit(unitName = "open-archi")
@Entity
@Table(schema = "Meta", name = "Approver")
@DynamicUpdate
public class Approver extends Account{

    @Column
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus = ApprovalStatus.NOT_VIEWED;

    public ApprovalStatus getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(ApprovalStatus approvalStatus) {
        this.approvalStatus = approvalStatus;
    }
}
