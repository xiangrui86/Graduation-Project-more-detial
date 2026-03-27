package com.mall.entity;

import com.mall.common.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sys_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 64)
    private String username;

    @Column(nullable = false, length = 128)
    @JsonIgnore
    private String password;

    @Column(length = 32)
    private String nickname;

    @Column(length = 64)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 255)
    private String avatar;

    /** 性别：MALE/FEMALE/OTHER */
    @Column(length = 10)
    private String gender;

    /** 收货人信息 */
    @Column(length = 32)
    private String receiverName;

    @Column(length = 20)
    private String receiverPhone;

    @Column(length = 255)
    private String receiverAddress;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private Role role;

    /** 运营关联：仅当 role=MERCHANT 时有效 */
    @Column(name = "merchant_id")
    private Long merchantId;

    @Column(nullable = false)
    private Boolean enabled = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private java.util.List<com.mall.entity.Address> addresses;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
