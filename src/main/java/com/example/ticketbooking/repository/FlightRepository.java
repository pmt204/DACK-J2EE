package com.example.ticketbooking.repository;

import com.example.ticketbooking.entity.Flight;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    /**
     * Tìm kiếm chuyến bay theo điểm đi, điểm đến, tên hãng bay, khoảng ngày.
     * Hỗ trợ tìm riêng lẻ từng trường (null = bỏ qua điều kiện).
     */
    @Query("SELECT f FROM Flight f WHERE " +
           "(:origin IS NULL OR LOWER(f.origin) LIKE LOWER(CONCAT('%', :origin, '%'))) AND " +
           "(:destination IS NULL OR LOWER(f.destination) LIKE LOWER(CONCAT('%', :destination, '%'))) AND " +
           "(:airline IS NULL OR LOWER(f.airline.airlineName) LIKE LOWER(CONCAT('%', :airline, '%'))) AND " +
           "(:departureFrom IS NULL OR f.departureTime >= :departureFrom) AND " +
           "(:departureTo IS NULL OR f.departureTime <= :departureTo)")
    Page<Flight> searchFlights(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("airline") String airline,
            @Param("departureFrom") LocalDateTime departureFrom,
            @Param("departureTo") LocalDateTime departureTo,
            Pageable pageable
    );

    // Tìm chuyến bay cùng tuyến, còn ghế, chưa hủy, khác chuyến hiện tại
    @Query("SELECT f FROM Flight f WHERE f.origin = :origin AND f.destination = :destination " +
           "AND f.availableSeats >= :minSeats AND f.status != 'CANCELLED' AND f.id != :excludeId " +
           "ORDER BY f.departureTime ASC")
    java.util.List<Flight> findAvailableFlightsSameRoute(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("minSeats") int minSeats,
            @Param("excludeId") Long excludeId);
}
