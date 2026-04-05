package com.example.ticketbooking.config;

import com.example.ticketbooking.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Cấu hình Spring Security cho toàn bộ ứng dụng.
 *
 * @EnableWebSecurity   : kích hoạt Spring Security
 * @EnableMethodSecurity: cho phép dùng @PreAuthorize trên method
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Bean mã hóa mật khẩu bằng BCrypt (cost factor = 12 mặc định).
     * BCrypt tự động thêm salt, an toàn hơn MD5/SHA.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Provider xác thực: kết nối UserDetailsService + PasswordEncoder.
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager được inject vào nơi cần xác thực thủ công.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Cấu hình filter chain: định nghĩa quy tắc phân quyền URL, form login, logout.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // ===== Public URLs - không cần đăng nhập =====
                .requestMatchers(
                    "/", "/home",
                    "/auth/login", "/auth/register", "/auth/forgot-password",
                    "/css/**", "/js/**", "/images/**", "/favicon.ico"
                ).permitAll()

                // ===== ADMIN only =====
                .requestMatchers("/admin/**").hasRole("ADMIN")

                // ===== Thêm/Sửa/Xóa chuyến bay: chỉ ADMIN =====
                .requestMatchers("/flights/add", "/flights/edit/**", "/flights/delete/**").hasRole("ADMIN")

                // ===== Xem danh sách chuyến bay: cả USER và ADMIN =====
                .requestMatchers("/flights/list", "/flights", "/flights/search").hasAnyRole("ADMIN", "USER")

                // ===== Đặt vé, xem booking, hồ sơ, đổi mật khẩu: USER và ADMIN =====
                .requestMatchers("/bookings/**", "/profile", "/profile/**").hasAnyRole("ADMIN", "USER")

                // ===== Mọi request còn lại cần đăng nhập =====
                .anyRequest().authenticated()
            )
            // ===== Form Login =====
            .formLogin(form -> form
                .loginPage("/auth/login")               // Trang login tùy chỉnh
                .loginProcessingUrl("/auth/login")       // URL Spring Security xử lý POST login
                .defaultSuccessUrl("/home", true)        // Redirect sau khi login thành công
                .failureUrl("/auth/login?error=true")    // Redirect khi login thất bại
                .usernameParameter("username")           // Tên field username trong form
                .passwordParameter("password")           // Tên field password trong form
                .permitAll()
            )
            // ===== Logout =====
            .logout(logout -> logout
                .logoutUrl("/auth/logout")               // URL POST để logout
                .logoutSuccessUrl("/auth/login?logout=true")
                .invalidateHttpSession(true)             // Xóa session
                .deleteCookies("JSESSIONID")             // Xóa cookie
                .permitAll()
            )
            // ===== Xử lý 403 Access Denied =====
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );

        return http.build();
    }
}
