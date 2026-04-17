package com.mall.controller.admin;

import com.mall.dto.Result;
import com.mall.entity.Category;
import com.mall.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/category")
@RequiredArgsConstructor
/** 管理端分类接口：分类的增删改查。 */
public class AdminCategoryController {

    private final CategoryRepository categoryRepository;

    /** 获取分类列表。 */
    @GetMapping
    public ResponseEntity<Result<?>> list() {
        return ResponseEntity.ok(Result.ok(categoryRepository.findAll(Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("id")))));
    }

    /** 创建分类。 */
    @PostMapping
    public ResponseEntity<Result<?>> create(@RequestBody Category category) {
        try {
            category.setId(null);
            validateCategory(category, null);
            return ResponseEntity.ok(Result.ok(categoryRepository.save(category)));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    /** 更新分类。 */
    @PutMapping("/{id}")
    public ResponseEntity<Result<?>> update(@PathVariable Long id, @RequestBody Category category) {
        try {
            category.setId(id);
            validateCategory(category, id);
            return ResponseEntity.ok(Result.ok(categoryRepository.save(category)));
        } catch (RuntimeException e) {
            return ResponseEntity.ok(Result.fail(e.getMessage()));
        }
    }

    /** 删除分类。 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<?>> delete(@PathVariable Long id) {
        if (!categoryRepository.existsById(id)) {
            return ResponseEntity.ok(Result.fail("分类不存在"));
        }
        if (categoryRepository.existsByParentId(id)) {
            return ResponseEntity.ok(Result.fail("请先删除或调整子分类"));
        }
        categoryRepository.deleteById(id);
        return ResponseEntity.ok(Result.ok(null));
    }

    private void validateCategory(Category category, Long currentId) {
        if (category.getName() == null || category.getName().trim().isEmpty()) {
            throw new RuntimeException("分类名称不能为空");
        }
        Long parentId = category.getParentId();
        if (currentId != null && parentId != null && parentId.equals(currentId)) {
            throw new RuntimeException("父级分类不能选择自己");
        }
        if (parentId != null) {
            Optional<Category> parentOpt = categoryRepository.findById(parentId);
            if (parentOpt.isEmpty()) {
                throw new RuntimeException("父级分类不存在");
            }
            if (currentId != null) {
                Long ancestorId = parentId;
                while (ancestorId != null) {
                    if (ancestorId.equals(currentId)) {
                        throw new RuntimeException("父级分类不能是当前分类的子级");
                    }
                    Optional<Category> ancestorOpt = categoryRepository.findById(ancestorId);
                    if (ancestorOpt.isEmpty()) break;
                    ancestorId = ancestorOpt.get().getParentId();
                }
            }
        }
        Optional<Category> existing = categoryRepository.findByNameAndParentId(category.getName(), parentId);
        if (existing.isPresent() && (currentId == null || !existing.get().getId().equals(currentId))) {
            throw new RuntimeException("同级分类名称已存在");
        }
    }
}
