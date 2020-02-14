package com.araguacaima.open_archi.persistence.diagrams.core.specification;

import com.araguacaima.open_archi.persistence.diagrams.core.Item;
import com.araguacaima.open_archi.persistence.diagrams.core.MetaData;
import com.araguacaima.open_archi.persistence.diagrams.meta.MetaInfo;
import com.araguacaima.open_archi.persistence.diagrams.persons.Person;
import com.araguacaima.open_archi.persistence.diagrams.persons.Responsible;
import com.araguacaima.specification.AbstractSpecification;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class FixGrantedResponsible extends AbstractSpecification {

    public FixGrantedResponsible() {
        this(false);
    }

    public FixGrantedResponsible(boolean evaluateAllTerms) {
        super(evaluateAllTerms);
    }

    @Override
    public boolean isSatisfiedBy(Object object, Map<Object, Object> map) {
        Class<?> clazz = object.getClass();
        if (Item.class.isAssignableFrom(clazz)) {
            Item entity = (Item) object;

            MetaData metdata = entity.getMetaData();

            if (metdata == null) {
                metdata = new MetaData();
            }

            MetaInfo meta = entity.getMeta();
            Responsible responsible = new Responsible();
            Person person = new Person();
            person.setMeta(meta);
            responsible.setPerson(person);

            metdata.getGrantedResponsibles().add(responsible);
        }
        return true;
    }

    @Override
    public Collection<Object> getTerms() {
        return new ArrayList<>();
    }

}
