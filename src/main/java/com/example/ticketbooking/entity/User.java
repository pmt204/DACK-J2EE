package com.example.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity người dùng. Quan hệ ManyToMany với Role.
 * FetchType.EAGER đảm bảo roles luôn được load khi load User
 * (cần thiết cho Spring Security).
 */
@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password; // Đã được mã hóa BCrypt

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private boolean enabled = true;

    /** Lý do bị ban (null = chưa bị ban) */
    @Column(length = 500)
    private String banReason;

    /** Thời điểm hết hạn ban (null = ban vĩnh viễn hoặc chưa bị ban) */
    private LocalDateTime banExpiry;

    /**
     * Quan hệ ManyToMany với Role.
     * JoinTable tạo bảng trung gian "user_roles"
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
}
