package com.araguacaima.open_archi.persistence.diagrams.core;

import com.araguacaima.commons.utils.ReflectionUtils;
import com.araguacaima.open_archi.persistence.commons.Constants;
import com.araguacaima.open_archi.persistence.commons.OperationType;
import com.araguacaima.open_archi.persistence.commons.Utils;
import com.araguacaima.open_archi.persistence.commons.exceptions.EntityError;
import com.araguacaima.open_archi.persistence.diagrams.meta.Version;
import com.araguacaima.open_archi.persistence.diagrams.meta.*;
import com.araguacaima.specification.Specification;
import com.araguacaima.specification.util.SpecificationMap;
import com.araguacaima.specification.util.SpecificationMapBuilder;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;

@MappedSuperclass
@PersistenceUnit(unitName = "open-archi")
@JsonIgnoreProperties(value = {"resourceBundle, reflectionUtils, specificationMapBuilder, serialVersionUID"})
public abstract class BaseEntity implements Serializable, BasicEntity, Cloneable, Storable, Comparable {

    @Transient
    @JsonIgnore
    protected static final ResourceBundle resourceBundle = ResourceBundle.getBundle(Constants.BUNDLE_NAME);
    private static final long serialVersionUID = 5449758397914117108L;
    @Transient
    @JsonIgnore
    private static final Logger log = LoggerFactory.getLogger(BaseEntity.class);
    @Transient
    @JsonIgnore
    private static ReflectionUtils reflectionUtils = ReflectionUtils.getInstance();
    @Transient
    @JsonIgnore
    private static SpecificationMapBuilder specificationMapBuilder = new SpecificationMapBuilder();
    @Transient
    protected String key;

    @Id
    @NotNull
    @Column(name = "Id")
    @org.hibernate.annotations.Type(type = "objectid")
    protected String id;

