package com.example.ticketbooking.service;

import com.example.ticketbooking.entity.Role;
import com.example.ticketbooking.entity.User;
import com.example.ticketbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Service tích hợp Spring Security với hệ thống User của chúng ta.
 * Spring Security gọi loadUserByUsername() khi người dùng đăng nhập.
 * Tự động mở khóa tài khoản khi hết hạn ban.
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Tìm user trong database theo username
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Không tìm thấy tài khoản: " + username));

        // Tự động mở khóa nếu ban đã hết hạn
        if (!user.isEnabled() && user.getBanExpiry() != null
                && LocalDateTime.now().isAfter(user.getBanExpiry())) {
            user.setEnabled(true);
            user.setBanReason(null);
            user.setBanExpiry(null);
            userRepository.save(user);
        }

        // Chuyển đổi sang UserDetails của Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.isEnabled(),
                true, true, true, // accountNonExpired, credentialsNonExpired, accountNonLocked
                mapRolesToAuthorities(user.getRoles())
        );
    }

    /**
     * Chuyển đổi Set<Role> thành Collection<GrantedAuthority> cho Spring Security.
     */
    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }
}
