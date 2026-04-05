package com.example.ticketbooking.repository;

import com.example.ticketbooking.entity.Booking;
import com.example.ticketbooking.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserOrderByBookingDateDesc(User user);

    Page<Booking> findByUser(User user, Pageable pageable);

    long countByUser(User user);

    @Query("SELECT COUNT(b) FROM Booking b WHERE b.status = 'CONFIRMED'")
    long countConfirmed();

    // Tìm các vé chưa thanh toán và đã quá hạn
    List<Booking> findByPaymentStatusAndStatusAndBookingDateBefore(
            String paymentStatus, String status, java.time.LocalDateTime time);
}
