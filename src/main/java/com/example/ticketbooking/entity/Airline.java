package com.example.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Entity hãng hàng không.
 * Quản lý riêng danh sách hãng bay (tách khỏi Flight).
 */
@Entity
@Table(name = "airlines")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Airline {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Mã hãng bay: VN, VJ, QH, ... */
    @Column(name = "airline_code", nullable = false, unique = true, length = 10)
    private String airlineCode;

    /** Tên hãng bay: Vietnam Airlines, Vietjet Air, ... */
    @Column(name = "airline_name", nullable = false, length = 100)
    private String airlineName;

    /** Quốc gia */
    @Column(length = 100)
    private String country;

    /** Logo URL (tùy chọn) */
    @Column(name = "logo_url", length = 255)
    private String logoUrl;

    /** Trạng thái: ACTIVE, INACTIVE */
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
