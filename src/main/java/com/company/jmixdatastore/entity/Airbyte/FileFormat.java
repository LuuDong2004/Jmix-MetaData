package com.company.jmixdatastore.entity.Airbyte;
import io.jmix.core.metamodel.datatype.EnumClass;

import org.springframework.lang.Nullable;


public enum FileFormat implements EnumClass<String> {

    CSV("csv"),
    JSON("json");

    private final String id;

    FileFormat(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static FileFormat fromId(String id) {
        for (FileFormat at : FileFormat.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
