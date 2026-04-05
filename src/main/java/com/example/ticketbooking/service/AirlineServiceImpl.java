package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.AirlineDto;
import com.example.ticketbooking.entity.Airline;
import com.example.ticketbooking.repository.AirlineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AirlineServiceImpl implements AirlineService {

    private final AirlineRepository airlineRepository;

    @Override
    public List<Airline> findAll() {
        return airlineRepository.findAllByOrderByAirlineNameAsc();
    }

    @Override
    public List<Airline> findActive() {
        return airlineRepository.findByStatus("ACTIVE");
    }

    @Override
    public Optional<Airline> findById(Long id) {
        return airlineRepository.findById(id);
    }

    @Override
    @Transactional
    public Airline save(AirlineDto dto) {
        if (airlineRepository.existsByAirlineCode(dto.getAirlineCode())) {
            throw new RuntimeException("Mã hãng bay '" + dto.getAirlineCode() + "' đã tồn tại!");
        }
        Airline airline = new Airline();
        mapDtoToEntity(dto, airline);
        return airlineRepository.save(airline);
    }

    @Override
    @Transactional
    public Airline update(Long id, AirlineDto dto) {
        Airline airline = airlineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng bay id: " + id));
        mapDtoToEntity(dto, airline);
        return airlineRepository.save(airline);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        airlineRepository.deleteById(id);
    }

    @Override
    public AirlineDto toDto(Airline airline) {
        AirlineDto dto = new AirlineDto();
        dto.setId(airline.getId());
        dto.setAirlineCode(airline.getAirlineCode());
        dto.setAirlineName(airline.getAirlineName());
        dto.setCountry(airline.getCountry());
        dto.setLogoUrl(airline.getLogoUrl());
        dto.setStatus(airline.getStatus());
        return dto;
    }

    private void mapDtoToEntity(AirlineDto dto, Airline airline) {
        airline.setAirlineCode(dto.getAirlineCode().toUpperCase());
        airline.setAirlineName(dto.getAirlineName());
        airline.setCountry(dto.getCountry());
        airline.setLogoUrl(dto.getLogoUrl());
        airline.setStatus(dto.getStatus() != null ? dto.getStatus() : "ACTIVE");
    }
}
