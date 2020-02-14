package com.araguacaima.open_archi.persistence.diagrams.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

import static com.araguacaima.open_archi.persistence.diagrams.meta.History.historyComparator;

/**
 * Created by Alejandro on 19/12/2014.
 */
@PersistenceUnit(unitName = "open-archi")
@Entity
@Table(schema = "Meta", name = "MetaInfo")
@DynamicUpdate
@NamedQueries(value = {@NamedQuery(name = MetaInfo.COUNT_ALL_META_INFO,
        query = "select count(a) from MetaInfo a"), @NamedQuery(
        name = MetaInfo.GET_ALL_META_INFO,
        query = "select a from MetaInfo a order by a.created"),
        @NamedQuery(name = MetaInfo.GET_META_INFO_BY_VERSION,
                query = "select m " +
                        "from MetaInfo m " +
                        "   inner join m.history h " +
                        "   inner join h.version v " +
                        "where v = :version"),
        @NamedQuery(name = MetaInfo.GET_LAST_META_INFO,
                query = "select m " +
                        "from MetaInfo m " +
                        "   inner join m.history h " +
                        "   inner join h.version v " +
                        "where v = :version")})
public class MetaInfo implements Serializable, Comparable<MetaInfo>, Storable {

    public static final String GET_ALL_META_INFO = "MetaInfo.getAllMetaInfo";
    public static final String COUNT_ALL_META_INFO = "MetaInfo.countAllMetaInfo";
    public static final String GET_META_INFO_BY_VERSION = "get.metainfo.by.version";
    public static final String GET_LAST_META_INFO = "get.last.meta.info";

    @Id
    private String id;

    @LazyCollection(LazyCollectionOption.FALSE)
    @ManyToMany(cascade = CascadeType.REMOVE)
    @JoinTable(schema = "Meta",
            name = "MetaInfo_History_Ids",
            joinColumns = {@JoinColumn(name = "MetaInfo_Id",
                    referencedColumnName = "Id")},
            inverseJoinColumns = {@JoinColumn(name = "History_Id",
                    referencedColumnName = "Id")})
    private Set<History> history = new TreeSet<>();

    @NotNull
    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private Account createdBy;

    @Column(nullable = false)
    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @OneToOne(cascade = CascadeType.REMOVE)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private ApprovalWorkflow approvalWorkflow;

    public MetaInfo() {
        this.id = UUID.randomUUID().toString();
        this.created = Calendar.getInstance().getTime();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Account getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Account createdBy) {
        this.createdBy = createdBy;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Set<History> getHistory() {
        return history;
    }

    public void setHistory(Set<History> history) {
        this.history.clear();
        this.history.addAll(history);
    }

    public ApprovalWorkflow getApprovalWorkflow() {
        return approvalWorkflow;
    }

    public void setApprovalWorkflow(ApprovalWorkflow approvalWorkflow) {
        this.approvalWorkflow = approvalWorkflow;
    }

    public void addHistory(History history) {
        this.history.add(history);
    }

    public void addNewHistory(Date time) {
        History history = new History(time);
        Version version = getActiveVersion().nextBuild();
        history.setVersion(version);
        this.history.add(history);
    }

    public void addNewHistory(Date time, Account modifiedBy) {
        History history = new History(time);
        Version version = getActiveVersion().nextBuild();
        history.setVersion(version);
        history.setModifiedBy(modifiedBy);
        this.history.add(history);
    }

    @JsonIgnore
    public Version getActiveVersion() {
        return getActiveHistory().getVersion();
    }

    @JsonIgnore
    public History getActiveHistory() {
        History history = IterableUtils.find(this.history, object -> VersionStatus.ACTIVE.equals(object.getStatus()));
        if (history == null) {
            history = new History();
            history.setVersion(new Version());
            history.setStatus(VersionStatus.ACTIVE);
            this.history.add(history);
        }
        return history;
    }

    public void override(MetaInfo source) {
        Set<History> target = this.history;
        Set<History> sourceHistory = source.getHistory();
        Collection<History> remaining = CollectionUtils.disjunction(target, sourceHistory);
        Collection<History> toRemove = new ArrayList<>();
        for (History item : target) {
            History found = IterableUtils.find(remaining, item_ -> historyComparator.compare(item_, item) == 0);
            if (found != null) {
                toRemove.add(found);
            }
        }
        target.removeAll(toRemove);
        for (History item : sourceHistory) {
            History newItem = new History();
            newItem.override(item);
            History found = IterableUtils.find(target, item_ -> historyComparator.compare(item_, newItem) == 0);
            if (found != null) {
                target.add(found);
            } else {
                target.add(item);
            }
        }
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        final Object cloned = super.clone();
        ((MetaInfo) cloned).id = UUID.randomUUID().toString();
        return cloned;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        MetaInfo metaInfo = (MetaInfo) o;

        return new EqualsBuilder().append(id, metaInfo.id)
                .append(history, metaInfo.history)
                .append(createdBy, metaInfo.createdBy)
                .append(created, metaInfo.created)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(id)
                .append(history)
                .append(createdBy)
                .append(created)
                .toHashCode();
    }

    @Override
    public int compareTo(MetaInfo o) {
        if (o == null) {
            return 0;
        }
        return this.created.compareTo(o.getCreated());
    }

}
