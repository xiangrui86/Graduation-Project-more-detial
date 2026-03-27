package com.mall.controller.pub;

import com.mall.dto.Result;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/pub/images")
@RequiredArgsConstructor
/** 公共图片接口：图片列表查询与图片上传。 */
public class ImageController {

    @Value("${file.upload.path:src/main/resources/static/images}")
    private String uploadPath;

    @Value("${server.port:8080}")
    private int serverPort;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    /**
     * 获取图片文件（通过API端点）
     */
    @GetMapping("/view/{fileName}")
    public ResponseEntity<?> getImage(@PathVariable String fileName) {
        try {
            // 文件名安全校验
            if (fileName.contains("..") || fileName.contains("/")) {
                return ResponseEntity.status(400).body("Invalid file name");
            }
            
            Path filePath = Paths.get(uploadPath).resolve(fileName);
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            byte[] imageBytes = Files.readAllBytes(filePath);
            
            // 根据文件后缀名判断Media Type
            String mediaTypeStr = "image/jpeg";
            if (fileName.endsWith(".png")) {
                mediaTypeStr = "image/png";
            } else if (fileName.endsWith(".gif")) {
                mediaTypeStr = "image/gif";
            } else if (fileName.endsWith(".webp")) {
                mediaTypeStr = "image/webp";
            }
            
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(mediaTypeStr))
                    .body(imageBytes);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("获取图片失败: " + e.getMessage());
        }
    }

    /**
     * 列出所有可用的图片
     */
    @GetMapping("/list")
    public Result<?> listImages() {
        try {
            Path imagesDir = Paths.get(uploadPath);
            if (!Files.exists(imagesDir)) {
                return Result.ok(new ArrayList<>());
            }

            List<Map<String, String>> images = new ArrayList<>();
            Files.list(imagesDir)
                    .filter(path -> {
                        String fileName = path.getFileName().toString().toLowerCase();
                        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") 
                            || fileName.endsWith(".png") || fileName.endsWith(".gif");
                    })
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        Map<String, String> imageInfo = new HashMap<>();
                        imageInfo.put("name", fileName);
                        imageInfo.put("url", "/images/" + fileName);
                        images.add(imageInfo);
                    });

            // 按字母顺序排序
            images.sort((a, b) -> a.get("name").compareTo(b.get("name")));
            return Result.ok(images);
        } catch (IOException e) {
            return Result.fail("获取图片列表失败: " + e.getMessage());
        }
    }

    /**
     * 上传商品图片
     */
    @PostMapping("/upload")
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        if (file.isEmpty()) {
            return Result.fail("请选择图片");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return Result.fail("文件名无效");
        }

        // 验证文件类型
        String fileName = originalFilename.toLowerCase();
        if (!fileName.endsWith(".jpg") && !fileName.endsWith(".jpeg")
            && !fileName.endsWith(".png") && !fileName.endsWith(".gif")
            && !fileName.endsWith(".webp") && !fileName.endsWith(".bmp")) {
            return Result.fail("仅支持 jpg, jpeg, png, gif, webp, bmp 格式");
        }

        try {
            // 生成唯一文件名（替换空格和特殊字符）
            String timestamp = System.currentTimeMillis() + "";
            String cleanFileName = originalFilename.replaceAll("[^a-zA-Z0-9._-]", "_");
            String newFileName = timestamp + "_" + cleanFileName;
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(newFileName);
            Files.write(filePath, file.getBytes());

            // 构建完整的访问URL
            String scheme = request.getScheme();
            String host = request.getServerName();
            int port = request.getServerPort();
            String baseUrl = scheme + "://" + host + ":" + port + "/api";

            Map<String, String> result = new HashMap<>();
            result.put("fileName", newFileName);
            result.put("url", baseUrl + "/pub/images/view/" + newFileName);
            return Result.ok(result);
        } catch (IOException e) {
            return Result.fail("图片上传失败: " + e.getMessage());
        }
    }
}
