package org.example.is_lab1.services;

import io.minio.*;
import jakarta.annotation.PostConstruct;
import org.example.is_lab1.models.entity.ImportFile;
import org.example.is_lab1.models.entity.ImportStatus;
import org.example.is_lab1.repository.ImportFileRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

@Service
public class MinioStorageService {

    private static final Logger log = LoggerFactory.getLogger(MinioStorageService.class);
    private final MinioClient minioClient;
    private final String bucket;
    private final ImportFileRepository importFileRepository;
    private final ImportStatusService importStatusService;

    public MinioStorageService(
            MinioClient minioClient,
            @Value("${minio.bucket}") String bucket,
            ImportFileRepository importFileRepository, ImportStatusService importStatusService) {
        this.minioClient = minioClient;
        this.bucket = bucket;
        this.importFileRepository = importFileRepository;
        this.importStatusService = importStatusService;
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


    public String upload(MultipartFile file, String importId) {
        try {
            String objectKey =
                    "final/" + importId + "/" + file.getOriginalFilename();

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

//    /**
//     * Phase 2 (COMMIT)
//     * Переводим файл из TEMP в FINAL
//     */
//    public String promoteToFinal(String tempKey) {
//        try {
//            String finalKey = tempKey.replaceFirst("tmp/", "final/");
//
//            // copy tmp → final
//            minioClient.copyObject(
//                    CopyObjectArgs.builder()
//                            .bucket(bucket)
//                            .object(finalKey)
//                            .source(
//                                    CopySource.builder()
//                                            .bucket(bucket)
//                                            .object(tempKey)
//                                            .build()
//                            )
//                            .build()
//            );
//
//            // delete tmp
//            minioClient.removeObject(
//                    RemoveObjectArgs.builder()
//                            .bucket(bucket)
//                            .object(tempKey)
//                            .build()
//            );
//
//            return finalKey;
//
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to promote file to final", e);
//        }
//    }


    public void delete(String key, ImportFile file) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(key)
                            .build()
            );
            check();
        } catch (Exception e) {
//            file.setStatus(ImportStatus.REMOVE_REQUIRES);
//            importFileRepository.save(file);
            importStatusService.markRemoveRequired(file);
            System.err.println("Failed to delete object " + key + ": " + e.getMessage());
        }
    }

    public int check(){
        List<ImportFile> list = importFileRepository.findAllByStatus(ImportStatus.REMOVE_REQUIRES);
        int count = 0;
        for(ImportFile file : list){
            try {
                minioClient.removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(bucket)
                                .object(file.getMinioKey())
                                .build()
                );
                count++;
            }catch(Exception e){
                log.warn("Can not delete now");
            }
        }
        return count;
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
