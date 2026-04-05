package com.example.ticketbooking.repository;

import com.example.ticketbooking.entity.Airline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AirlineRepository extends JpaRepository<Airline, Long> {

    Optional<Airline> findByAirlineCode(String airlineCode);

    List<Airline> findByStatus(String status);

    List<Airline> findAllByOrderByAirlineNameAsc();

    boolean existsByAirlineCode(String airlineCode);
}
