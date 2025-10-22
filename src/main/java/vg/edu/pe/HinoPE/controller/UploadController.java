package vg.edu.pe.HinoPE.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import vg.edu.pe.HinoPE.model.dto.ApiResponse;
import vg.edu.pe.HinoPE.service.FileStorageService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
@Slf4j
@io.swagger.v3.oas.annotations.tags.Tag(name = "Archivos", description = "Carga de archivos (imágenes y documentos)")
public class UploadController {
    
    private final FileStorageService fileStorageService;
    
    @PostMapping
    public Mono<ApiResponse<Map<String, String>>> uploadFile(
            @RequestPart("file") FilePart filePart,
            @RequestParam(required = false, defaultValue = "image") String type) {
        
        log.info("POST /api/upload - Uploading file: {} of type: {}", filePart.filename(), type);
        
        return fileStorageService.storeFile(filePart, type)
                .map(url -> {
                    Map<String, String> data = new HashMap<>();
                    data.put("url", url);
                    data.put("filename", filePart.filename());
                    return ApiResponse.success("Archivo subido exitosamente", data);
                })
                .onErrorResume(e -> {
                    log.error("Error uploading file", e);
                    return Mono.just(ApiResponse.error(e.getMessage()));
                });
    }
}
