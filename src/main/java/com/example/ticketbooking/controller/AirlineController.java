package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.AirlineDto;
import com.example.ticketbooking.service.AirlineService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/airlines")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AirlineController {

    private final AirlineService airlineService;

    /** Danh sách hãng bay */
    @GetMapping
    public String listAirlines(Model model) {
        model.addAttribute("airlines", airlineService.findAll());
        return "admin/airlines";
    }

    /** Form thêm hãng bay */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("airlineDto", new AirlineDto());
        model.addAttribute("isEdit", false);
        return "admin/airline-form";
    }

    /** Xử lý thêm hãng bay */
    @PostMapping("/add")
    public String addAirline(@Valid @ModelAttribute("airlineDto") AirlineDto dto,
                              BindingResult result, Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", false);
            return "admin/airline-form";
        }
        try {
            airlineService.save(dto);
            redirectAttributes.addFlashAttribute("successMessage", "Thêm hãng bay thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/airlines";
    }

    /** Form sửa hãng bay */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model,
                                RedirectAttributes redirectAttributes) {
        return airlineService.findById(id)
                .map(airline -> {
                    model.addAttribute("airlineDto", airlineService.toDto(airline));
                    model.addAttribute("isEdit", true);
                    return "admin/airline-form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("errorMessage", "Không tìm thấy hãng bay!");
                    return "redirect:/admin/airlines";
                });
    }

    /** Xử lý cập nhật hãng bay */
    @PostMapping("/edit/{id}")
    public String updateAirline(@PathVariable Long id,
                                 @Valid @ModelAttribute("airlineDto") AirlineDto dto,
                                 BindingResult result, Model model,
                                 RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", true);
            return "admin/airline-form";
        }
        try {
            airlineService.update(id, dto);
            redirectAttributes.addFlashAttribute("successMessage", "Cập nhật hãng bay thành công!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/airlines";
    }

    /** Xóa hãng bay */
    @PostMapping("/delete/{id}")
    public String deleteAirline(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            airlineService.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Đã xóa hãng bay!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không thể xóa hãng bay (đang có chuyến bay liên kết)!");
        }
        return "redirect:/admin/airlines";
    }
}
