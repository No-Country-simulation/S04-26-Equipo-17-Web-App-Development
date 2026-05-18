package com.northpay.backend.document;

import com.northpay.backend.common.config.StorageConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Component
@RequiredArgsConstructor
public class SupabaseStorageClient implements StorageClient {

    private final S3Client s3Client;
    private final StorageConfig storageConfig;

    @Override
    public String upload(byte[] content, String key, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(storageConfig.bucket())
                .key(key)
                .contentType(contentType)
                .build();
        s3Client.putObject(request, RequestBody.fromBytes(content));
        return storageConfig.publicUrlPrefix() + key;
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(storageConfig.bucket())
                .key(key)
                .build();
        s3Client.deleteObject(request);
    }
}
