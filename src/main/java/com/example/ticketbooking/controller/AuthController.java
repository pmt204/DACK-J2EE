package com.example.ticketbooking.controller;

import com.example.ticketbooking.dto.UserRegistrationDto;
import com.example.ticketbooking.entity.User;
import com.example.ticketbooking.repository.UserRepository;
import com.example.ticketbooking.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /** Hiển thị trang đăng nhập */
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    /** Hiển thị form đăng ký */
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDto", new UserRegistrationDto());
        return "auth/register";
    }

    /**
     * Xử lý form đăng ký.
     * @Valid kích hoạt Bean Validation trên UserRegistrationDto.
     * BindingResult chứa các lỗi validation.
     */
    @PostMapping("/register")
    public String register(
            @Valid @ModelAttribute("userDto") UserRegistrationDto dto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        // Kiểm tra mật khẩu xác nhận khớp
        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.userDto",
                    "Mật khẩu xác nhận không khớp");
        }

        // Kiểm tra username đã tồn tại
        if (userService.existsByUsername(dto.getUsername())) {
            result.rejectValue("username", "error.userDto",
                    "Tên đăng nhập đã được sử dụng");
        }

        // Kiểm tra email đã tồn tại
        if (userService.existsByEmail(dto.getEmail())) {
            result.rejectValue("email", "error.userDto",
                    "Email đã được sử dụng");
        }

        // Nếu có lỗi, quay lại form với thông báo lỗi
        if (result.hasErrors()) {
            return "auth/register";
        }

        // Lưu user vào database
        userService.register(dto);
        redirectAttributes.addFlashAttribute("successMessage",
                "Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/auth/login";
    }

    /** Hiển thị form quên mật khẩu */
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    /**
     * Xử lý quên mật khẩu: xác minh username + email, cho đặt mật khẩu mới.
     */
    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestParam String username,
                                 @RequestParam String email,
                                 @RequestParam String newPassword,
                                 @RequestParam String confirmPassword,
                                 RedirectAttributes redirectAttributes) {

        // Kiểm tra mật khẩu mới khớp
        if (!newPassword.equals(confirmPassword)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới không khớp!");
            return "redirect:/auth/forgot-password";
        }

        // Kiểm tra độ dài tối thiểu
        if (newPassword.length() < 6) {
            redirectAttributes.addFlashAttribute("errorMessage", "Mật khẩu mới phải có ít nhất 6 ký tự!");
            return "redirect:/auth/forgot-password";
        }

        // Tìm user theo username + email
        Optional<User> userOpt = userRepository.findByUsernameAndEmail(username, email);
        if (userOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Không tìm thấy tài khoản với username và email này!");
            return "redirect:/auth/forgot-password";
        }

        // Đặt mật khẩu mới (mã hóa BCrypt)
        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("successMessage",
                "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
        return "redirect:/auth/login";
    }
}
