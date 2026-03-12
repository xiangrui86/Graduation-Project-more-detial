package com.mall.controller.pub;

import com.mall.dto.Result;
import com.mall.entity.Category;
import com.mall.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pub")
@RequiredArgsConstructor
public class PubCategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping("/categories")
    public ResponseEntity<Result<?>> list(@RequestParam(required = false) Long parentId) {
        List<Category> list = parentId == null
                ? categoryRepository.findByParentIdIsNullOrderBySortOrderAsc()
                : categoryRepository.findByParentIdOrderBySortOrderAsc(parentId);
        return ResponseEntity.ok(Result.ok(list));
    }
}
