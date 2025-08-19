package com.company.jmixdatastore.service.minio.impl;



import com.company.jmixdatastore.config.MinioStorageProperties;
import com.company.jmixdatastore.exception.StorageException;
import com.company.jmixdatastore.service.minio.PresignedUrlService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.http.Method;
import org.springframework.stereotype.Service;


@Service
public class MinioPresignedUrlService implements PresignedUrlService {

    private final MinioClient mc;
    private final MinioStorageProperties props;

    public MinioPresignedUrlService(MinioClient mc, MinioStorageProperties props) {
        this.mc = mc;
        this.props = props;
    }

    @Override
    public String presignedGet(String objectKey, int expirySeconds) {
        try {
            return mc.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(props.bucket())
                            .object(objectKey)
                            .expiry(expirySeconds > 0 ? expirySeconds : props.presignExpirySeconds())
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Presigned GET failed: " + objectKey, e);
        }
    }

    @Override
    public String presignedPut(String objectKey, int expirySeconds) {
        try {
            return mc.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(props.bucket())
                            .object(objectKey)
                            .expiry(expirySeconds > 0 ? expirySeconds : props.presignExpirySeconds())
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Presigned PUT failed: " + objectKey, e);
        }
    }

    @Override
    public String presignedGet(String bucket, String objectKey, int expirySeconds) {
        try {
            return mc.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(expirySeconds > 0 ? expirySeconds : props.presignExpirySeconds())
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Presigned GET failed: " + objectKey + " in bucket=" + bucket, e);
        }
    }

    @Override
    public String presignedPut(String bucket, String objectKey, int expirySeconds) {
        try {
            return mc.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.PUT)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(expirySeconds > 0 ? expirySeconds : props.presignExpirySeconds())
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Presigned PUT failed: " + objectKey + " in bucket=" + bucket, e);
        }
    }
}