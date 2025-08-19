package com.company.jmixdatastore.dto;

import io.jmix.core.metamodel.annotation.JmixEntity;

import java.io.Serializable;
import java.time.OffsetDateTime;

@JmixEntity
public class BucketInfo implements Serializable {
    private String name;
    private OffsetDateTime creationDate;

    public BucketInfo() {
    }

    public BucketInfo(String name, OffsetDateTime creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }

    public String getName() {
        return name;
    }

    public OffsetDateTime getCreationDate() {
        return creationDate;
    }
}


