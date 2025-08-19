package com.company.jmixdatastore.entity.Airbyte;

import io.jmix.core.metamodel.datatype.EnumClass;
import org.springframework.lang.Nullable;


public enum FileProvider implements EnumClass<String> {

    HTTPS("https"),
    SFTP("sftp");

    private final String id;

    FileProvider(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static FileProvider fromId(String id) {
        for (FileProvider at : FileProvider.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}