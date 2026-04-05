package com.example.ticketbooking.service;

import com.example.ticketbooking.dto.FlightDto;
import com.example.ticketbooking.entity.Airline;
import com.example.ticketbooking.entity.Flight;
import com.example.ticketbooking.repository.AirlineRepository;
import com.example.ticketbooking.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlightServiceImpl implements FlightService {

    private final FlightRepository flightRepository;
    private final AirlineRepository airlineRepository;

    @Override
    public Page<Flight> searchFlights(String origin, String destination, String airline,
                                       LocalDate departureFrom, LocalDate departureTo,
                                       Pageable pageable) {
        boolean hasFilter = (origin != null && !origin.isBlank())
                || (destination != null && !destination.isBlank())
                || (airline != null && !airline.isBlank())
                || departureFrom != null
                || departureTo != null;

        if (hasFilter) {
            LocalDateTime fromDateTime = departureFrom != null ? departureFrom.atStartOfDay() : null;
            LocalDateTime toDateTime = departureTo != null ? departureTo.atTime(23, 59, 59) : null;

            return flightRepository.searchFlights(
                    blankToNull(origin),
                    blankToNull(destination),
                    blankToNull(airline),
                    fromDateTime,
                    toDateTime,
                    pageable
            );
        }
        return flightRepository.findAll(pageable);
    }

    @Override
    public Page<Flight> findAll(Pageable pageable) {
        return flightRepository.findAll(pageable);
    }

    @Override
    public Optional<Flight> findById(Long id) {
        return flightRepository.findById(id);
    }

    @Override
    @Transactional
    public Flight save(FlightDto dto) {
        Flight flight = new Flight();
        mapDtoToFlight(dto, flight);
        return flightRepository.save(flight);
    }

    @Override
    @Transactional
    public Flight update(Long id, FlightDto dto) {
        Flight flight = flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chuyến bay id: " + id));
        mapDtoToFlight(dto, flight);
        return flightRepository.save(flight);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        flightRepository.deleteById(id);
    }

    @Override
    public FlightDto toDto(Flight flight) {
        FlightDto dto = new FlightDto();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setAirlineId(flight.getAirline() != null ? flight.getAirline().getId() : null);
        dto.setOrigin(flight.getOrigin());
        dto.setDestination(flight.getDestination());
        dto.setDepartureTime(flight.getDepartureTime());
        dto.setArrivalTime(flight.getArrivalTime());
        dto.setPrice(flight.getPrice());
        dto.setAvailableSeats(flight.getAvailableSeats());
        dto.setDescription(flight.getDescription());
        dto.setStatus(flight.getStatus());
        return dto;
    }

    private void mapDtoToFlight(FlightDto dto, Flight flight) {
        Airline airline = airlineRepository.findById(dto.getAirlineId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hãng bay id: " + dto.getAirlineId()));

        flight.setFlightNumber(dto.getFlightNumber());
        flight.setAirline(airline);
        flight.setOrigin(dto.getOrigin());
        flight.setDestination(dto.getDestination());
        flight.setDepartureTime(dto.getDepartureTime());
        flight.setArrivalTime(dto.getArrivalTime());
        flight.setPrice(dto.getPrice());
        flight.setAvailableSeats(dto.getAvailableSeats());
        flight.setDescription(dto.getDescription());
        flight.setStatus(dto.getStatus() != null ? dto.getStatus() : "AVAILABLE");
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }
}
