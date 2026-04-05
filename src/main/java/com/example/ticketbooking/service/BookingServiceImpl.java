package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.BookingDto;
import com.example.ticketbooking.entity.*;
import com.example.ticketbooking.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final FlightRepository flightRepository;
    private final EmailService emailService;

    @Override
    @Transactional
    public Booking createBooking(BookingDto dto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));

        Flight flight = flightRepository.findById(dto.getFlightId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến bay"));

        if (flight.getAvailableSeats() < dto.getSeatCount()) {
            throw new RuntimeException("Không đủ ghế trống. Còn lại: " + flight.getAvailableSeats() + " ghế");
        }

        // Trừ số ghế chuyến đi
        flight.setAvailableSeats(flight.getAvailableSeats() - dto.getSeatCount());
        if (flight.getAvailableSeats() == 0) {
            flight.setStatus("FULL");
        }
        flightRepository.save(flight);

        // Tính giá chuyến đi theo hạng ghế
        BigDecimal pricePerSeat = flight.getPrice();
        if ("BUSINESS".equals(dto.getSeatClass())) {
            pricePerSeat = pricePerSeat.multiply(BigDecimal.valueOf(1.5));
        }
        BigDecimal totalPrice = pricePerSeat.multiply(BigDecimal.valueOf(dto.getSeatCount()));

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setBookingType("FLIGHT");
        booking.setTripType(dto.getTripType() != null ? dto.getTripType() : "ONE_WAY");
        booking.setFlight(flight);
        booking.setPassengerName(dto.getPassengerName());
        booking.setPassengerEmail(dto.getPassengerEmail());
        booking.setPassengerPhone(dto.getPassengerPhone());
        booking.setSeatCount(dto.getSeatCount());
        booking.setSeatClass(dto.getSeatClass());
        booking.setPaymentMethod(dto.getPaymentMethod());
        booking.setPaymentStatus("UNPAID");
        booking.setStatus("CONFIRMED");

        // Xử lý chuyến về nếu khứ hồi
        if ("ROUND_TRIP".equals(dto.getTripType()) && dto.getReturnFlightId() != null) {
            Flight returnFlight = flightRepository.findById(dto.getReturnFlightId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến bay về"));

            if (returnFlight.getAvailableSeats() < dto.getSeatCount()) {
                throw new RuntimeException("Chuyến bay về không đủ ghế trống. Còn lại: "
                        + returnFlight.getAvailableSeats() + " ghế");
            }

            // Trừ ghế chuyến về
            returnFlight.setAvailableSeats(returnFlight.getAvailableSeats() - dto.getSeatCount());
            if (returnFlight.getAvailableSeats() == 0) {
                returnFlight.setStatus("FULL");
            }
            flightRepository.save(returnFlight);

            booking.setReturnFlight(returnFlight);

            // Cộng giá chuyến về
            BigDecimal returnPricePerSeat = returnFlight.getPrice();
            if ("BUSINESS".equals(dto.getSeatClass())) {
                returnPricePerSeat = returnPricePerSeat.multiply(BigDecimal.valueOf(1.5));
            }
            totalPrice = totalPrice.add(returnPricePerSeat.multiply(BigDecimal.valueOf(dto.getSeatCount())));
        }

        booking.setTotalPrice(totalPrice);
        // Giả sử bạn đã inject EmailService emailService;
        emailService.sendBookingConfirmationEmail(
                booking.getPassengerEmail(),
                booking.getPassengerName(),
                flight.getFlightNumber());
        return bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền hủy booking này");
        }

        if ("CANCELLED".equals(booking.getStatus())) {
            throw new RuntimeException("Booking đã bị hủy trước đó");
        }

        // Hoàn lại ghế chuyến đi
        if (booking.getFlight() != null) {
            Flight flight = booking.getFlight();
            flight.setAvailableSeats(flight.getAvailableSeats() + booking.getSeatCount());
            if (!"CANCELLED".equals(flight.getStatus())) {
                flight.setStatus("AVAILABLE");
            }
            flightRepository.save(flight);
        }

        // Hoàn lại ghế chuyến về (nếu khứ hồi)
        if (booking.getReturnFlight() != null) {
            Flight returnFlight = booking.getReturnFlight();
            returnFlight.setAvailableSeats(returnFlight.getAvailableSeats() + booking.getSeatCount());
            if (!"CANCELLED".equals(returnFlight.getStatus())) {
                returnFlight.setStatus("AVAILABLE");
            }
            flightRepository.save(returnFlight);
        }

        booking.setStatus("CANCELLED");
        bookingRepository.save(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Booking> getMyBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        return bookingRepository.findByUserOrderByBookingDateDesc(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Booking> findAll(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return bookingRepository.findById(id);
    }

    @Override
    @Transactional
    public Booking rescheduleBooking(Long bookingId, Long newFlightId, String username) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        if (!booking.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Bạn không có quyền đổi vé này");
        }

        if (!"CONFIRMED".equals(booking.getStatus())) {
            throw new RuntimeException("Chỉ có thể đổi vé đang xác nhận");
        }

        Flight oldFlight = booking.getFlight();
        Flight newFlight = flightRepository.findById(newFlightId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến bay mới"));

        if (newFlight.getAvailableSeats() < booking.getSeatCount()) {
            throw new RuntimeException("Chuyến bay mới không đủ ghế trống");
        }

        // Hoàn ghế cho chuyến cũ
        oldFlight.setAvailableSeats(oldFlight.getAvailableSeats() + booking.getSeatCount());
        if (!"CANCELLED".equals(oldFlight.getStatus())) {
            oldFlight.setStatus("AVAILABLE");
        }
        flightRepository.save(oldFlight);

        // Trừ ghế chuyến mới
        newFlight.setAvailableSeats(newFlight.getAvailableSeats() - booking.getSeatCount());
        if (newFlight.getAvailableSeats() == 0) {
            newFlight.setStatus("FULL");
        }
        flightRepository.save(newFlight);

        // Cập nhật booking
        booking.setFlight(newFlight);
        BigDecimal newPricePerSeat = newFlight.getPrice();
        if ("BUSINESS".equals(booking.getSeatClass())) {
            newPricePerSeat = newPricePerSeat.multiply(BigDecimal.valueOf(1.5));
        }
        BigDecimal newTotal = newPricePerSeat.multiply(BigDecimal.valueOf(booking.getSeatCount()));

        // Nếu khứ hồi, cộng thêm giá chuyến về
        if (booking.getReturnFlight() != null) {
            BigDecimal returnPrice = booking.getReturnFlight().getPrice();
            if ("BUSINESS".equals(booking.getSeatClass())) {
                returnPrice = returnPrice.multiply(BigDecimal.valueOf(1.5));
            }
            newTotal = newTotal.add(returnPrice.multiply(BigDecimal.valueOf(booking.getSeatCount())));
        }

        booking.setTotalPrice(newTotal);
        return bookingRepository.save(booking);
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void cancelUnpaidBookingsAutomatically() {
        // Lấy thời điểm hiện tại lùi lại 30 phút
        LocalDateTime deadline = LocalDateTime.now().minusMinutes(30);

        // Tìm tất cả các booking UNPAID, CONFIRMED và đặt trước cái deadline kia
        List<Booking> expiredBookings = bookingRepository
                .findByPaymentStatusAndStatusAndBookingDateBefore("UNPAID", "CONFIRMED", deadline);

        if (!expiredBookings.isEmpty()) {
            System.out.println("Đang xử lý hủy tự động " + expiredBookings.size() + " vé quá hạn thanh toán...");
        }

        for (Booking booking : expiredBookings) {
            // 1. Cập nhật trạng thái vé
            booking.setStatus("CANCELLED");

            // 2. Hoàn lại ghế cho chuyến bay đi
            Flight flight = booking.getFlight();
            if (flight != null) {
                flight.setAvailableSeats(flight.getAvailableSeats() + booking.getSeatCount());
                if ("FULL".equals(flight.getStatus())) {
                    flight.setStatus("AVAILABLE");
                }
                flightRepository.save(flight);
            }

            // 3. Hoàn lại ghế cho chuyến bay về (nếu là vé khứ hồi)
            if (booking.getReturnFlight() != null) {
                Flight returnFlight = booking.getReturnFlight();
                returnFlight.setAvailableSeats(returnFlight.getAvailableSeats() + booking.getSeatCount());
                if ("FULL".equals(returnFlight.getStatus())) {
                    returnFlight.setStatus("AVAILABLE");
                }
                flightRepository.save(returnFlight);
            }

            // 4. Lưu lại database
            bookingRepository.save(booking);
            System.out
                    .println("-> Đã hủy booking #" + booking.getId() + " của user: " + booking.getUser().getUsername());
        }
    }
}
