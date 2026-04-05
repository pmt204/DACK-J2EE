package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.BookingDto;
import com.example.ticketbooking.entity.Booking;
import com.example.ticketbooking.entity.Flight;
import com.example.ticketbooking.repository.FlightRepository;
import com.example.ticketbooking.service.BookingService;
import com.example.ticketbooking.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final FlightService flightService;
    private final FlightRepository flightRepository;

    /**
     * Xử lý đặt vé máy bay.
     */
    @PostMapping("/flight")
    public String bookFlight(
            @Valid @ModelAttribute("bookingDto") BookingDto dto,
            BindingResult result,
            @AuthenticationPrincipal UserDetails currentUser,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return flightService.findById(dto.getFlightId())
                    .map(flight -> {
                        model.addAttribute("flight", flight);
                        return "flight/book";
                    })
                    .orElse("redirect:/flights/list");
        }

        try {
            Booking booking = bookingService.createBooking(dto, currentUser.getUsername());
            // Redirect tới trang xác nhận đặt vé
            return "redirect:/bookings/confirmation/" + booking.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/flights/book/" + dto.getFlightId();
        }
    }

    /**
     * Trang xác nhận đặt vé thành công.
     */
    @GetMapping("/confirmation/{id}")
    public String bookingConfirmation(@PathVariable Long id,
                                       @AuthenticationPrincipal UserDetails currentUser,
                                       Model model) {
        Booking booking = bookingService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        // Chỉ cho phép xem confirmation của chính mình
        if (!booking.getUser().getUsername().equals(currentUser.getUsername())) {
            return "redirect:/bookings/my-bookings";
        }

        model.addAttribute("booking", booking);
        return "booking/confirmation";
    }

    /**
     * Trang lịch sử đặt vé của người dùng hiện tại.
     */
    @GetMapping("/my-bookings")
    public String myBookings(@AuthenticationPrincipal UserDetails currentUser,
                              Model model) {
        List<Booking> bookings = bookingService.getMyBookings(currentUser.getUsername());
        model.addAttribute("bookings", bookings);
        return "booking/my-bookings";
    }

    /**
     * Hủy vé đã đặt.
     */
    @PostMapping("/cancel/{id}")
    public String cancelBooking(@PathVariable Long id,
                                 @AuthenticationPrincipal UserDetails currentUser,
                                 RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id, currentUser.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Đã hủy vé thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/bookings/my-bookings";
    }

    /**
     * Trang đổi chuyến bay: hiển thị chuyến hiện tại + danh sách chuyến cùng tuyến.
     */
    @GetMapping("/reschedule/{id}")
    public String reschedulePage(@PathVariable Long id,
                                  @AuthenticationPrincipal UserDetails currentUser,
                                  Model model) {
        Booking booking = bookingService.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy booking"));

        if (!booking.getUser().getUsername().equals(currentUser.getUsername())) {
            return "redirect:/bookings/my-bookings";
        }

        if (!"CONFIRMED".equals(booking.getStatus())) {
            return "redirect:/bookings/my-bookings";
        }

        // Tìm các chuyến bay cùng tuyến, còn ghế
        Flight currentFlight = booking.getFlight();
        List<Flight> availableFlights = flightRepository.findAvailableFlightsSameRoute(
                currentFlight.getOrigin(),
                currentFlight.getDestination(),
                booking.getSeatCount(),
                currentFlight.getId());

        model.addAttribute("booking", booking);
        model.addAttribute("availableFlights", availableFlights);
        model.addAttribute("currentPrice", booking.getTotalPrice());

        return "booking/reschedule";
    }

    /**
     * Xử lý đổi chuyến bay.
     */
    @PostMapping("/reschedule/{id}")
    public String rescheduleBooking(@PathVariable Long id,
                                     @RequestParam Long newFlightId,
                                     @AuthenticationPrincipal UserDetails currentUser,
                                     RedirectAttributes redirectAttributes) {
        try {
            Booking updated = bookingService.rescheduleBooking(id, newFlightId, currentUser.getUsername());

            BigDecimal oldPrice = updated.getTotalPrice(); // already updated
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đổi chuyến bay thành công! Giá mới: " +
                    String.format("%,.0f", updated.getTotalPrice()) + " ₫");

            return "redirect:/bookings/confirmation/" + updated.getId();
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/bookings/reschedule/" + id;
        }
    }
}
