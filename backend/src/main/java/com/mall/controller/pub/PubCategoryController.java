package com.mall.controller.pub;

import com.mall.dto.Result;
import com.mall.entity.Category;
import com.mall.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pub")
@RequiredArgsConstructor
/** 公共分类接口：支持按父级查询或拉取全部分类。 */
public class PubCategoryController {

    private final CategoryRepository categoryRepository;

    /** 查询分类列表。all=true 时返回完整分类集合。 */
    @GetMapping("/categories")
    public ResponseEntity<Result<?>> list(
            @RequestParam(required = false) Long parentId,
            @RequestParam(required = false, defaultValue = "false") Boolean all
    ) {
        List<Category> list;
        if (Boolean.TRUE.equals(all)) {
            list = categoryRepository.findAll(Sort.by(Sort.Order.asc("sortOrder"), Sort.Order.asc("id")));
        } else if (parentId == null) {
            list = categoryRepository.findByParentIdIsNullOrderBySortOrderAsc();
        } else {
            list = categoryRepository.findByParentIdOrderBySortOrderAsc(parentId);
        }
        return ResponseEntity.ok(Result.ok(list));
    }
}
