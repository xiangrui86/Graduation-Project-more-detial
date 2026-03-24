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
/** 管理端分类接口：分类的增删改查。 */
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    /** 获取分类列表。 */
    @GetMapping
    public ResponseEntity<Result<?>> list() {
        return ResponseEntity.ok(Result.ok(categoryRepository.findAll()));
    }

    /** 创建分类。 */
    @PostMapping
    public ResponseEntity<Result<?>> create(@RequestBody Category category) {
        category.setId(null);
        return ResponseEntity.ok(Result.ok(categoryRepository.save(category)));
    }

    /** 更新分类。 */
    @PutMapping("/{id}")
    public ResponseEntity<Result<?>> update(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        return ResponseEntity.ok(Result.ok(categoryRepository.save(category)));
    }

    /** 删除分类。 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<?>> delete(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(Result.ok(null));
    }
}
