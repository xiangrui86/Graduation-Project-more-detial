package com.mall.controller.admin;

import com.mall.dto.Result;
import com.mall.entity.News;
import com.mall.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/news")
@RequiredArgsConstructor
public class AdminNewsController {

    private final NewsRepository newsRepository;

    @GetMapping
    public ResponseEntity<Result<?>> list() {
        return ResponseEntity.ok(Result.ok(newsRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<Result<?>> create(@RequestBody News news) {
        news.setId(null);
        return ResponseEntity.ok(Result.ok(newsRepository.save(news)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<?>> update(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        return ResponseEntity.ok(Result.ok(newsRepository.save(news)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<?>> delete(@PathVariable Long id) {
        newsRepository.deleteById(id);
        return ResponseEntity.ok(Result.ok(null));
    }
}
