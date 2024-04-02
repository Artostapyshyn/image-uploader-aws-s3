package com.artostapyshyn.image.manager.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ImageService {
    void uploadFile(MultipartFile multipartFile);

    List<String> searchImages(String query);
}
