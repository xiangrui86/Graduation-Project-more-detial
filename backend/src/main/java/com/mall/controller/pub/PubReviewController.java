package com.mall.controller.pub;

import com.mall.dto.Result;
import com.mall.repository.ProductReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pub")
@RequiredArgsConstructor
public class PubReviewController {

    private final ProductReviewRepository productReviewRepository;

    @GetMapping("/reviews")
    public ResponseEntity<Result<?>> list(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(Result.ok(
                productReviewRepository.findByProductIdOrderByCreatedAtDesc(productId, PageRequest.of(page, size))));
    }
}
