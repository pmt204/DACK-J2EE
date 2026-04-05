package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.FlightDto;
import com.example.ticketbooking.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface FlightService {

    Page<Flight> searchFlights(String origin, String destination, String airline,
                               java.time.LocalDate departureFrom, java.time.LocalDate departureTo,
                               Pageable pageable);

    Page<Flight> findAll(Pageable pageable);

    Optional<Flight> findById(Long id);

    Flight save(FlightDto dto);

    Flight update(Long id, FlightDto dto);

    void deleteById(Long id);

    // Chuyển Entity -> DTO (để điền form sửa)
    FlightDto toDto(Flight flight);
}
