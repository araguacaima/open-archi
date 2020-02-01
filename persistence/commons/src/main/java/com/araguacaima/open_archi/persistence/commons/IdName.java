package com.araguacaima.open_archi.persistence.commons;

public class IdName<T extends Comparable<T>> implements Comparable<IdName> {

    private String id;
    private String name;
    private Class clazz;
    private T kind;

    public IdName(String id, String name, Class clazz) {
        this.id = id;
        this.name = name;
        this.clazz = clazz;
    }

    public IdName(String id, String name, Class clazz, T kind) {
        this(id, name, clazz);
        this.kind = kind;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public T getKind() {
        return kind;
    }

    public void setKind(T kind) {
        this.kind = kind;
    }

    @Override
    public int compareTo(IdName idName) {
        if (idName == null) return -1;
        if (clazz.getName().compareTo(idName.clazz.getName()) >= 0) {
            if (kind.compareTo((T) idName.getKind()) >= 0) {
                return name.compareTo(idName.getName());
            } else {
                return kind.compareTo((T) idName.getKind());
            }
        } else {
            return clazz.getName().compareTo(idName.clazz.getName());
        }
    }
}
