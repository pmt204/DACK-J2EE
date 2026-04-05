package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.UserRegistrationDto;
import com.example.ticketbooking.entity.Role;
import com.example.ticketbooking.entity.User;
import com.example.ticketbooking.repository.RoleRepository;
import com.example.ticketbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder; // Inject từ SecurityConfig

    @Override
    @Transactional
    public User register(UserRegistrationDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        // Mã hóa mật khẩu bằng BCrypt trước khi lưu
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setEnabled(true);

        // Gán role USER mặc định cho người đăng ký mới
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Role ROLE_USER chưa được khởi tạo"));

        Set<Role> roles = new HashSet<>();
        roles.add(userRole);
        user.setRoles(roles);

        return userRepository.save(user);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
