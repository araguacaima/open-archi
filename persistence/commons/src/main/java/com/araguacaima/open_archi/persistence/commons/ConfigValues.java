package com.araguacaima.open_archi.persistence.commons;

import org.apache.commons.collections4.CollectionUtils;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.*;


/**
 * Created by Alejandro on 19/12/2014.
 */
@PersistenceUnit(unitName = "open-archi")
@Entity
@Table(schema = "Config", name = "ConfigValues")
public class ConfigValues implements Serializable {

    @Id
    private String key;

    @ElementCollection
    @CollectionTable(name = "ConfigValuesList", schema = "Config")
    private List<String> values;

    public ConfigValues() {

    }

    public ConfigValues(String key, String... values) {
        this.key = key;
        this.values = Arrays.asList(values);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConfigValues that = (ConfigValues) o;
        return key.equals(that.key) &&
                Objects.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, values);
    }
}
