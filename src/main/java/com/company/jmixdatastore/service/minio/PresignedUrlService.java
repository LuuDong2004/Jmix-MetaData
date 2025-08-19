package com.company.jmixdatastore.service.minio;

public interface PresignedUrlService {
    String presignedGet(String objectKey, int expirySeconds);
    String presignedPut(String objectKey, int expirySeconds);

    String presignedGet(String bucket, String objectKey, int expirySeconds);
    String presignedPut(String bucket, String objectKey, int expirySeconds);
}