    @OneToOne(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JsonIgnore
    @Transient
    private MetaInfo meta = new MetaInfo();

    public BaseEntity() {
        this.id = generateId();
    }

    @JsonIgnore
    private String generateId() {
        return UUID.randomUUID().toString();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public void resetId() {
        this.id = generateId();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonIgnore
    @Transient
    public MetaInfo getMeta() {
        return meta;
    }

    public void setMeta(MetaInfo meta) {
        this.meta = meta;
    }

    @JsonIgnore
    protected String getRequestErrorMessageKey() {
        return this.getClass().getName().toLowerCase() + "-" + "request" + "." + "error";
    }

    @JsonIgnore
    protected String getModificationErrorMessageKey() {
        return this.getClass().getName().toLowerCase() + "-" + "modification" + "." + "error";
    }

    @JsonIgnore
    protected String getCreationErrorMessageKey() {
        return this.getClass().getName().toLowerCase() + "-" + "creation" + "." + "error";
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateRequest() throws EntityError {
        return validateRequest(null);
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateCreation() throws EntityError {
        return validateCreation(null);
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateModification() throws EntityError {
        return validateModification(null);
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateReplacement() throws EntityError {
        return validateReplacement(null);
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateAssociation() throws EntityError {
        return validateAssociation(null);
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateRequest(Map<Object, Object> map) throws EntityError {
        //Do nothing. All request are valid on this entity
        return (T) this;
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateCreation(Map<Object, Object> map) throws EntityError {
        if (map == null) map = new HashMap<>();
        map.put(Constants.OPERATION_TYPE, OperationType.CREATION);
        map.put(Constants.INITIATOR, this);
        return traverse(this, "validateCreation", map);
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateModification(Map<Object, Object> map) throws EntityError {
        if (map == null) map = new HashMap<>();
        map.put(Constants.OPERATION_TYPE, OperationType.MODIFICATION);
        map.put(Constants.INITIATOR, this);
        return traverse(this, "validateModification", map);
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateReplacement(Map<Object, Object> map) throws EntityError {
        if (map == null) map = new HashMap<>();
        map.put(Constants.OPERATION_TYPE, OperationType.REPLACEMENT);
        map.put(Constants.INITIATOR, this);
        return traverse(this, "validateReplacement", map);
    }

    @Override
    @JsonIgnore
    public <T extends BaseEntity> T validateAssociation(Map<Object, Object> map) throws EntityError {
        if (map == null) map = new HashMap<>();
        map.put(Constants.OPERATION_TYPE, OperationType.REPLACEMENT);
        map.put(Constants.INITIATOR, this);
        return traverse(this, "validateAsociation", map);
    }

    @JsonIgnore
    private <T> T traverse(final Object entity, String method, Map<Object, Object> map) {
        Class<?> clazz = entity.getClass();
        ReflectionUtils.doWithFields(clazz, field -> {
            field.setAccessible(true);
            Object object_ = field.get(entity);
            Class<?> clazz_ = field.getType();
            if (reflectionUtils.isCollectionImplementation(clazz_) && object_ != null) {
                Collection<Object> collection = (Collection<Object>) object_;
                Collection<Object> remove = new ArrayList<>();
                Collection<Object> add = new ArrayList<>();
                if (!collection.isEmpty()) {
                    for (Object innerCollection : collection) {
                        Object foundObject = traverse(innerCollection, method, map);
                        remove.add(innerCollection);
                        add.add(foundObject);
                    }
                    collection.clear();
                    collection.addAll(add);
                }
            } else if (reflectionUtils.isMapImplementation(clazz_) && object_ != null) {
                Map<Object, Object> map_ = (Map<Object, Object>) object_;
                Set<Map.Entry<Object, Object>> set = map_.entrySet();
                if (!map_.isEmpty()) {
                    for (Map.Entry innerMapValues : set) {
                        traverse(innerMapValues.getValue(), method, map);
                    }
                    Map<Object, Object> stroredReplacements = (Map<Object, Object>) map.get(Constants.SPECIFICATION_STORED_REPLACEMENTS);
                    if (stroredReplacements != null) {
                        stroredReplacements.forEach((key, value) -> {
                            for (Map.Entry<Object, Object> innerMapValues : set) {
                                Object key1 = innerMapValues.getKey();
                                map_.put(key1, value);
                            }
                        });
                    }
                }
            } else {
                if (object_ != null) {
                    if (BaseEntity.class.isAssignableFrom(clazz)) {
                        Object foundObject = processSpecification(method, object_, clazz_, map);
                        field.set(entity, foundObject);
                    } else {
                        log.warn("No changes");
                    }
                }
            }
        }, Utils::filterMethod);
        Object mainObject = entity;
        return processSpecification(method, mainObject, clazz, map);
    }

    @JsonIgnore
    private <T> T processSpecification(String method, Object object_, Class<?> clazz_, Map<Object, Object> map) {
        try {
            if (reflectionUtils.getFullyQualifiedJavaTypeOrNull(clazz_) == null) {
                List<SpecificationMap> specificationMaps = specificationMapBuilder.getInstances(clazz_, true);
                Boolean specificationResult = null;
                for (SpecificationMap specificationMap : specificationMaps) {
                    Specification specification = specificationMap.getSpecificationFromMethod(method);
                    if (specification != null) {
                        boolean satisfiedBy = specification.isSatisfiedBy(object_, map);
                        Map<Object, Object> stroredReplacements = (Map<Object, Object>) map.get(Constants.SPECIFICATION_STORED_REPLACEMENTS);
                        if (stroredReplacements != null) {
                            Object storedObject = stroredReplacements.get(object_);
                            if (storedObject != null) {
                                object_ = storedObject;
                            }
                        }
                        Boolean break_ = (Boolean) map.get(Constants.BREAK_REMAINING_SPECIFICATIONS);
                        if (BooleanUtils.isNotTrue(break_)) {
                            specificationResult = (specificationResult == null ? Boolean.TRUE : specificationResult) && satisfiedBy;
                        } else {
                            specificationResult = satisfiedBy;
                            map.remove(Constants.BREAK_REMAINING_SPECIFICATIONS);
                            break;
                        }
                    }
                }
                if (specificationResult != null && specificationResult.equals(Boolean.FALSE)) {
                    throw EntityErrorFactory.build(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new EntityError(e.getMessage(), e);
        }
        Map<Object, Object> replacedEntities = (Map<Object, Object>) map.get(Constants.SPECIFICATION_REPLACED_ENTITIES);
        if (replacedEntities != null) {
            Object replacedEntity = replacedEntities.get(object_);
            if (replacedEntity != null) {
                if (replacedEntity.getClass().equals(object_.getClass())) {
                    return (T) replacedEntity;
                }
            }
        }
        return (T) object_;
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
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom) {
        override(source, keepMeta, suffix, clonedFrom, null);
    }

    @Override
    public void override(BaseEntity source, boolean keepMeta, String suffix, CompositeElement clonedFrom, Comparator comparator) {
        if (source.getMeta() != null) {
            if (keepMeta) {
                this.meta = source.getMeta();
            } else {
                this.meta = buildDefaultMeta();
            }
        } else {
            if (!keepMeta) {
                this.meta = null;
            }
        }
        if (StringUtils.isNotBlank(source.getId())) {
            this.key = source.getId();
            this.id = source.getId();
        }
        this.key = source.getKey();
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
        if (source.getMeta() != null) {
            if (keepMeta) {
                this.meta = source.getMeta();
            } else {
                if (source.getMeta() != null) {
                    this.meta = buildDefaultMeta();
                }
            }
        }
    }

    @JsonIgnore
    private MetaInfo buildDefaultMeta() {
        MetaInfo meta = new MetaInfo();
        Date time = Calendar.getInstance().getTime();
        meta.setCreated(time);
        History history = new History(time);
        history.setVersion(new Version());
        meta.addHistory(history);
        return meta;
    }

    @Override
    public abstract int compareTo(Object o);
}