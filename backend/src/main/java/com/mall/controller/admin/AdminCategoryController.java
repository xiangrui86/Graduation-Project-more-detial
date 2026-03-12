package com.mall.controller.admin;

import com.mall.dto.Result;
import com.mall.entity.Category;
import com.mall.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<Result<?>> list() {
        return ResponseEntity.ok(Result.ok(categoryRepository.findAll()));
    }

    @PostMapping
    public ResponseEntity<Result<?>> create(@RequestBody Category category) {
        category.setId(null);
        return ResponseEntity.ok(Result.ok(categoryRepository.save(category)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<?>> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return ResponseEntity.ok(Result.ok(categoryRepository.save(category)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<?>> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(Result.ok(null));
    }
}
