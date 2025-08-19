package com.company.jmixdatastore.service.minio.impl;
import com.company.jmixdatastore.config.MinioStorageProperties;
import com.company.jmixdatastore.dto.ObjectInfo;
import com.company.jmixdatastore.exception.StorageException;
import com.company.jmixdatastore.service.minio.ObjectStorageService;
import io.minio.GetObjectArgs;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.ListBucketsArgs;
import io.minio.RemoveBucketArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.messages.Item;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class MinioObjectStorageService implements ObjectStorageService {

    private final MinioClient mc;
    private final MinioStorageProperties props;

    public MinioObjectStorageService(MinioClient mc, MinioStorageProperties props) {
        this.mc = mc;
        this.props = props;
        ensureBucketExists();
    }

    @Override
    public String put(String objectKey, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            mc.putObject(
                    PutObjectArgs.builder()
                            .bucket(props.bucket())
                            .object(objectKey)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return objectKey;
        } catch (Exception e) {
            throw new StorageException("Upload failed: " + objectKey, e);
        }
    }

    @Override
    public String put(String objectKey, InputStream stream, long size, String contentType) {
        try (stream) {
            mc.putObject(
                    PutObjectArgs.builder()
                            .bucket(props.bucket())
                            .object(objectKey)
                            .stream(stream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            return objectKey;
        } catch (Exception e) {
            throw new StorageException("Upload failed: " + objectKey, e);
        }
    }

    @Override
    public InputStream get(String objectKey) {
        try {
            return mc.getObject(
                    GetObjectArgs.builder()
                            .bucket(props.bucket())
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Download failed: " + objectKey, e);
        }
    }

    @Override
    public void delete(String objectKey) {
        try {
            mc.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(props.bucket())
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Delete failed: " + objectKey, e);
        }
    }

    @Override
    public boolean exists(String objectKey) {
        try {
            mc.statObject(StatObjectArgs.builder()
                    .bucket(props.bucket())
                    .object(objectKey)
                    .build());
            return true;
        } catch (ErrorResponseException ere) {
            // NoSuchKey => không tồn tại
            return "NoSuchKey".equalsIgnoreCase(ere.errorResponse().code()) ? false
                    : throwWrap("Stat failed: " + objectKey, ere);
        } catch (Exception e) {
            throw new StorageException("Stat failed: " + objectKey, e);
        }
    }

    @Override
    public List<ObjectInfo> list(String prefix) {
        try {
            Iterable<Result<Item>> it = mc.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(props.bucket())
                            .prefix(prefix == null ? "" : prefix)
                            .recursive(true)
                            .build()
            );

            List<ObjectInfo> out = new ArrayList<>();
            for (Result<Item> r : it) {
                Item item = r.get(); // có thể ném exception, để trong try/catch nếu muốn bỏ qua lỗi từng object
                out.add(new ObjectInfo(
                        item.objectName(),
                        item.size(),
                        null,
                        item.lastModified() == null ? null : item.lastModified().toOffsetDateTime()
                ));
            }
            return out;
        } catch (Exception e) {
            throw new StorageException("List failed for prefix=" + prefix, e);
        }
    }

    @Override
    public Optional<ObjectInfo> stat(String objectKey) {
        try {
            var stat = mc.statObject(
                    StatObjectArgs.builder()
                            .bucket(props.bucket())
                            .object(objectKey)
                            .build()
            );
            return Optional.of(new ObjectInfo(
                    objectKey,
                    stat.size(),
                    stat.contentType(),
                    stat.lastModified() == null ? null : stat.lastModified().toOffsetDateTime()
            ));
        } catch (ErrorResponseException ere) {
            if ("NoSuchKey".equalsIgnoreCase(ere.errorResponse().code())) return Optional.empty();
            throw new StorageException("Stat failed: " + objectKey, ere);
        } catch (Exception e) {
            throw new StorageException("Stat failed: " + objectKey, e);
        }
    }

    // Per-bucket variants
    @Override
    public String put(String bucket, String objectKey, MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            mc.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(is, file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );
            return objectKey;
        } catch (Exception e) {
            throw new StorageException("Upload failed: " + objectKey + " in bucket=" + bucket, e);
        }
    }

    @Override
    public String put(String bucket, String objectKey, InputStream stream, long size, String contentType) {
        try (stream) {
            mc.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(stream, size, -1)
                            .contentType(contentType)
                            .build()
            );
            return objectKey;
        } catch (Exception e) {
            throw new StorageException("Upload failed: " + objectKey + " in bucket=" + bucket, e);
        }
    }

    @Override
    public InputStream get(String bucket, String objectKey) {
        try {
            return mc.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Download failed: " + objectKey + " in bucket=" + bucket, e);
        }
    }

    @Override
    public void delete(String bucket, String objectKey) {
        try {
            mc.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new StorageException("Delete failed: " + objectKey + " in bucket=" + bucket, e);
        }
    }

    @Override
    public boolean exists(String bucket, String objectKey) {
        try {
            mc.statObject(StatObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .build());
            return true;
        } catch (ErrorResponseException ere) {
            return "NoSuchKey".equalsIgnoreCase(ere.errorResponse().code()) ? false
                    : throwWrap("Stat failed: " + objectKey + " in bucket=" + bucket, ere);
        } catch (Exception e) {
            throw new StorageException("Stat failed: " + objectKey + " in bucket=" + bucket, e);
        }
    }

    @Override
    public List<ObjectInfo> list(String bucket, String prefix) {
        try {
            Iterable<Result<Item>> it = mc.listObjects(
                    ListObjectsArgs.builder()
                            .bucket(bucket)
                            .prefix(prefix == null ? "" : prefix)
                            .recursive(true)
                            .build()
            );
            List<ObjectInfo> out = new ArrayList<>();
            for (Result<Item> r : it) {
                Item item = r.get();
                out.add(new ObjectInfo(
                        item.objectName(),
                        item.size(),
                        null,
                        item.lastModified() == null ? null : item.lastModified().toOffsetDateTime()
                ));
            }
            return out;
        } catch (Exception e) {
            throw new StorageException("List failed for bucket=" + bucket + ", prefix=" + prefix, e);
        }
    }

    @Override
    public Optional<ObjectInfo> stat(String bucket, String objectKey) {
        try {
            var stat = mc.statObject(
                    StatObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
            return Optional.of(new ObjectInfo(
                    objectKey,
                    stat.size(),
                    stat.contentType(),
                    stat.lastModified() == null ? null : stat.lastModified().toOffsetDateTime()
            ));
        } catch (ErrorResponseException ere) {
            if ("NoSuchKey".equalsIgnoreCase(ere.errorResponse().code())) return Optional.empty();
            throw new StorageException("Stat failed: " + objectKey + " in bucket=" + bucket, ere);
        } catch (Exception e) {
            throw new StorageException("Stat failed: " + objectKey + " in bucket=" + bucket, e);
        }
    }

    // thủ thuật nhỏ để ném lại lỗi checked
    private boolean throwWrap(String msg, Exception e) {
        throw new StorageException(msg, e);
    }

    private void ensureBucketExists() {
        try {
            boolean exists = mc.bucketExists(
                    BucketExistsArgs.builder().bucket(props.bucket()).build()
            );
            if (!exists) {
                mc.makeBucket(MakeBucketArgs.builder().bucket(props.bucket()).build());
            }
        } catch (Exception e) {
            throw new StorageException("Bucket check/create failed: " + props.bucket(), e);
        }
    }

    // Bucket management helpers (used by bucket management view/service)
    public java.util.List<com.company.jmixdatastore.dto.BucketInfo> listBuckets() {
        try {
            var buckets = mc.listBuckets(ListBucketsArgs.builder().build());
            java.util.List<com.company.jmixdatastore.dto.BucketInfo> out = new java.util.ArrayList<>();
            for (var b : buckets) {
                out.add(new com.company.jmixdatastore.dto.BucketInfo(
                        b.name(),
                        b.creationDate() == null ? null : b.creationDate().toOffsetDateTime()
                ));
            }
            return out;
        } catch (Exception e) {
            throw new StorageException("List buckets failed", e);
        }
    }

    public void createBucket(String bucketName) {
        try {
            boolean exists = mc.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!exists) {
                mc.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }
        } catch (Exception e) {
            throw new StorageException("Create bucket failed: " + bucketName, e);
        }
    }

    public void deleteBucket(String bucketName) {
        try {
            mc.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build());
        } catch (Exception e) {
            throw new StorageException("Delete bucket failed: " + bucketName, e);
        }
    }
}
