package org.sumire.studyhardprogram.service;

import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Path;
import java.util.stream.Stream;

public interface FileStorageService {
    void init();
    String store(MultipartFile file, String directory);
    Stream<Path> loadAll();
    Path load(String filename);
    void deleteAll();
} 