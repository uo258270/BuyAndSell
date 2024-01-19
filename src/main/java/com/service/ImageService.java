package com.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ImageService {
    
    @Value("${image.upload.path}")
    private String uploadPath;

    public List<String> saveImages(List<MultipartFile> images) {
        List<String> imagePaths = new ArrayList<>();

        for (MultipartFile image : images) {   
            String imagePath = saveImageToFileSystem(image);
            
            imagePaths.add(imagePath);
        }

        return imagePaths;
    }

    private String saveImageToFileSystem(MultipartFile image) {
        String fileName = UUID.randomUUID().toString() + "-" + image.getOriginalFilename();
        String filePath = Paths.get(uploadPath, fileName).toString();

        try {
            Files.write(Paths.get(filePath), image.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return fileName;
    }
}