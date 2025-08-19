package com.company.jmixdatastore.entity.Airbyte;
import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;

@JmixEntity
@Entity
@PrimaryKeyJoinColumn(name = "ID")
@Table(name = "API_SOURCE")
public class ApiSource extends Source {
    @Column(name = "DECLARATIVE_MANIFEST_JSON")
    @Lob
    private String declarativeManifestJson;

    @Column(name = "DECLARATIVE_DEFINITION_ID")
    @Lob
    private String declarativeDefinitionId;

    @Column(name = "BASE_URL")
    @Lob
    private String baseUrl;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDeclarativeDefinitionId() {
        return declarativeDefinitionId;
    }

    public void setDeclarativeDefinitionId(String declarativeDefinitionId) {
        this.declarativeDefinitionId = declarativeDefinitionId;
    }

    public String getDeclarativeManifestJson() {
        return declarativeManifestJson;
    }

    public void setDeclarativeManifestJson(String declarativeManifestJson) {
        this.declarativeManifestJson = declarativeManifestJson;
    }

}
