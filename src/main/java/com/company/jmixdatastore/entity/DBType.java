package com.company.jmixdatastore.entity;


import io.jmix.core.metamodel.datatype.EnumClass;
import org.springframework.lang.Nullable;


public enum DBType implements EnumClass<String> {

    POSTGRES("P"),
    SQLSERVER("S"),
    MYSQL("M");

    private final String id;

    DBType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static DBType fromId(String id) {
        for (DBType at : DBType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}