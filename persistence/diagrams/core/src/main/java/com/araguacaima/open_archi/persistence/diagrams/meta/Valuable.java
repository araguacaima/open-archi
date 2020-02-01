package com.araguacaima.open_archi.persistence.diagrams.meta;

import com.araguacaima.open_archi.persistence.commons.exceptions.EntityError;
import com.araguacaima.open_archi.persistence.diagrams.core.BaseEntity;
import com.araguacaima.open_archi.persistence.diagrams.core.ClonableAndOverridable;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Map;

public interface Valuable extends ClonableAndOverridable {

    @JsonIgnore
    <T extends BaseEntity> T validateRequest(Map<Object, Object> map) throws EntityError;

    @JsonIgnore
    <T extends BaseEntity> T validateCreation(Map<Object, Object> map) throws EntityError;

    @JsonIgnore
    <T extends BaseEntity> T validateModification(Map<Object, Object> map) throws EntityError;

    @JsonIgnore
    <T extends BaseEntity> T validateReplacement(Map<Object, Object> map) throws EntityError;

    @JsonIgnore
    <T extends BaseEntity> T validateAsociation(Map<Object, Object> map) throws EntityError;

    @JsonIgnore
    <T extends BaseEntity> T validateRequest() throws EntityError;

    @JsonIgnore
    <T extends BaseEntity> T validateCreation() throws EntityError;

    @JsonIgnore
    <T extends BaseEntity> T validateModification() throws EntityError;

    @JsonIgnore
    <T extends BaseEntity> T validateReplacement() throws EntityError;

    @JsonIgnore
    <T extends BaseEntity> T validateAsociation() throws EntityError;
}
