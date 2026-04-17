package com.mall.repository;

import com.mall.common.Role;
import com.mall.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPhone(String phone);

    boolean existsByUsername(String username);

    List<User> findByRole(Role role);

    List<User> findByMerchantId(Long merchantId);
}
