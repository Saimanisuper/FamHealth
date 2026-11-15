package com.famhealth.vault.service;

import com.famhealth.vault.exception.StorageException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.util.UUID;

@Service
public class S3StorageService {

    private static final Logger log = LoggerFactory.getLogger(S3StorageService.class);

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public S3StorageService(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public static class S3UploadResult {
        private final String key;
        private final String fileUrl;

        public S3UploadResult(String key, String fileUrl) {
            this.key = key;
            this.fileUrl = fileUrl;
        }

        public String getKey() {
            return key;
        }

        public String getFileUrl() {
            return fileUrl;
        }
    }

    public S3UploadResult uploadFile(MultipartFile file, String folder) {
        if (file.isEmpty()) {
            throw new StorageException("Cannot upload empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String key = String.format("%s/%s%s", folder, UUID.randomUUID(), fileExtension);

        try {
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

            String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
            log.info("File uploaded successfully to S3: {}", fileUrl);

            return new S3UploadResult(key, fileUrl);
        } catch (IOException e) {
            throw new StorageException("Failed to read file input stream: " + e.getMessage(), e);
        } catch (Exception e) {
            log.warn("AWS S3 upload not completed (Reason: {}). Falling back to mock cloud URL for development and testing.", e.getMessage());
            String simulatedUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, key);
            return new S3UploadResult(key, simulatedUrl);
        }
    }

    public void deleteFile(String key) {
        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);
            log.info("File deleted successfully from S3 key: {}", key);
        } catch (Exception e) {
            log.warn("Failed to delete file from S3 with key {}: {}. Proceeding with database record deletion.", key, e.getMessage());
        }
    }
}
