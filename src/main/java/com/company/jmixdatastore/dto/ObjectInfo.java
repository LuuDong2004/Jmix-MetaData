package com.company.jmixdatastore.dto;

import io.jmix.core.metamodel.annotation.JmixEntity;

import java.io.Serializable;
import java.time.OffsetDateTime;

@JmixEntity
public class ObjectInfo implements Serializable {
	private String key;
	private Long size;
	private String contentType;
	private OffsetDateTime lastModified;

	public ObjectInfo() {
	}

	public ObjectInfo(String key, Long size, String contentType, OffsetDateTime lastModified) {
		this.key = key;
		this.size = size;
		this.contentType = contentType;
		this.lastModified = lastModified;
	}

	public String getKey() {
		return key;
	}

	public Long getSize() {
		return size;
	}

	public String getContentType() {
		return contentType;
	}

	public OffsetDateTime getLastModified() {
		return lastModified;
	}
}
