package com.mall.controller.admin;

import com.mall.common.Role;
import com.mall.dto.Result;
import com.mall.entity.User;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public ResponseEntity<Result<?>> list(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        if (role != null) {
            return ResponseEntity.ok(Result.ok(userRepository.findByRole(role)));
        }
        return ResponseEntity.ok(Result.ok(userRepository.findAll(PageRequest.of(page, size))));
    }

    @PostMapping
    public ResponseEntity<Result<?>> create(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String roleStr = (String) body.get("role");
        Long merchantId = body.containsKey("merchantId") ? Long.valueOf(body.get("merchantId").toString()) : null;
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.ok(Result.fail("用户名已存在"));
        }
        Role role = Role.valueOf(roleStr);
        User u = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname((String) body.get("nickname"))
                .role(role)
                .merchantId(merchantId)
                .enabled(true)
                .build();
        userRepository.save(u);
        return ResponseEntity.ok(Result.ok(u));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Result<?>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.ok(Result.fail("用户不存在"));
        User u = opt.get();
        if (body.containsKey("nickname")) u.setNickname((String) body.get("nickname"));
        if (body.containsKey("enabled")) u.setEnabled((Boolean) body.get("enabled"));
        if (body.containsKey("merchantId")) u.setMerchantId(body.get("merchantId") != null ? Long.valueOf(body.get("merchantId").toString()) : null);
        userRepository.save(u);
        return ResponseEntity.ok(Result.ok(u));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result<?>> delete(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.ok(Result.ok(null));
    }
}
