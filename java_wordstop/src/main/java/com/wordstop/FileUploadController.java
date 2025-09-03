package com.wordstop;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/files")
public class FileUploadController {
    private static final String UPLOAD_DIR = "/tmp/uploads/";

    @PostMapping("/upload")
    public Map<String, String> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> response = new HashMap<>();
        try {
            // Vulnerable: No file type validation
            // Vulnerable: No file size validation
            // Vulnerable: Predictable file location
            String fileName = file.getOriginalFilename();
            String filePath = UPLOAD_DIR + fileName;
            
            // Create upload directory if it doesn't exist
            new File(UPLOAD_DIR).mkdirs();
            
            // Save file
            Files.write(Paths.get(filePath), file.getBytes());
            
            response.put("status", "success");
            response.put("path", filePath);
            response.put("message", "File uploaded successfully");
        } catch (Exception e) {
            response.put("status", "error");
            response.put("error", e.toString());
        }
        return response;
    }

    @GetMapping("/download/{fileName}")
    public byte[] downloadFile(@PathVariable String fileName) throws Exception {
        // Vulnerable: Path traversal
        // Vulnerable: No access control
        String filePath = UPLOAD_DIR + fileName;
        return Files.readAllBytes(Paths.get(filePath));
    }

    @GetMapping("/list")
    public Map<String, Object> listFiles() {
        Map<String, Object> response = new HashMap<>();
        try {
            // Vulnerable: Directory listing
            File dir = new File(UPLOAD_DIR);
            response.put("files", dir.list());
            response.put("directory", UPLOAD_DIR);
        } catch (Exception e) {
            response.put("error", e.toString());
        }
        return response;
    }

    @PostMapping("/execute")
    public Map<String, String> executeFile(@RequestParam String filePath) {
        Map<String, String> response = new HashMap<>();
        try {
            // Vulnerable: Remote Code Execution
            Process process = Runtime.getRuntime().exec(filePath);
            response.put("status", "executed");
            response.put("file", filePath);
        } catch (Exception e) {
            response.put("error", e.toString());
        }
        return response;
    }
}
