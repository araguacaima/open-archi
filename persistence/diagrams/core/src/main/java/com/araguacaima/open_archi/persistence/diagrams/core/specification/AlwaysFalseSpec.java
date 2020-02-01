package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class AlwaysFalseSpec extends AbstractSpecification {

    public AlwaysFalseSpec() {
        this(false);
    }

    public AlwaysFalseSpec(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        return false;
    }

    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }
}
