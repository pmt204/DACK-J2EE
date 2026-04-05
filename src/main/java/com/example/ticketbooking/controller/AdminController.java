package com.example.ticketbooking.controller;

import com.example.ticketbooking.repository.BookingRepository;
import com.example.ticketbooking.repository.FlightRepository;
import com.example.ticketbooking.repository.UserRepository;
import com.example.ticketbooking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final BookingService bookingService;
    private final BookingRepository bookingRepository;
    private final FlightRepository flightRepository;
    private final UserRepository userRepository;

    @GetMapping({"/dashboard", ""})
    public String dashboard(Model model) {
        model.addAttribute("totalFlights", flightRepository.count());
        model.addAttribute("totalBookings", bookingRepository.count());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("confirmedBookings", bookingRepository.countConfirmed());

        model.addAttribute("recentBookings",
                bookingService.findAll(
                        PageRequest.of(0, 10, Sort.by("bookingDate").descending())
                ).getContent());

        return "admin/dashboard";
    }

    @GetMapping("/bookings")
    public String allBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "15") int size,
            Model model) {

        var bookingPage = bookingService.findAll(
                PageRequest.of(page, size, Sort.by("bookingDate").descending()));

        model.addAttribute("bookings", bookingPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", bookingPage.getTotalPages());
        return "admin/bookings";
    }

    @PostMapping("/bookings/cancel/{id}")
    public String cancelBookingByAdmin(@PathVariable Long id,
                                        RedirectAttributes redirectAttributes) {
        bookingService.findById(id).ifPresent(booking -> {
            booking.setStatus("CANCELLED");
            bookingRepository.save(booking);
        });
        redirectAttributes.addFlashAttribute("successMessage", "Đã hủy booking #" + id);
        return "redirect:/admin/bookings";
    }

    /**
     * Admin xác nhận thanh toán cho booking.
     */
    @PostMapping("/bookings/confirm-payment/{id}")
    public String confirmPayment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookingService.findById(id).ifPresent(booking -> {
            booking.setPaymentStatus("PAID");
            bookingRepository.save(booking);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã xác nhận thanh toán cho booking #" + id);
        });
        return "redirect:/admin/bookings";
    }

    /**
     * Admin đánh dấu chưa thanh toán (hoàn tác).
     */
    @PostMapping("/bookings/unpaid/{id}")
    public String markUnpaid(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        bookingService.findById(id).ifPresent(booking -> {
            booking.setPaymentStatus("UNPAID");
            bookingRepository.save(booking);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã đánh dấu chưa thanh toán cho booking #" + id);
        });
        return "redirect:/admin/bookings";
    }

    /**
     * Quản lý người dùng: hiển thị danh sách tất cả user.
     */
    @GetMapping("/users")
    public String userManagement(Model model) {
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("now", LocalDateTime.now());
        return "admin/users";
    }

    /**
     * Ban tài khoản người dùng với thời hạn và lý do.
     * @param duration: số ngày ban (0 = vĩnh viễn, 1/7/30 = tạm thời)
     * @param reason: lý do ban
     */
    @PostMapping("/users/ban/{id}")
    public String banUser(@PathVariable Long id,
                          @RequestParam(defaultValue = "0") int duration,
                          @RequestParam(defaultValue = "Vi phạm quy định") String reason,
                          RedirectAttributes redirectAttributes) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(false);
            user.setBanReason(reason);

            if (duration > 0) {
                // Ban tạm thời: tính thời điểm hết hạn
                user.setBanExpiry(LocalDateTime.now().plusDays(duration));
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã ban " + user.getUsername() + " trong " + duration + " ngày. Lý do: " + reason);
            } else {
                // Ban vĩnh viễn: không có thời hạn
                user.setBanExpiry(null);
                redirectAttributes.addFlashAttribute("successMessage",
                        "Đã ban vĩnh viễn " + user.getUsername() + ". Lý do: " + reason);
            }

            userRepository.save(user);
        });
        return "redirect:/admin/users";
    }

    /**
     * Gia hạn thời gian ban: thêm số ngày vào thời hạn hiện tại.
     */
    @PostMapping("/users/extend-ban/{id}")
    public String extendBan(@PathVariable Long id,
                            @RequestParam int extraDays,
                            RedirectAttributes redirectAttributes) {
        userRepository.findById(id).ifPresent(user -> {
            if (!user.isEnabled()) {
                if (user.getBanExpiry() != null) {
                    // Gia hạn từ thời điểm hết hạn hiện tại
                    user.setBanExpiry(user.getBanExpiry().plusDays(extraDays));
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Đã gia hạn ban " + user.getUsername() + " thêm " + extraDays + " ngày");
                } else {
                    // Đang ban vĩnh viễn → chuyển sang ban có thời hạn
                    user.setBanExpiry(LocalDateTime.now().plusDays(extraDays));
                    redirectAttributes.addFlashAttribute("successMessage",
                            "Đã chuyển " + user.getUsername() + " từ ban vĩnh viễn sang ban " + extraDays + " ngày");
                }
                userRepository.save(user);
            }
        });
        return "redirect:/admin/users";
    }

    /**
     * Mở khóa tài khoản người dùng (gỡ ban).
     */
    @PostMapping("/users/unban/{id}")
    public String unbanUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userRepository.findById(id).ifPresent(user -> {
            user.setEnabled(true);
            user.setBanReason(null);
            user.setBanExpiry(null);
            userRepository.save(user);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Đã mở khóa tài khoản: " + user.getUsername());
        });
        return "redirect:/admin/users";
    }
}
