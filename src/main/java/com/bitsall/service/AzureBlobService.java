package com.bitsall.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Service
public class AzureBlobService {

    private final BlobContainerClient blobContainerClient;

    public AzureBlobService(BlobServiceClientBuilder blobServiceClientBuilder,
                            @Value("${spring.cloud.azure.storage.blob.container-name}") String containerName) {
        BlobServiceClient blobServiceClient = blobServiceClientBuilder.buildClient();
        this.blobContainerClient = blobServiceClient.getBlobContainerClient(containerName);
    }

    public String uploadFile(MultipartFile file, String folderName, String name) throws IOException {
        String blobName = folderName + "/" + UUID.randomUUID().toString() + "-" + name;
        BlobClient blobClient = blobContainerClient.getBlobClient(blobName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);
        return blobClient.getBlobUrl();
    }
}
