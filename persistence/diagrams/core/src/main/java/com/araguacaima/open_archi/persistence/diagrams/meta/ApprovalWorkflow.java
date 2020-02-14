package com.araguacaima.open_archi.persistence.diagrams.meta;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.util.*;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "ApprovalWorkfow", schema = "Diagrams")
@DynamicUpdate
public class ApprovalWorkflow implements Storable {

    @Id
    private String id;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Meta",
            name = "Approval_Workflow_Steps",
            joinColumns = {@JoinColumn(name = "Approval_Workflow_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "Step_Id",
                    referencedColumnName = "Id")})
    private List<ApprovalStep> steps = new LinkedList<>();

    public ApprovalWorkflow() {
        this.id = UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Set<Approver> getCurrentStepApprovers() {
        return getCurrentStep().getApprovers();
    }

    public List<ApprovalStep> getSteps() {
        if (steps != null) return steps;
        this.steps = new LinkedList<>();
        return this.steps;
    }

    public void setSteps(List<ApprovalStep> steps) {
        this.steps = steps;
    }

    public void addStep(ApprovalStep step) {
        this.getSteps().add(step);
    }

    public ApprovalStep getCurrentStep() {
        List<ApprovalStep> steps = this.getSteps();
        Collections.sort(steps);
        for (ApprovalStep step : this.steps) {
            boolean notViewed = true;
            for (Approver approver : step.getApprovers()) {
                notViewed = notViewed & !ApprovalStatus.NOT_VIEWED.equals(approver.getApprovalStatus());
            }
            if (notViewed) {
                return step;
            }
        }
        return steps.stream().findFirst().get();
    }
}

