package com.tfg.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.service.ImageService;

@ExtendWith(MockitoExtension.class)
public class ImageServiceTest {

	
	@InjectMocks
    private ImageService imageService;

	private String uploadPath = "/path/to/upload";

   
    
    @BeforeEach
	public void init() {
		MockitoAnnotations.openMocks(this);
		imageService.uploadPath = uploadPath;
	}
    
    @Test
    void testSaveImages() {
       
        List<MultipartFile> mockImages = new ArrayList<>();
        MockMultipartFile image1 = new MockMultipartFile("image", "image1.jpg", "image/jpeg", "content1".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("image", "image2.jpg", "image/jpeg", "content2".getBytes());
        mockImages.add(image1);
        mockImages.add(image2);

        List<String> savedImagePaths = imageService.saveImages(mockImages);

       
        assertEquals(2, savedImagePaths.size());
       
        for (String imagePath : savedImagePaths) {
            Path expectedPath = Paths.get(uploadPath, imagePath);
            assertTrue(Files.exists(expectedPath));
        }
    }
    
    @Test
    void testDeleteImages() throws IOException {
    	 ImageService imageService = new ImageService();
         List<String> imageNames = List.of("image1.jpg", "image2.jpg");

         ImageService imageServiceSpy = spy(imageService);
         doNothing().when(imageServiceSpy).deleteImageFromFileSystem(any());

         imageServiceSpy.deleteImages(imageNames);

         for (String imageName : imageNames) {
             verify(imageServiceSpy).deleteImageFromFileSystem(imageName);
         }
    }
    
    

    @Test
    void testDeleteImageFromFileSystem_Success() throws IOException {
       
        String imageName = "image1.jpg";
        Files.createFile(Paths.get(uploadPath, imageName)); 
        imageService.deleteImageFromFileSystem(imageName);

       
        assertFalse(Files.exists(Paths.get(uploadPath, imageName)));
    }

    @Test
    void testDeleteImageFromFileSystem_FileNotFound() {
      
        String imageName = "non_existent_image.jpg";

        assertThrows(IOException.class, () -> imageService.deleteImageFromFileSystem(imageName));
    }

   

}
