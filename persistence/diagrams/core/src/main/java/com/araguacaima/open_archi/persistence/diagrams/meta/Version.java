package com.araguacaima.open_archi.persistence.diagrams.meta;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.beans.Transient;
import java.io.Serializable;

@PersistenceUnit(unitName = "open-archi")
@Entity
@Table(schema = "Meta", name = "Version")
@DynamicUpdate
@NamedQueries(value = {@NamedQuery(name = Version.COUNT_ALL_VERSIONS,
        query = "select count(a) from Version a"), @NamedQuery(name = Version.GET_DEFAULT_VERSION,
        query = "select a from Version a where a.id.major = 1 and a.id.minor = 0 and a.id.build = 0"), @NamedQuery(
        name = Version.GET_ALL_VERSIONS,
        query = "select a from Version a order by a.id.major, a.id.minor, a.id.build"), @NamedQuery(
        name = Version.FIND_VERSION,
        query = "select a from Version a where a.id.major = :major and a.id.minor = :minor and a.id.build = :build order by a.id.major, a.id.minor, a.id.build")})
public class Version implements Serializable, Comparable<Version>, Cloneable {

    public static final String GET_ALL_VERSIONS = "Version.getAllVersions";
    public static final String COUNT_ALL_VERSIONS = "Version.countAllVersions";
    public static final String GET_DEFAULT_VERSION = "Version.getDefaultVersion";
    public static final String FIND_VERSION = "Version.findByMajorMinorAndBuild";
    public static final String FIND_BY_ID = "Version.findById";
    public static final String PARAM_ID = "id";
    public static final String PARAM_MAJOR = "major";
    public static final String PARAM_MINOR = "minor";
    public static final String PARAM_BUILD = "build";

    private static final long serialVersionUID = -5350803918802322500L;

    @EmbeddedId
    @JsonIgnore
    private VersionId id = new VersionId();

    public Version() {
    }

    public Version(Integer major, Integer minor, Integer build) {
        this();
        this.id.setMajor(major);
        this.id.setMinor(minor);
        this.id.setBuild(build);
    }

    public Version(String version) throws NumberFormatException {
        this();
        if (StringUtils.isNotBlank(version)) {
            version = version.trim();
            String[] versionSplitted;

            versionSplitted = version.split("\\.");
            try {
                this.id.setMajor(Integer.valueOf(versionSplitted[0]));
            } catch (IndexOutOfBoundsException ignored) {
            } catch (NumberFormatException nfe) {
                this.id.setMajor(Integer.valueOf(versionSplitted[0].substring(0, 1)));
            }
            try {
                this.id.setMinor(Integer.valueOf(versionSplitted[1]));
            } catch (IndexOutOfBoundsException ignored) {
            } catch (NumberFormatException nfe) {
                this.id.setMinor(Integer.valueOf(versionSplitted[1].substring(0, 1)));
            }
            if (this.id.getMajor() == null) {
                versionSplitted = version.split(",");
                try {
                    this.id.setMajor(Integer.valueOf(versionSplitted[0]));
                } catch (IndexOutOfBoundsException ignored) {
                } catch (NumberFormatException nfe) {
                    this.id.setMajor(Integer.valueOf(versionSplitted[0].substring(0, 1)));
                }
                try {
                    this.id.setMinor(Integer.valueOf(versionSplitted[1]));
                } catch (IndexOutOfBoundsException ignored) {
                } catch (NumberFormatException nfe) {
                    this.id.setMinor(Integer.valueOf(versionSplitted[1].substring(0, 1)));
                }
            }
            try {
                this.id.setBuild(Integer.valueOf(versionSplitted[2]));
            } catch (IndexOutOfBoundsException ignored) {
            }
        }
    }

    @JsonIgnore
    @Transient
    public VersionId getId() {
        return id;
    }

    @JsonIgnore
    @Transient
    public void setId(VersionId id) {
        this.id = id;
    }

    public Integer getMajor() {
        return id.getMajor();
    }

    public void setMajor(Integer major) {
        this.setMajor(major);
    }

    public Integer getMinor() {
        return id.getMinor();
    }

    public void setMinor(Integer minor) {
        this.setMinor(minor);
    }

    public Integer getBuild() {
        return id.getBuild();
    }

    public void setBuild(Integer build) {
        this.setBuild(build);
    }

    @Override
    public String toString() {
        return id.getMajor() + "." + id.getMinor() + "." + id.getBuild();
    }

    @Override
    public Object clone() {
        final Version cloned = new Version();
        cloned.setId(this.getId());
        cloned.setMajor(this.getMajor());
        cloned.setMinor(this.getMinor());
        cloned.setBuild(this.getBuild());
        return cloned;
    }

    public Version nextBuild() {
        Integer build = this.id.getBuild();
        if (build != null) {
            this.id.setBuild(build + 1);
        } else {
            this.id.setBuild(1);
        }
        return this;
    }

    public Version nextMinor() {
        Integer minor = this.id.getMinor();
        if (minor != null) {
            this.id.setMinor(minor + 1);
        } else {
            this.id.setMinor(1);
        }
        return this;
    }

    public Version nextMajor() {
        Integer major = this.id.getMajor();
        if (major != null) {
            this.id.setMajor(major + 1);
        } else {
            this.id.setMajor(1);
        }
        return this;
    }

    @Override
    public int compareTo(Version version) {
        return this.id.compareTo(version.getId());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Version version = (Version) o;

        return id != null ? id.equals(version.id) : version.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public void override(Version source) {
        this.setMajor(source.getMajor());
        this.setMinor(source.getMinor());
        this.setBuild(source.getBuild());
    }
}
