package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.BookingDto;
import com.example.ticketbooking.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    // Đặt vé (tạo booking mới)
    Booking createBooking(BookingDto dto, String username);

    // Hủy vé
    void cancelBooking(Long bookingId, String username);

    // Lịch sử đặt vé của user
    List<Booking> getMyBookings(String username);

    // Tất cả bookings (ADMIN)
    Page<Booking> findAll(Pageable pageable);

    Optional<Booking> findById(Long id);

    // Đổi chuyến bay (gia hạn vé)
    Booking rescheduleBooking(Long bookingId, Long newFlightId, String username);
}
