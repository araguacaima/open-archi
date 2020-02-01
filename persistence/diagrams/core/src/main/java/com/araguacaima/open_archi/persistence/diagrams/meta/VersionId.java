package com.araguacaima.open_archi.persistence.diagrams.meta;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Embeddable
public class VersionId implements Serializable, Comparable<VersionId>, Cloneable {

    private static final long serialVersionUID = -5350803918802322500L;

    @Column(nullable = false)
    @NotNull
    private Integer major = 0;
    @Column(nullable = false)
    @NotNull
    private Integer minor = 0;
    @Column
    private Integer build = 1;

    public VersionId() {
    }

    public VersionId(@NotNull Integer major, @NotNull Integer minor, Integer build) {
        this.major = major;
        this.minor = minor;
        this.build = build;
    }

    public Integer getMajor() {
        return major;
    }

    public void setMajor(Integer major) {
        this.major = major;
    }

    public Integer getMinor() {
        return minor;
    }

    public void setMinor(Integer minor) {
        this.minor = minor;
    }

    public Integer getBuild() {
        return build;
    }

    public void setBuild(Integer build) {
        this.build = build;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;

        if (o == null || getClass() != o.getClass())
            return false;

        VersionId versionId = (VersionId) o;

        return new EqualsBuilder()
                .append(major, versionId.major)
                .append(minor, versionId.minor)
                .append(build, versionId.build)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(major).append(minor).append(build).toHashCode();
    }

    @Override
    public int compareTo(VersionId other) {
        try {
            if (this.major.equals(other.major)) {
                if (this.minor.equals(other.minor)) {
                    return this.build - other.build;
                } else {
                    return this.minor - other.minor;
                }
            } else {
                return this.major - other.major;
            }
        } catch (Throwable ignored) {
            return -1;
        }
    }

    @Override
    public String toString() {
        return major + "." + minor + "." + build;
    }

}
