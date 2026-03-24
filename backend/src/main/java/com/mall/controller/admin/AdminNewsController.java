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
/** 管理端资讯接口：资讯/公告的增删改查。 */
public class AdminNewsController {

    private final NewsRepository newsRepository;

    /** 查询资讯列表。 */
    @GetMapping
    public ResponseEntity<Result<?>> list() {
        return ResponseEntity.ok(Result.ok(newsRepository.findAll()));
    }

    /** 创建资讯。 */
    @PostMapping
    public ResponseEntity<Result<?>> create(@RequestBody News news) {
        news.setId(null);
        return ResponseEntity.ok(Result.ok(newsRepository.save(news)));
    }

    /** 更新资讯。 */
    @PutMapping("/{id}")
    public ResponseEntity<Result<?>> update(@PathVariable Long id, @RequestBody News news) {
        news.setId(id);
        return ResponseEntity.ok(Result.ok(newsRepository.save(news)));
    }

    /** 删除资讯。 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<?>> delete(@PathVariable Long id) {
        newsRepository.deleteById(id);
        return ResponseEntity.ok(Result.ok(null));
    }
}
