package org.example.is_lab1.services;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Service
public class MinioStorageService {

    private final MinioClient minioClient;
    private final String bucket;

    public MinioStorageService(
            MinioClient minioClient,
            @Value("${minio.bucket}") String bucket
    ) {
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    @PostConstruct
    public void ensureBucketExists() {
        try {
            boolean exists = minioClient.bucketExists(
                    BucketExistsArgs.builder()
                            .bucket(bucket)
                            .build()
            );

            if (!exists) {
                minioClient.makeBucket(
                        MakeBucketArgs.builder()
                                .bucket(bucket)
                                .build()
                );
            }

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Failed to ensure MinIO bucket exists: " + bucket, e);
        }
    }

    /**
     * Phase 1 (PREPARE)
     * Загружаем файл во временную область
     */
    public String uploadTemp(MultipartFile file, String importId) {
        try {
            String objectKey =
                    "tmp/" + importId + "/" + file.getOriginalFilename();

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(
                                    file.getInputStream(),
                                    file.getSize(),
                                    -1
                            )
                            .contentType(file.getContentType())
                            .build()
            );

            return objectKey;

        } catch (Exception e) {
            throw new RuntimeException("Failed to upload temp file to MinIO", e);
        }
    }

    /**
     * Phase 2 (COMMIT)
     * Переводим файл из TEMP в FINAL
     */
    public String promoteToFinal(String tempKey) {
        try {
            String finalKey = tempKey.replaceFirst("tmp/", "final/");

            // copy tmp → final
            minioClient.copyObject(
                    CopyObjectArgs.builder()
                            .bucket(bucket)
                            .object(finalKey)
                            .source(
                                    CopySource.builder()
                                            .bucket(bucket)
                                            .object(tempKey)
                                            .build()
                            )
                            .build()
            );

            // delete tmp
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(tempKey)
                            .build()
            );

            return finalKey;

        } catch (Exception e) {
            throw new RuntimeException("Failed to promote file to final", e);
        }
    }

    /**
     * COMPENSATING ACTION (ROLLBACK)
     */
    public void delete(String key) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
        } catch (Exception e) {
            // логируем, но не падаем
            System.err.println("Failed to delete object " + key + ": " + e.getMessage());
        }
    }

    public InputStream download(String objectKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            throw new RuntimeException("Failed to download file from MinIO: " + e.getMessage());
        }
    }
}
