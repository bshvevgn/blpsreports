package com.eg.blpsreports.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Base64;

@Component
public class ImageUtils {
    public String encodeImageToBase64(String path) {
        try {
            ClassPathResource resource = new ClassPathResource(path);
            byte[] bytes = resource.getInputStream().readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить изображение по пути: " + path, e);
        }
    }
}
