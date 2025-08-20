package com.company.jmixdatastore.service.minio;
import com.company.jmixdatastore.dto.ObjectInfo;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;
public interface ObjectStorageService {
    String put(String objectKey, MultipartFile file);
    String put(String objectKey, InputStream stream, long size, String contentType);
    InputStream get(String objectKey);
    void delete(String objectKey);
    boolean exists(String objectKey);
    List<ObjectInfo> list(String prefix);
    Optional<ObjectInfo> stat(String objectKey);

    // Per-bucket variants
    String put(String bucket, String objectKey, MultipartFile file);
    String put(String bucket, String objectKey, InputStream stream, long size, String contentType);
    InputStream get(String bucket, String objectKey);
    void delete(String bucket, String objectKey);
    boolean exists(String bucket, String objectKey);
    List<ObjectInfo> list(String bucket, String prefix);
    Optional<ObjectInfo> stat(String bucket, String objectKey);
}
