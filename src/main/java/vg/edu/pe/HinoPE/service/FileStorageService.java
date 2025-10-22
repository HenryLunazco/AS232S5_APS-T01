package vg.edu.pe.HinoPE.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileStorageService {
    
    @Value("${file.upload.dir}")
    private String uploadDir;
    
    @Value("${file.upload.max-size-images}")
    private long maxSizeImages;
    
    @Value("${file.upload.max-size-documents}")
    private long maxSizeDocuments;
    
    private static final List<String> ALLOWED_IMAGE_TYPES = Arrays.asList("jpg", "jpeg", "png", "webp");
    private static final List<String> ALLOWED_DOCUMENT_TYPES = Arrays.asList("pdf", "doc", "docx");
    
    /**
     * Store file in the file system
     */
    public Mono<String> storeFile(FilePart filePart, String type) {
        log.debug("Storing file: {} of type: {}", filePart.filename(), type);
        
        return Mono.fromCallable(() -> {
            // Validate file type
            String originalFilename = filePart.filename();
            if (!validateFileType(originalFilename, type)) {
                throw new IllegalArgumentException("Tipo de archivo no permitido");
            }
            
            // Generate unique filename
            String uniqueFilename = generateUniqueFilename(originalFilename);
            
            // Determine subdirectory based on type
            String subDir = "image".equalsIgnoreCase(type) ? "images" : "documents";
            Path uploadPath = Paths.get(uploadDir, subDir);
            
            // Create directories if they don't exist
            try {
                Files.createDirectories(uploadPath);
            } catch (IOException e) {
                log.error("Error creating upload directory", e);
                throw new RuntimeException("Error al crear directorio de carga", e);
            }
            
            // Save file
            Path filePath = uploadPath.resolve(uniqueFilename);
            return filePath;
        })
        .flatMap(filePath -> 
            filePart.transferTo(filePath)
                .then(Mono.just("/uploads/" + 
                    ("image".equalsIgnoreCase(type) ? "images/" : "documents/") + 
                    filePath.getFileName().toString()))
        )
        .doOnSuccess(url -> log.info("File stored successfully: {}", url))
        .doOnError(error -> log.error("Error storing file", error));
    }
    
    /**
     * Validate file type based on extension
     */
    public boolean validateFileType(String filename, String type) {
        String extension = getFileExtension(filename).toLowerCase();
        
        if ("image".equalsIgnoreCase(type)) {
            return ALLOWED_IMAGE_TYPES.contains(extension);
        } else if ("document".equalsIgnoreCase(type)) {
            return ALLOWED_DOCUMENT_TYPES.contains(extension);
        }
        
        return false;
    }
    
    /**
     * Validate file size
     */
    public boolean validateFileSize(long size, String type) {
        if ("image".equalsIgnoreCase(type)) {
            return size <= maxSizeImages;
        } else if ("document".equalsIgnoreCase(type)) {
            return size <= maxSizeDocuments;
        }
        return false;
    }
    
    /**
     * Generate unique filename to avoid collisions
     */
    public String generateUniqueFilename(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + extension;
    }
    
    /**
     * Get file extension from filename
     */
    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex > 0 && lastDotIndex < filename.length() - 1) {
            return filename.substring(lastDotIndex + 1);
        }
        return "";
    }
}
