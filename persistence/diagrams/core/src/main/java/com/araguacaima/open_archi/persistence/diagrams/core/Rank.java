package com.araguacaima.open_archi.persistence.diagrams.core;


import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Comparator;
import java.util.Set;
import java.util.UUID;
import java.util.function.ToIntFunction;

@Entity
@PersistenceUnit(unitName = "open-archi")
@Table(name = "Rank", schema = "Diagrams")
@DynamicUpdate
public class Rank extends BaseEntity {

    @Column(nullable = false)
    @NotNull
    private String parentId;

    @Column
    private Integer position;

    public Rank() {
        super();
    }

    public Rank(String parentId, Integer position) {
        this();
        this.parentId = parentId;
        this.position = position;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        super.override(source, keepMeta, suffix, clonedFrom, comparator);
        Rank source1 = (Rank) source;
        this.id = source1.getId();
        this.parentId = source1.getParentId();
        this.position = source1.getPosition();
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, null);
    }

    @Override
    public void copyNonEmpty(BaseEntity source) {
        copyNonEmpty(source, false);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta) {
        copyNonEmpty(source, keepMeta, null);
    }

    @Override
    public void copyNonEmpty(BaseEntity source, boolean keepMeta, Comparator comparator) {
        super.copyNonEmpty(source, keepMeta, comparator);
        Rank source1 = (Rank) source;
        if (StringUtils.isNotBlank(source1.getId())) {
            this.id = source1.getId();
        }
        String parentId = source1.getParentId();
        if (parentId != null) {
            this.parentId = parentId;
        }
        Integer position = source1.getPosition();
        if (position != null) {
            this.position = position;
        }
    }

    @Override
    public int compareTo(Object o) {
        if (o == null || !Rank.class.isAssignableFrom(o.getClass())) {
            return -1;
        }
        Rank t = (Rank) o;
        if (o == null) {
            return -1;
        }
        if (this.parentId == null) {
            return 1;
        }
        String parentId_ = t.getParentId();
        if (parentId_ == null) {
            return -1;
        }
        return 0;
    }

    public static Integer getNextRank(Set<Rank> ranks) {
        Integer max = 1;
        if (ranks != null) {
            for (Rank rank : ranks) {
                Integer position = rank.getPosition();
                if (position > max) {
                    max = position;
                }
            }
            return max + 1;
        } else {
            return max;
        }
    }

    public static Integer getMaxRank(Set<Rank> ranks) {
        Integer max = 0;
        if (ranks != null) {
            for (Rank rank : ranks) {
                Integer position = rank.getPosition();
                if (position > max) {
                    max = position;
                }
            }
        }
        return max;
    }

    public static ToIntFunction<? super Taggable> compareFunc(Taggable o1) {

        return (ToIntFunction<Taggable>) value -> {
            int pos = 0;
            Set<Rank> ranks = value.getRanks();
            Set<Rank> ranks_ = o1.getRanks();
            if (ranks == null && ranks_ == null) {
                return 0;
            } else if (ranks == null || ranks_.isEmpty()) {
                Rank rank_ = IterableUtils.find(ranks_, rank -> {
                    try {
                        return value.id.equals(rank.getParentId());
                    } catch (Throwable ignored) {
                        return false;
                    }
                });
                if (rank_ != null) {
                    pos = rank_.getPosition();
                }
            } else if (ranks.isEmpty()) {
                Rank rank = IterableUtils.find(ranks, rank_ -> {
                    try {
                        return value.id.equals(rank_.getParentId());
                    } catch (Throwable ignored) {
                        return false;
                    }
                });
                if (rank != null) {
                    pos = rank.getPosition();
                }
            } else {
                Integer position = 0;
                Rank rank = IterableUtils.find(ranks, rank_ -> {
                    try {
                        return value.id.equals(rank_.getParentId());
                    } catch (Throwable ignored) {
                        return false;
                    }
                });
                if (rank != null) {
                    position = rank.getPosition();
                }

                Integer position_ = 0;
                Rank rank_ = IterableUtils.find(ranks_, rank__ -> {
                    try {
                        return value.id.equals(rank__.getParentId());
                    } catch (Throwable ignored) {
                        return false;
                    }
                });
                if (rank_ != null) {
                    position_ = rank_.getPosition();
                }
                pos = Math.max(position, position_);
            }
            return pos;
        };

    }

    ;
}
