package com.example.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity đại diện cho vai trò người dùng trong hệ thống.
 * Spring Security yêu cầu tên role bắt đầu bằng "ROLE_" (vd: ROLE_ADMIN, ROLE_USER)
 */
@Entity
@Table(name = "roles")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name; // ROLE_ADMIN hoặc ROLE_USER

    // Constructor tiện lợi chỉ với name
    public Role(String name) {
        this.name = name;
    }
}
