package com.mall.controller.pub;

import com.mall.dto.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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
public class ImageController {

    @Value("${file.upload.path:src/main/resources/static/images}")
    private String uploadPath;

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
                        imageInfo.put("url", "/api/images/" + fileName);
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
    public Result<?> uploadImage(@RequestParam("file") MultipartFile file) {
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
            && !fileName.endsWith(".png") && !fileName.endsWith(".gif")) {
            return Result.fail("仅支持 jpg, jpeg, png, gif 格式");
        }

        try {
            // 生成唯一文件名
            String timestamp = System.currentTimeMillis() + "";
            String newFileName = timestamp + "_" + originalFilename;
            Path uploadDir = Paths.get(uploadPath);
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
            }

            // 保存文件
            Path filePath = uploadDir.resolve(newFileName);
            Files.write(filePath, file.getBytes());

            Map<String, String> result = new HashMap<>();
            result.put("fileName", newFileName);
            result.put("url", "/api/images/" + newFileName);
            return Result.ok(result);
        } catch (IOException e) {
            return Result.fail("图片上传失败: " + e.getMessage());
        }
    }
}
