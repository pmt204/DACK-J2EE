package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.UserRegistrationDto;
import com.example.ticketbooking.entity.User;

public interface UserService {
    User register(UserRegistrationDto dto);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
