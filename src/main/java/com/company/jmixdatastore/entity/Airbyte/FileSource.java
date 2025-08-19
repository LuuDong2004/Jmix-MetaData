package com.company.jmixdatastore.entity.Airbyte;

import io.jmix.core.metamodel.annotation.JmixEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

@DiscriminatorValue("FILE")
@JmixEntity
@Entity
@PrimaryKeyJoinColumn(name = "ID")
@Table(name = "FILE_SOURCE")
public class FileSource extends Source {

    @NotNull
    @Column(name = "DATASET_NAME", nullable = false)
    private String datasetName;

    @NotNull
    @Column(name = "FILE_FORMAT", nullable = false)
    private String format;

    @Column(name = "FILE_PROVIDER", nullable = false)
    @NotNull
    private String provider;

    @NotNull
    @Column(name = "URL", nullable = false)
    private String url;

    public FileProvider getProvider() {
        return provider == null ? null : FileProvider.fromId(provider);
    }

    public void setProvider(FileProvider fileProvider) {
        this.provider = fileProvider == null ? null : fileProvider.getId();
    }

    public FileFormat getFormat() {
        return format == null ? null : FileFormat.fromId(format);
    }

    public void setFormat(FileFormat fileFormat) {
        this.format = fileFormat == null ? null : fileFormat.getId();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

}
