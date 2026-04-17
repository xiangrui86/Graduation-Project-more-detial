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
/** 管理端用户接口：用户查询、创建、更新与删除。 */
public class AdminUserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** 分页查询用户，支持按角色过滤。 */
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

    /** 创建用户账号（密码加密后保存）。 */
    @PostMapping
    public ResponseEntity<Result<?>> create(@RequestBody Map<String, Object> body) {
        String username = (String) body.get("username");
        String password = (String) body.get("password");
        String roleStr = (String) body.get("role");
        Long merchantId = body.containsKey("merchantId") ? Long.valueOf(body.get("merchantId").toString()) : null;
        if (userRepository.existsByUsername(username)) {
            return ResponseEntity.ok(Result.fail("用户名已存在"));
        }
        if (roleStr == null || roleStr.isBlank()) {
            return ResponseEntity.ok(Result.fail("请选择用户角色"));
        }
        Role role = Role.valueOf(roleStr);
        if (role == Role.ADMIN) {
            return ResponseEntity.ok(Result.fail("不能新增管理员账号"));
        }
        User u = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname((String) body.get("nickname"))
                .phone((String) body.get("phone"))
                .role(role)
                .merchantId(merchantId)
                .enabled(true)
                .build();
        userRepository.save(u);
        return ResponseEntity.ok(Result.ok(u));
    }

    /** 更新用户基础信息（昵称、状态、商家绑定）。 */
    @PutMapping("/{id}")
    public ResponseEntity<Result<?>> update(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.ok(Result.fail("用户不存在"));
        User u = opt.get();
        if (body.containsKey("nickname")) u.setNickname((String) body.get("nickname"));
        if (body.containsKey("phone")) u.setPhone((String) body.get("phone"));
        if (body.containsKey("enabled")) u.setEnabled((Boolean) body.get("enabled"));
        if (body.containsKey("merchantId")) u.setMerchantId(body.get("merchantId") != null ? Long.valueOf(body.get("merchantId").toString()) : null);
        userRepository.save(u);
        return ResponseEntity.ok(Result.ok(u));
    }

    /** 删除用户。 */
    @DeleteMapping("/{id}")
    public ResponseEntity<Result<?>> delete(@PathVariable Long id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) {
            return ResponseEntity.ok(Result.fail("用户不存在"));
        }
        if (opt.get().getRole() == Role.ADMIN) {
            return ResponseEntity.ok(Result.fail("管理员账号不能删除"));
        }
        userRepository.deleteById(id);
        return ResponseEntity.ok(Result.ok(null));
    }
}
