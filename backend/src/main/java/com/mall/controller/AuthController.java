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
        String role = body.get("role");
        if (username == null || password == null) {
            return Result.fail("用户名和密码不能为空");
        }
        if (role == null || role.isBlank()) {
            return Result.fail("请选择登录角色");
        }
        try {
            Map<String, Object> data = authService.login(username, password, role);
            return Result.ok(data);
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    @PostMapping("/register")
    public Result<?> register(@RequestBody Map<String, Object> body) {
        String username = body.get("username") == null ? null : body.get("username").toString();
        String password = body.get("password") == null ? null : body.get("password").toString();
        String nickname = body.get("nickname") == null ? null : body.get("nickname").toString();
        String gender = body.get("gender") == null ? null : body.get("gender").toString();
        String email = body.get("email") == null ? null : body.get("email").toString();
        String phone = body.get("phone") == null ? null : body.get("phone").toString();
        String receiverName = body.get("receiverName") == null ? null : body.get("receiverName").toString();
        String receiverPhone = body.get("receiverPhone") == null ? null : body.get("receiverPhone").toString();
        String receiverAddress = body.get("receiverAddress") == null ? null : body.get("receiverAddress").toString();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return Result.fail("用户名和密码不能为空");
        }
        if (nickname == null || nickname.isBlank()) {
            return Result.fail("昵称不能为空");
        }
        try {
            authService.registerUser(
                    username.trim(),
                    password,
                    nickname.trim(),
                    gender,
                    email,
                    phone,
                    receiverName,
                    receiverPhone,
                    receiverAddress
            );
            return Result.ok(Map.of("message", "注册成功"));
        } catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }
}
