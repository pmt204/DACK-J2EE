package com.example.ticketbooking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entity đặt vé máy bay.
 * Hỗ trợ vé một chiều (ONE_WAY) và khứ hồi (ROUND_TRIP).
 */
@Entity
@Table(name = "bookings")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "booking_type", nullable = false, length = 10)
    private String bookingType = "FLIGHT";

    /** Loại hành trình: ONE_WAY (một chiều) hoặc ROUND_TRIP (khứ hồi) */
    @Column(name = "trip_type", nullable = false, length = 20)
    private String tripType = "ONE_WAY";

    /** Chuyến bay đi */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "flight_id")
    private Flight flight;

    /** Chuyến bay về (chỉ khi tripType = ROUND_TRIP) */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "return_flight_id")
    private Flight returnFlight;

    @Column(name = "passenger_name", nullable = false, length = 100)
    private String passengerName;

    @Column(name = "passenger_email", nullable = false, length = 100)
    private String passengerEmail;

    @Column(name = "passenger_phone", nullable = false, length = 15)
    private String passengerPhone;

    @Column(name = "seat_count", nullable = false)
    private Integer seatCount;

    @Column(name = "total_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalPrice;

    /** Hạng ghế: ECONOMY (phổ thông) hoặc BUSINESS (thương gia) */
    @Column(name = "seat_class", nullable = false, length = 20)
    private String seatClass = "ECONOMY";

    /** Phương thức thanh toán: BANK_TRANSFER, CASH, E_WALLET */
    @Column(name = "payment_method", length = 30)
    private String paymentMethod;

    /** Trạng thái thanh toán: UNPAID, PAID */
    @Column(name = "payment_status", nullable = false, length = 20)
    private String paymentStatus = "UNPAID";

    @Column(nullable = false, length = 20)
    private String status = "CONFIRMED";

    @Column(name = "booking_date", nullable = false, updatable = false)
    private LocalDateTime bookingDate;

    @PrePersist
    protected void onCreate() {
        bookingDate = LocalDateTime.now();
        if (status == null) status = "CONFIRMED";
        if (seatClass == null) seatClass = "ECONOMY";
        if (paymentStatus == null) paymentStatus = "UNPAID";
        if (tripType == null) tripType = "ONE_WAY";
    }
}
