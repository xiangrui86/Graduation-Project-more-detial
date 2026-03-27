package com.mall.service;

import com.mall.entity.User;
import com.mall.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> getById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional
    public User updateProfile(Long userId, Map<String, Object> body) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        if (body.containsKey("nickname")) user.setNickname(toStr(body.get("nickname")));
        if (body.containsKey("avatar")) user.setAvatar(toStr(body.get("avatar")));
        if (body.containsKey("gender")) user.setGender(toStr(body.get("gender")));
        if (body.containsKey("email")) user.setEmail(toStr(body.get("email")));
        if (body.containsKey("phone")) user.setPhone(toStr(body.get("phone")));
        if (body.containsKey("receiverName")) user.setReceiverName(toStr(body.get("receiverName")));
        if (body.containsKey("receiverPhone")) user.setReceiverPhone(toStr(body.get("receiverPhone")));
        if (body.containsKey("receiverAddress")) user.setReceiverAddress(toStr(body.get("receiverAddress")));
        return userRepository.save(user);
    }

    private String toStr(Object v) {
        if (v == null) return null;
        String s = v.toString().trim();
        return s.isEmpty() ? null : s;
    }
}
