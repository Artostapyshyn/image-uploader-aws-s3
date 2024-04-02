package com.artostapyshyn.image.manager.controller;

import com.artostapyshyn.image.manager.service.ImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

class ImageControllerTest {

    @Mock
    private ImageService imageService;

    @InjectMocks
    private ImageController imageController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void uploadImage_ShouldReturnOkResponse() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());

        ResponseEntity<String> responseEntity = imageController.uploadImage(multipartFile);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Image uploaded successfully!", responseEntity.getBody());
        verify(imageService).uploadFile(multipartFile);
    }

    @Test
    void uploadImage_ShouldReturnInternalServerErrorResponse() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());

        doThrow(IOException.class).when(imageService).uploadFile(multipartFile);

        ResponseEntity<String> responseEntity = imageController.uploadImage(multipartFile);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Failed to upload image", responseEntity.getBody());
    }
}
