package com.company.jmixdatastore.entity;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JmixEntity
public class TableGroup {
    @JmixGeneratedValue
    private UUID id;

    @InstanceName
    private String name;

    private String description;
    private List<String> tables;
    private String domain;

    public TableGroup() {
        this.tables = new ArrayList<>();
    }

    public TableGroup(String name, String domain) {
        this();
        this.name = name;
        this.domain = domain;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getTables() {
        return tables;
    }

    public void setTables(List<String> tables) {
        this.tables = tables;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void addTable(String tableName) {
        if (!this.tables.contains(tableName)) {
            this.tables.add(tableName);
        }
    }
}
