package com.mall.controller.pub;

import com.mall.dto.Result;
import com.mall.entity.News;
import com.mall.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pub")
@RequiredArgsConstructor
public class PubNewsController {

    private final NewsRepository newsRepository;

    @GetMapping("/news")
    public ResponseEntity<Result<?>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Result.ok(newsRepository.findByPublishedTrueOrderByCreatedAtDesc(PageRequest.of(page, size))));
    }

    @GetMapping("/news/announcements")
    public ResponseEntity<Result<?>> announcements(@RequestParam(defaultValue = "5") int size) {
        List<News> list = newsRepository.findByPublishedTrueAndTypeOrderByCreatedAtDesc("ANNOUNCEMENT", PageRequest.of(0, size));
        return ResponseEntity.ok(Result.ok(list));
    }
}
