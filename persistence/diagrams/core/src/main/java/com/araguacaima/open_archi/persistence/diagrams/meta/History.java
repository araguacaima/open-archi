package com.araguacaima.open_archi.persistence.diagrams.meta;

import com.araguacaima.orpheusdb.utils.OrpheusDbJPAEntityManagerUtils;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.UUID;

@PersistenceUnit(unitName = "open-archi")
@Entity
@Table(schema = "Meta", name = "History")
@DynamicUpdate
@NamedQueries(value = {
        @NamedQuery(
                name = History.GET_DEFAULT_HISTORY_VERSION,
                query = "select a from History a where a.version.id.major = 1 and a.version.id.minor = 0 and a.version.id.build = 0 order by a.version"),
        @NamedQuery(
                name = History.GET_DEFAULT_ACTIVE_VERSION,
                query = "select a from History a where a.status = 'ACTIVE'")})
public class History implements Serializable, Comparable<History>, Storable {

    public static final String GET_DEFAULT_HISTORY_VERSION = "get.default.history.version";
    public static final String GET_DEFAULT_ACTIVE_VERSION = "get.default.active.version";
    public static final Comparator<History> historyComparator = Comparator.comparing(History::getVersion,
            Comparator.nullsFirst(Version::compareTo));

    @Id
    private String id;

    @NotNull
    @ManyToOne
    private Version version;

    @Column
    @Enumerated(EnumType.STRING)
    private VersionStatus status;

    @Column
    @Temporal(TemporalType.TIMESTAMP)
    private Date modified;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @Cascade({org.hibernate.annotations.CascadeType.REMOVE})
    private Account modifiedBy;

    public History() {
        this.id = UUID.randomUUID().toString();
        this.status = VersionStatus.INITIAL;
    }


    public History(Date modified) {
        this();
        this.modified = modified;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getModified() {
        return modified;
    }

    public Account getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(Account modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public VersionStatus getStatus() {
        return status;
    }

    public void setStatus(VersionStatus status) {
        this.status = status;
    }

    public void override(History source_) {
        Version source = source_.getVersion();
        Version target_;
        if (source != null) {
            target_ = OrpheusDbJPAEntityManagerUtils.find(source);
            if (target_ == null) {
                target_ = this.getVersion();
            }
            if (target_ == null) {
                target_ = source;
            } else {
                target_.override(source);
            }
        } else {
            target_ = null;
        }
        this.version = target_;
        this.status = source_.getStatus();
        this.modified = Calendar.getInstance().getTime();
        this.modifiedBy = source_.modifiedBy;
    }

    @Override
    public int compareTo(History o) {
        int compare = this.version.compareTo(o.getVersion());
        if (compare == 0) {
            if (this.modified == null || o.getModified() == null) {
                return 0;
            }
            return this.modified.compareTo(o.getModified());
        } else {
            return compare;
        }
    }

}
