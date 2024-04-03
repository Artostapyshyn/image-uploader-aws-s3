package com.artostapyshyn.image.manager.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.artostapyshyn.image.manager.service.impl.ImageServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

class ImageServiceTest {

    @Mock
    private AmazonS3 s3Client;

    @InjectMocks
    private ImageServiceImpl imageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadFile_ShouldUploadFileToS3Bucket() {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());

        imageService.uploadFile(multipartFile);

        verify(s3Client).putObject(any(PutObjectRequest.class));
    }

    @Test
    void convertMultiPartToFile_ShouldConvertMultipartFileToFile() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());

        assertNotNull(imageService.convertMultiPartToFile(multipartFile));
    }

    @Test
    void uploadFileTos3bucket_ShouldUploadFileToS3Bucket() {
        File file = new File("test.jpg");
        String fileName = "test.jpg";

        imageService.uploadFileTos3bucket(fileName, file);

        verify(s3Client).putObject(any(PutObjectRequest.class));
    }
}

