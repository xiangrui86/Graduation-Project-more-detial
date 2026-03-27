package com.mall.controller.user;

import com.mall.dto.Result;
import com.mall.entity.Address;
import com.mall.entity.User;
import com.mall.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/address")
@RequiredArgsConstructor
public class UserAddressController {
    private final AddressService addressService;

    @GetMapping
    public Result<List<Address>> getAddresses(@AuthenticationPrincipal User user) {
        List<Address> addresses = addressService.getUserAddresses(user);
        return Result.ok(addresses);
    }

    @GetMapping("/{id}")
    public Result<Address> getAddress(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Address address = addressService.getAddressById(id, user);
        if (address == null) {
            return Result.fail("地址不存在");
        }
        return Result.ok(address);
    }

    @PostMapping
    public Result<Address> createAddress(@RequestBody Address address, @AuthenticationPrincipal User user) {
        Address created = addressService.createAddress(user, address);
        return Result.ok(created);
    }

    @PutMapping("/{id}")
    public Result<Address> updateAddress(@PathVariable Long id, @RequestBody Address address, @AuthenticationPrincipal User user) {
        Address updated = addressService.updateAddress(id, user, address);
        if (updated == null) {
            return Result.fail("地址不存在");
        }
        return Result.ok(updated);
    }

    @DeleteMapping("/{id}")
    public Result<Void> deleteAddress(@PathVariable Long id, @AuthenticationPrincipal User user) {
        addressService.deleteAddress(id, user);
        return Result.ok(null);
    }

    @PutMapping("/{id}/default")
    public Result<Address> setDefaultAddress(@PathVariable Long id, @AuthenticationPrincipal User user) {
        Address address = addressService.setDefaultAddress(id, user);
        if (address == null) {
            return Result.fail("地址不存在");
        }
        return Result.ok(address);
    }

    @GetMapping("/default")
    public Result<Address> getDefaultAddress(@AuthenticationPrincipal User user) {
        Address address = addressService.getDefaultAddress(user);
        if (address == null) {
            return Result.fail("未设置默认地址");
        }
        return Result.ok(address);
    }
}
