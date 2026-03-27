package com.mall.service;
import com.mall.entity.Merchant;
import com.mall.repository.MerchantRepository;

import com.mall.common.Role;
import com.mall.entity.User;
import com.mall.repository.UserRepository;
import com.mall.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
/** 认证服务：处理登录鉴权与用户注册。 */
public class AuthService {

    private final UserRepository userRepository;
    private final MerchantRepository merchantRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /** 用户登录：校验账号状态并签发 JWT。 */
    public Map<String, Object> login(String username, String password, String role) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (opt.isEmpty() || !opt.get().getEnabled()) {
            throw new RuntimeException("用户名或密码错误");
        }
        User user = opt.get();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }
        // 验证选择的角色是否与用户实际角色匹配
        if (!user.getRole().name().equals(role)) {
            throw new RuntimeException("账号角色不匹配，请选择正确的角色登录");
        }
        // 运营账号需校验运营主体是否启用
        if (user.getRole() == Role.MERCHANT && user.getMerchantId() != null) {
            Optional<Merchant> merchantOpt = merchantRepository.findById(user.getMerchantId());
            if (merchantOpt.isEmpty() || !Boolean.TRUE.equals(merchantOpt.get().getEnabled())) {
                throw new RuntimeException("运营已被禁用，无法登录");
            }
        }
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole().name());
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        result.put("userId", user.getId());
        result.put("username", user.getUsername());
        result.put("role", user.getRole().name());
        result.put("nickname", user.getNickname());
        result.put("avatar", user.getAvatar());
        result.put("gender", user.getGender());
        result.put("merchantId", user.getMerchantId());
        return result;
    }

    /** 普通用户注册。 */
    public void registerUser(
            String username,
            String password,
            String nickname,
            String gender,
            String email,
            String phone,
            String receiverName,
            String receiverPhone,
            String receiverAddress
    ) {
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("用户名已存在");
        }
        User user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .nickname(nickname != null ? nickname : username)
                .gender(gender)
                .email(email)
                .phone(phone)
                .receiverName(receiverName)
                .receiverPhone(receiverPhone)
                .receiverAddress(receiverAddress)
                .role(Role.USER)
                .enabled(true)
                .build();
        userRepository.save(user);
    }
}
