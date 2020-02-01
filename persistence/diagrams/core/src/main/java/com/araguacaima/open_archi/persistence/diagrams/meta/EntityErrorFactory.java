package com.araguacaima.open_archi.persistence.diagrams.meta;

import com.araguacaima.open_archi.persistence.commons.Constants;
import com.araguacaima.open_archi.persistence.commons.OperationType;
import com.araguacaima.open_archi.persistence.commons.exceptions.*;

import java.util.Map;

public class EntityErrorFactory {
    public static EntityError build(Map map) {
        if (map == null) {
            return new EntityError(null);
        }
        OperationType operationType = (OperationType) map.get("OperationType");

        Object error = null;
        if (operationType.equals(OperationType.CREATION)) {
            error = map.get(Constants.SPECIFICATION_ERROR_ALREADY_EXISTS);
        }
        if (error == null) {
            error = map.get(Constants.SPECIFICATION_ERROR_CREATION);
        }
        if (error != null) {
            return new EntityCreationError(error.toString());
        } else {
            error = map.get(Constants.SPECIFICATION_ERROR_MODIFICATION);
            if (error != null) {
                return new EntityModificationError(error.toString());
            } else {
                error = map.get(Constants.SPECIFICATION_ERROR_DELETION);
                if (error != null) {
                    return new EntityDeletionError(error.toString());
                } else {
                    error = map.get(Constants.SPECIFICATION_ERROR_REPLACEMENT);
                    if (error != null) {
                        return new EntityReplacementError(error.toString());
                    }
                }
            }
        }
        return new EntityError(null);
    }
}
