package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.AirlineDto;
import com.example.ticketbooking.entity.Airline;

import java.util.List;
import java.util.Optional;

public interface AirlineService {

    List<Airline> findAll();

    List<Airline> findActive();

    Optional<Airline> findById(Long id);

    Airline save(AirlineDto dto);

    Airline update(Long id, AirlineDto dto);

    void deleteById(Long id);

    AirlineDto toDto(Airline airline);
}
