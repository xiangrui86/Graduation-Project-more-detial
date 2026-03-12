package com.mall.controller;

import com.mall.dto.Result;
import com.mall.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public Result<?> login(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        if (username == null || password == null) {
            return Result.fail("用户名和密码不能为空");
        }
        try {
            Map<String, Object> data = authService.login(username, password);
            return Result.ok(data);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, String> body) {
        String username = body.get("username");
        String password = body.get("password");
        String nickname = body.get("nickname");
        if (username == null || password == null) {
            return Result.fail("用户名和密码不能为空");
        }
        try {
            authService.registerUser(username, password, nickname);
            return Result.ok(Map.of("message", "注册成功"));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
