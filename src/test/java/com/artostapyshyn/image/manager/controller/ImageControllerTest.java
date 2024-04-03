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
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

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
    void uploadImage_ShouldReturnOkResponse() {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());

        ResponseEntity<String> responseEntity = imageController.uploadImage(multipartFile);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("Image uploaded successfully!", responseEntity.getBody());
        verify(imageService).uploadFile(multipartFile);
    }

    @Test
    void uploadImage_ShouldReturnInternalServerErrorResponse() {
        MultipartFile multipartFile = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test image".getBytes());

        doThrow(IOException.class).when(imageService).uploadFile(multipartFile);

        ResponseEntity<String> responseEntity = imageController.uploadImage(multipartFile);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        assertEquals("Failed to upload image", responseEntity.getBody());
    }

    @Test
    void searchImages_ShouldReturnListOfImageUrls() {
        String query = "cat";
        List<String> expectedImageUrls = List.of("https://example.com/image1.jpg", "https://example.com/image2.jpg");

        when(imageService.searchImages(query)).thenReturn(expectedImageUrls);

        ResponseEntity<List<String>> responseEntity = imageController.searchImages(query);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedImageUrls, responseEntity.getBody());
        verify(imageService).searchImages(query);
    }

    @Test
    void searchImages_ShouldReturnEmptyListWhenNoMatchFound() {
        String query = "dog";
        List<String> expectedImageUrls = Collections.emptyList();

        when(imageService.searchImages(query)).thenReturn(expectedImageUrls);

        ResponseEntity<List<String>> responseEntity = imageController.searchImages(query);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedImageUrls, responseEntity.getBody());
        verify(imageService).searchImages(query);
    }

    @Test
    void searchImages_ShouldReturnInternalServerErrorResponse() {
        String query = "cat";

        when(imageService.searchImages(query)).thenThrow(RuntimeException.class);

        ResponseEntity<List<String>> responseEntity = imageController.searchImages(query);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseEntity.getStatusCode());
        verify(imageService).searchImages(query);
    }
}
