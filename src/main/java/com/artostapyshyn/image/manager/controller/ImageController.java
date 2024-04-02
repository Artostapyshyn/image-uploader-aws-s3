package com.artostapyshyn.image.manager.controller;

import com.artostapyshyn.image.manager.service.ImageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("api/v1/images")
@AllArgsConstructor
public class ImageController {

    private final ImageService imageService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        imageService.uploadFile(file);
        return ResponseEntity.ok("Image uploaded successfully!");
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> searchImages(@RequestParam("query") String query) {
        List<String> imageUrls = imageService.searchImages(query);
        return ResponseEntity.ok(imageUrls);
    }
}
