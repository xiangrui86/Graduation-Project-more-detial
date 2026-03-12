package com.mall.controller.admin;

import com.mall.dto.Result;
import com.mall.entity.Merchant;
import com.mall.repository.MerchantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/admin/merchant")
@RequiredArgsConstructor
public class AdminMerchantController {

    private final MerchantRepository merchantRepository;

    @GetMapping
    public Result<?> list() {
        return Result.ok(merchantRepository.findAll());
    }

    @PostMapping
    public Result<?> create(@RequestBody Merchant merchant) {
        merchant.setId(null);
        return Result.ok(merchantRepository.save(merchant));
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Merchant merchant) {
        merchant.setId(id);
        return Result.ok(merchantRepository.save(merchant));
    }

    @GetMapping("/{id}")
    public Result<?> get(@PathVariable Long id) {
        Optional<Merchant> m = merchantRepository.findById(id);
        return m.map(merchant -> Result.ok(merchant))
                .orElse(Result.fail("商家不存在"));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        merchantRepository.deleteById(id);
        return Result.ok(null);
    }
}
