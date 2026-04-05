package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.BookingDto;
import com.example.ticketbooking.dto.FlightDto;
import com.example.ticketbooking.entity.Flight;
import com.example.ticketbooking.repository.FlightRepository;
import com.example.ticketbooking.service.AirlineService;
import com.example.ticketbooking.service.FlightService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;
    private final AirlineService airlineService;
    private final FlightRepository flightRepository;

    /**
     * Danh sách chuyến bay với tìm kiếm riêng lẻ + phân trang.
     */
    @GetMapping({"/list", ""})
    public String listFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String airline,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureTo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("departureTime").ascending());
        Page<Flight> flightPage = flightService.searchFlights(origin, destination, airline,
                departureFrom, departureTo, pageable);

        model.addAttribute("flights", flightPage);
        model.addAttribute("origin", origin);
        model.addAttribute("destination", destination);
        model.addAttribute("airline", airline);
        model.addAttribute("departureFrom", departureFrom);
        model.addAttribute("departureTo", departureTo);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", flightPage.getTotalPages());
        return "flight/list";
    }

    /** Form đặt vé cho chuyến bay cụ thể */
    @GetMapping("/book/{id}")
    public String showBookingForm(@PathVariable Long id, Model model,
                                  RedirectAttributes redirectAttributes) {
        return flightService.findById(id)
                .map(flight -> {
                    if (flight.getAvailableSeats() == 0) {
                        redirectAttributes.addFlashAttribute("errorMessage", "Chuyến bay này đã hết chỗ!");
                        return "redirect:/flights/list";
                    }
                    BookingDto dto = new BookingDto();
                    dto.setBookingType("FLIGHT");
                    dto.setFlightId(id);
                    model.addAttribute("flight", flight);
                    model.addAttribute("bookingDto", dto);

                    // Tìm chuyến bay về (chiều ngược lại) cho option khứ hồi
                    List<Flight> returnFlights = flightRepository.findAvailableFlightsSameRoute(
                            flight.getDestination(), flight.getOrigin(), 1, -1L);
                    model.addAttribute("returnFlights", returnFlights);

                    return "flight/book";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến bay!");
                    return "redirect:/flights/list";
                });
    }

    // ===================== ADMIN ONLY =====================

    /** Form thêm chuyến bay mới */
    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String showAddForm(Model model) {
        model.addAttribute("flightDto", new FlightDto());
        model.addAttribute("isEdit", false);
        model.addAttribute("airlines", airlineService.findActive());
        return "flight/form";
    }

    /** Xử lý thêm chuyến bay */
    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addFlight(@Valid @ModelAttribute("flightDto") FlightDto dto,
                             BindingResult result, Model model,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            model.addAttribute("airlines", airlineService.findActive());
            return "flight/form";
        }
        flightService.save(dto);
        redirectAttributes.addFlashAttribute("successMessage", "Thêm chuyến bay thành công!");
        return "redirect:/flights/list";
    }

    /** Form sửa chuyến bay */
    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String showEditForm(@PathVariable Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        return flightService.findById(id)
                .map(flight -> {
                    model.addAttribute("flightDto", flightService.toDto(flight));
                    model.addAttribute("isEdit", true);
                    model.addAttribute("airlines", airlineService.findActive());
                    return "flight/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy chuyến bay!");
                    return "redirect:/flights/list";
                });
    }

    /** Xử lý cập nhật chuyến bay */
    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateFlight(@PathVariable Long id,
                                @Valid @ModelAttribute("flightDto") FlightDto dto,
                                BindingResult result, Model model,
                                RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            model.addAttribute("airlines", airlineService.findActive());
            return "flight/form";
        }
        flightService.update(id, dto);
        redirectAttributes.addFlashAttribute("successMessage", "Cập nhật chuyến bay thành công!");
        return "redirect:/flights/list";
    }

    /** Xóa chuyến bay */
    @PostMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteFlight(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            flightService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa chuyến bay!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa: " + e.getMessage());
        }
        return "redirect:/flights/list";
    }
}
