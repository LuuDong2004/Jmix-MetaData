package com.company.jmixdatastore.entity.Airbyte;

import com.company.jmixdatastore.entity.DBType;
import com.github.javaparser.quality.Nullable;
import io.jmix.core.metamodel.datatype.EnumClass;


public enum SourceType implements EnumClass<String> {
    API("api"),
    FILE("file"),
    DATABASE("database");

    private final String id;

    SourceType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }


    public static SourceType fromId(String id) {
        if (DBType.fromId(id) != null) {
            return DATABASE;
        }

        if (id.isEmpty() || id == null) {
            return API;
        }

        for (SourceType at : SourceType.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
