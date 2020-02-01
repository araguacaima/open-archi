package com.araguacaima.open_archi.persistence.commons;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Objects;


/**
 * Created by Alejandro on 19/12/2014.
 */
@PersistenceUnit(unitName = "open-archi")
@Entity
@Table(schema = "Config", name = "Config")
@NamedQueries(value = {@NamedQuery(name = Config.FIND_ALL, query = "select c from Config c")})
public class Config implements Serializable {

    public static final String FIND_ALL = "config.find.all";
    @Id
    private String key;

    @Column(nullable = false, length = 4000)
    @NotNull
    private String value;

    public Config() {

    }

    public Config(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Config config = (Config) o;
        return key.equals(config.key) &&
                value.equals(config.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }
}
