package org.example.stashroom.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
@Slf4j
public class FileStorageService {

    public List<String> saveImages(List<MultipartFile> images) {
        if (images == null || images.isEmpty()) return List.of();

        List<String> urls = new ArrayList<>();
        Path uploadPath = Paths.get("uploads");

        try {
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
        } catch (IOException e) {
            log.error("Could not create upload directory", e);
            return List.of();
        }

        for (MultipartFile image : images) {
            try {
                String filename = UUID.randomUUID() + "_" + image.getOriginalFilename();
                Path filePath = uploadPath.resolve(filename);
                Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

                String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                        .path("/uploads/")
                        .path(filename)
                        .toUriString();

                urls.add(fileUrl);
            } catch (IOException e) {
                log.error("Failed to save image", e);
            }
        }

        return urls;
    }
}
