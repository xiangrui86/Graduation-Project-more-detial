package com.mall.controller.user;

import com.mall.dto.Result;
import com.mall.entity.User;
import com.mall.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user/profile")
@RequiredArgsConstructor
/** 用户资料接口：查询当前登录用户信息。 */
public class UserProfileController {

    private final UserService userService;

    /** 获取当前登录用户资料。 */
    @GetMapping
    public Result<?> get(Authentication auth) {
        Long userId = (Long) auth.getPrincipal();
        return userService.getById(userId)
                .map(u -> Result.ok(u))
                .orElse(Result.fail("用户不存在"));
    }
}
