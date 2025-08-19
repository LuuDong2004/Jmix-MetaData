package com.company.jmixdatastore.entity.Airbyte;

import io.jmix.core.entity.annotation.JmixGeneratedValue;
import io.jmix.core.metamodel.annotation.InstanceName;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@JmixEntity
@Table(name = "SOURCE")
@Entity
@DiscriminatorColumn(name = "DTYPE", discriminatorType = DiscriminatorType.STRING)
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorValue("SOURCE")
public class Source {
    @JmixGeneratedValue
    @Column(name = "ID", nullable = false)
    @Id
    private UUID id;

    @Version
    @Column(name = "VERSION", nullable = false)
    private Integer version;

    @InstanceName
    @Column(name = "NAME", nullable = false)
    @NotNull
    private String name;

    @Column(name = "DESCRIPTION", length = 1000)
    private String description;

    @Column(name = "AIRBYTE_SOURCE_ID", length = 100)
    private String airbyteSourceId;

    @Column(name = "RAW_CONFIGURATION")
    @Lob
    private String rawConfiguration;

    @Column(name = "SOURCE_TYPE")
    private String sourceType;

    @Column(name = "CREATE_AT")
    private Long createAt;

    @Column(name = "DEFINITION_ID")
    private String definitionId;

    @Column(name = "WORKSPACE_ID")
    private String workspaceId;

    public SourceType getSourceType() {
        return sourceType == null ? null : SourceType.fromId(sourceType);
    }

    public void setSourceType(SourceType sourceType) {
        this.sourceType = sourceType == null ? null : sourceType.getId();
    }

    public String getWorkspaceId() {
        return workspaceId;
    }

    public void setWorkspaceId(String workspaceId) {
        this.workspaceId = workspaceId;
    }

    public String getDefinitionId() {
        return definitionId;
    }

    public void setDefinitionId(String definitionId) {
        this.definitionId = definitionId;
    }

    public Long getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Long createAt) {
        this.createAt = createAt;
    }

    public String getRawConfiguration() {
        return rawConfiguration;
    }

    public void setRawConfiguration(String configuration) {
        this.rawConfiguration = configuration;
    }

    public String getAirbyteSourceId() {
        return airbyteSourceId;
    }

    public void setAirbyteSourceId(String airbyteSourceId) {
        this.airbyteSourceId = airbyteSourceId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }
}