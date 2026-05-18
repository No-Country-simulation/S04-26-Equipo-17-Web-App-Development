package com.northpay.backend.document;

public interface StorageClient {

    String upload(byte[] content, String key, String contentType);

    void delete(String key);
}
