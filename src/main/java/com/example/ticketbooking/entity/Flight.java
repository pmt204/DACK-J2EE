package com.example.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity chuyến bay.
 * Liên kết với Airline (hãng hàng không) qua ManyToOne.
 */
@Entity
@Table(name = "flights")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Flight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "flight_number", nullable = false, unique = true, length = 20)
    private String flightNumber; // Ví dụ: VN123, VJ456

    /** Hãng hàng không — liên kết bảng airlines */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "airline_id", nullable = false)
    private Airline airline;

    @Column(nullable = false, length = 100)
    private String origin; // Điểm khởi hành

    @Column(nullable = false, length = 100)
    private String destination; // Điểm đến

    @Column(name = "departure_time", nullable = false)
    private LocalDateTime departureTime; // Thời gian khởi hành

    @Column(name = "arrival_time", nullable = false)
    private LocalDateTime arrivalTime; // Thời gian đến nơi

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal price; // Giá vé (VNĐ)

    @Column(name = "available_seats", nullable = false)
    private Integer availableSeats; // Số ghế còn trống

    @Column(length = 500)
    private String description; // Mô tả thêm

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false, length = 20)
    private String status = "AVAILABLE"; // AVAILABLE, FULL, CANCELLED, DELAYED

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /** Helper: lấy tên hãng bay */
    public String getAirlineName() {
        return airline != null ? airline.getAirlineName() : "";
    }
}
