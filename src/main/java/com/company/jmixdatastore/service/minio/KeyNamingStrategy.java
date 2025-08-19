package com.company.jmixdatastore.service.minio;

public interface KeyNamingStrategy {
    String buildKey(String tenantId, String userId, String originalFilename);
}
