package vg.edu.pe.HinoPE.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.util.ReflectionTestUtils;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FileStorageServiceTest {
    
    @InjectMocks
    private FileStorageService fileStorageService;
    
    @Mock
    private FilePart filePart;
    
    @TempDir
    Path tempDir;
    
    @BeforeEach
    void setUp() {
        // Set test values using reflection
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
        ReflectionTestUtils.setField(fileStorageService, "maxSizeImages", 10485760L); // 10MB
        ReflectionTestUtils.setField(fileStorageService, "maxSizeDocuments", 20971520L); // 20MB
    }
    
    @Test
    void validateFileType_WithValidImageExtension_ShouldReturnTrue() {
        // Test all valid image extensions
        assertThat(fileStorageService.validateFileType("photo.jpg", "image")).isTrue();
        assertThat(fileStorageService.validateFileType("photo.jpeg", "image")).isTrue();
        assertThat(fileStorageService.validateFileType("photo.png", "image")).isTrue();
        assertThat(fileStorageService.validateFileType("photo.webp", "image")).isTrue();
        assertThat(fileStorageService.validateFileType("PHOTO.JPG", "image")).isTrue(); // Case insensitive
    }
    
    @Test
    void validateFileType_WithInvalidImageExtension_ShouldReturnFalse() {
        assertThat(fileStorageService.validateFileType("document.pdf", "image")).isFalse();
        assertThat(fileStorageService.validateFileType("file.txt", "image")).isFalse();
        assertThat(fileStorageService.validateFileType("video.mp4", "image")).isFalse();
    }
    
    @Test
    void validateFileType_WithValidDocumentExtension_ShouldReturnTrue() {
        // Test all valid document extensions
        assertThat(fileStorageService.validateFileType("document.pdf", "document")).isTrue();
        assertThat(fileStorageService.validateFileType("document.doc", "document")).isTrue();
        assertThat(fileStorageService.validateFileType("document.docx", "document")).isTrue();
        assertThat(fileStorageService.validateFileType("DOCUMENT.PDF", "document")).isTrue(); // Case insensitive
    }
    
    @Test
    void validateFileType_WithInvalidDocumentExtension_ShouldReturnFalse() {
        assertThat(fileStorageService.validateFileType("photo.jpg", "document")).isFalse();
        assertThat(fileStorageService.validateFileType("file.txt", "document")).isFalse();
        assertThat(fileStorageService.validateFileType("spreadsheet.xlsx", "document")).isFalse();
    }
    
    @Test
    void validateFileType_WithUnknownType_ShouldReturnFalse() {
        assertThat(fileStorageService.validateFileType("file.jpg", "unknown")).isFalse();
        assertThat(fileStorageService.validateFileType("file.pdf", "video")).isFalse();
    }
    
    @Test
    void validateFileType_WithNoExtension_ShouldReturnFalse() {
        assertThat(fileStorageService.validateFileType("filename", "image")).isFalse();
        assertThat(fileStorageService.validateFileType("filename", "document")).isFalse();
    }
    
    @Test
    void validateFileSize_ForImage_WithinLimit_ShouldReturnTrue() {
        long size = 5 * 1024 * 1024; // 5MB
        assertThat(fileStorageService.validateFileSize(size, "image")).isTrue();
    }
    
    @Test
    void validateFileSize_ForImage_ExceedsLimit_ShouldReturnFalse() {
        long size = 15 * 1024 * 1024; // 15MB
        assertThat(fileStorageService.validateFileSize(size, "image")).isFalse();
    }
    
    @Test
    void validateFileSize_ForImage_AtLimit_ShouldReturnTrue() {
        long size = 10 * 1024 * 1024; // 10MB exactly
        assertThat(fileStorageService.validateFileSize(size, "image")).isTrue();
    }
    
    @Test
    void validateFileSize_ForDocument_WithinLimit_ShouldReturnTrue() {
        long size = 15 * 1024 * 1024; // 15MB
        assertThat(fileStorageService.validateFileSize(size, "document")).isTrue();
    }
    
    @Test
    void validateFileSize_ForDocument_ExceedsLimit_ShouldReturnFalse() {
        long size = 25 * 1024 * 1024; // 25MB
        assertThat(fileStorageService.validateFileSize(size, "document")).isFalse();
    }
    
    @Test
    void validateFileSize_ForDocument_AtLimit_ShouldReturnTrue() {
        long size = 20 * 1024 * 1024; // 20MB exactly
        assertThat(fileStorageService.validateFileSize(size, "document")).isTrue();
    }
    
    @Test
    void validateFileSize_WithUnknownType_ShouldReturnFalse() {
        long size = 1024; // 1KB
        assertThat(fileStorageService.validateFileSize(size, "unknown")).isFalse();
    }
    
    @Test
    void generateUniqueFilename_ShouldPreserveExtension() {
        String originalFilename = "photo.jpg";
        String uniqueFilename = fileStorageService.generateUniqueFilename(originalFilename);
        
        assertThat(uniqueFilename).endsWith(".jpg");
        assertThat(uniqueFilename).isNotEqualTo(originalFilename);
        assertThat(uniqueFilename).matches("^[a-f0-9\\-]+\\.jpg$"); // UUID pattern
    }
    
    @Test
    void generateUniqueFilename_WithMultipleDots_ShouldUseLastExtension() {
        String originalFilename = "my.file.name.pdf";
        String uniqueFilename = fileStorageService.generateUniqueFilename(originalFilename);
        
        assertThat(uniqueFilename).endsWith(".pdf");
    }
    
    @Test
    void generateUniqueFilename_ShouldGenerateDifferentNames() {
        String originalFilename = "photo.jpg";
        String uniqueFilename1 = fileStorageService.generateUniqueFilename(originalFilename);
        String uniqueFilename2 = fileStorageService.generateUniqueFilename(originalFilename);
        
        assertThat(uniqueFilename1).isNotEqualTo(uniqueFilename2);
    }
    
    @Test
    void storeFile_WithValidImage_ShouldSaveAndReturnUrl() {
        // Arrange
        when(filePart.filename()).thenReturn("photo.jpg");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(fileStorageService.storeFile(filePart, "image"))
                .assertNext(url -> {
                    assertThat(url).startsWith("/uploads/images/");
                    assertThat(url).endsWith(".jpg");
                })
                .verifyComplete();
        
        verify(filePart, times(1)).filename();
        verify(filePart, times(1)).transferTo(any(Path.class));
    }
    
    @Test
    void storeFile_WithValidDocument_ShouldSaveAndReturnUrl() {
        // Arrange
        when(filePart.filename()).thenReturn("document.pdf");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(fileStorageService.storeFile(filePart, "document"))
                .assertNext(url -> {
                    assertThat(url).startsWith("/uploads/documents/");
                    assertThat(url).endsWith(".pdf");
                })
                .verifyComplete();
        
        verify(filePart, times(1)).filename();
        verify(filePart, times(1)).transferTo(any(Path.class));
    }
    
    @Test
    void storeFile_WithInvalidFileType_ShouldReturnError() {
        // Arrange
        when(filePart.filename()).thenReturn("file.txt");
        
        // Act & Assert
        StepVerifier.create(fileStorageService.storeFile(filePart, "image"))
                .expectErrorMatches(throwable -> 
                    throwable instanceof IllegalArgumentException &&
                    throwable.getMessage().contains("Tipo de archivo no permitido"))
                .verify();
        
        verify(filePart, times(1)).filename();
        verify(filePart, never()).transferTo(any(Path.class));
    }
    
    @Test
    void storeFile_WithTransferError_ShouldPropagateError() {
        // Arrange
        when(filePart.filename()).thenReturn("photo.jpg");
        when(filePart.transferTo(any(Path.class)))
                .thenReturn(Mono.error(new RuntimeException("Transfer failed")));
        
        // Act & Assert
        StepVerifier.create(fileStorageService.storeFile(filePart, "image"))
                .expectErrorMatches(throwable -> 
                    throwable instanceof RuntimeException &&
                    throwable.getMessage().contains("Transfer failed"))
                .verify();
        
        verify(filePart, times(1)).filename();
        verify(filePart, times(1)).transferTo(any(Path.class));
    }
    
    @Test
    void storeFile_ShouldCreateDirectoriesIfNotExist() {
        // Arrange
        when(filePart.filename()).thenReturn("photo.png");
        when(filePart.transferTo(any(Path.class))).thenReturn(Mono.empty());
        
        // Act & Assert
        StepVerifier.create(fileStorageService.storeFile(filePart, "image"))
                .assertNext(url -> {
                    assertThat(url).isNotNull();
                    // Verify directory was created
                    Path imagesDir = tempDir.resolve("images");
                    assertThat(imagesDir).exists();
                })
                .verifyComplete();
    }
}
