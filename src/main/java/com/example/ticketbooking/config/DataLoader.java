package com.example.ticketbooking.config;

import com.example.ticketbooking.entity.*;
import com.example.ticketbooking.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * DataLoader: Tự động import dữ liệu mẫu khi khởi động ứng dụng.
 * - Tạo roles + users (admin, user1)
 * - Tạo hãng bay + nhiều chuyến bay mẫu với nhiều ngày
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DataLoader implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AirlineRepository airlineRepository;
    private final FlightRepository flightRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        initRoles();
        initUsers();
        initAirlinesAndFlights();
    }

    private void initRoles() {
        if (roleRepository.count() == 0) {
            roleRepository.save(new Role("ROLE_ADMIN"));
            roleRepository.save(new Role("ROLE_USER"));
            log.info(">>> Đã khởi tạo roles: ROLE_ADMIN, ROLE_USER");
        }
    }

    private void initUsers() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@ticketbooking.vn");
            admin.setEnabled(true);
            Set<Role> adminRoles = new HashSet<>();
            roleRepository.findByName("ROLE_ADMIN").ifPresent(adminRoles::add);
            admin.setRoles(adminRoles);
            userRepository.save(admin);
            log.info(">>> Tài khoản ADMIN: username=admin | password=admin123");
        }

        if (!userRepository.existsByUsername("user1")) {
            User user = new User();
            user.setUsername("user1");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user1@example.com");
            user.setEnabled(true);
            Set<Role> userRoles = new HashSet<>();
            roleRepository.findByName("ROLE_USER").ifPresent(userRoles::add);
            user.setRoles(userRoles);
            userRepository.save(user);
            log.info(">>> Tài khoản USER: username=user1 | password=user123");
        }
    }

    private void initAirlinesAndFlights() {
        if (airlineRepository.count() > 0) {
            log.info("Hãng bay đã tồn tại, bỏ qua import chuyến bay.");
            return;
        }

        log.info("=== BẮT ĐẦU IMPORT DỮ LIỆU MẪU ===");

        // ===== 1. Tạo hãng bay =====
        Airline vna = createAirline("VN", "Vietnam Airlines", "Việt Nam");
        Airline vj  = createAirline("VJ", "Vietjet Air", "Việt Nam");
        Airline qh  = createAirline("QH", "Bamboo Airways", "Việt Nam");
        Airline bl  = createAirline("BL", "Pacific Airlines", "Việt Nam");
        Airline vu  = createAirline("VU", "Vietravel Airlines", "Việt Nam");
        Airline sq  = createAirline("SQ", "Singapore Airlines", "Singapore");
        Airline tg  = createAirline("TG", "Thai Airways", "Thái Lan");

        // ===== 2. Tạo chuyến bay — nhiều ngày khác nhau =====
        LocalDateTime now = LocalDateTime.now();

        // --- Hà Nội → TP.HCM (nhiều hãng, nhiều ngày) ---
        createFlight("VN100", vna, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(1).withHour(6).withMinute(0),
                now.plusDays(1).withHour(8).withMinute(10),
                new BigDecimal("1500000"), 120);
        createFlight("VN102", vna, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(1).withHour(14).withMinute(30),
                now.plusDays(1).withHour(16).withMinute(40),
                new BigDecimal("1600000"), 100);
        createFlight("VJ201", vj, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(2).withHour(7).withMinute(0),
                now.plusDays(2).withHour(9).withMinute(15),
                new BigDecimal("990000"), 180);
        createFlight("VJ203", vj, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(2).withHour(19).withMinute(0),
                now.plusDays(2).withHour(21).withMinute(10),
                new BigDecimal("890000"), 180);
        createFlight("QH301", qh, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(3).withHour(8).withMinute(30),
                now.plusDays(3).withHour(10).withMinute(45),
                new BigDecimal("1200000"), 150);
        createFlight("BL401", bl, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(4).withHour(10).withMinute(0),
                now.plusDays(4).withHour(12).withMinute(15),
                new BigDecimal("850000"), 140);
        createFlight("VU501", vu, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(5).withHour(9).withMinute(0),
                now.plusDays(5).withHour(11).withMinute(10),
                new BigDecimal("950000"), 130);

        // --- TP.HCM → Hà Nội (chiều ngược - cho khứ hồi) ---
        createFlight("VN101", vna, "TP. Hồ Chí Minh", "Hà Nội",
                now.plusDays(3).withHour(17).withMinute(0),
                now.plusDays(3).withHour(19).withMinute(10),
                new BigDecimal("1500000"), 120);
        createFlight("VJ202", vj, "TP. Hồ Chí Minh", "Hà Nội",
                now.plusDays(4).withHour(6).withMinute(30),
                now.plusDays(4).withHour(8).withMinute(45),
                new BigDecimal("990000"), 180);
        createFlight("QH302", qh, "TP. Hồ Chí Minh", "Hà Nội",
                now.plusDays(5).withHour(14).withMinute(0),
                now.plusDays(5).withHour(16).withMinute(15),
                new BigDecimal("1250000"), 150);
        createFlight("VN103", vna, "TP. Hồ Chí Minh", "Hà Nội",
                now.plusDays(6).withHour(20).withMinute(0),
                now.plusDays(6).withHour(22).withMinute(10),
                new BigDecimal("1700000"), 100);

        // --- Hà Nội → Đà Nẵng ---
        createFlight("VN110", vna, "Hà Nội", "Đà Nẵng",
                now.plusDays(1).withHour(8).withMinute(0),
                now.plusDays(1).withHour(9).withMinute(20),
                new BigDecimal("1100000"), 130);
        createFlight("VJ210", vj, "Hà Nội", "Đà Nẵng",
                now.plusDays(2).withHour(11).withMinute(30),
                now.plusDays(2).withHour(12).withMinute(50),
                new BigDecimal("750000"), 180);
        createFlight("QH310", qh, "Hà Nội", "Đà Nẵng",
                now.plusDays(3).withHour(15).withMinute(0),
                now.plusDays(3).withHour(16).withMinute(20),
                new BigDecimal("1050000"), 150);

        // --- Đà Nẵng → Hà Nội ---
        createFlight("VN111", vna, "Đà Nẵng", "Hà Nội",
                now.plusDays(5).withHour(10).withMinute(0),
                now.plusDays(5).withHour(11).withMinute(20),
                new BigDecimal("1100000"), 130);
        createFlight("VJ211", vj, "Đà Nẵng", "Hà Nội",
                now.plusDays(6).withHour(16).withMinute(0),
                now.plusDays(6).withHour(17).withMinute(20),
                new BigDecimal("780000"), 180);

        // --- TP.HCM → Đà Nẵng ---
        createFlight("VN120", vna, "TP. Hồ Chí Minh", "Đà Nẵng",
                now.plusDays(1).withHour(9).withMinute(30),
                now.plusDays(1).withHour(10).withMinute(50),
                new BigDecimal("1050000"), 120);
        createFlight("VJ220", vj, "TP. Hồ Chí Minh", "Đà Nẵng",
                now.plusDays(3).withHour(13).withMinute(0),
                now.plusDays(3).withHour(14).withMinute(20),
                new BigDecimal("690000"), 180);

        // --- Đà Nẵng → TP.HCM ---
        createFlight("VN121", vna, "Đà Nẵng", "TP. Hồ Chí Minh",
                now.plusDays(5).withHour(18).withMinute(0),
                now.plusDays(5).withHour(19).withMinute(20),
                new BigDecimal("1050000"), 120);
        createFlight("VJ221", vj, "Đà Nẵng", "TP. Hồ Chí Minh",
                now.plusDays(7).withHour(7).withMinute(0),
                now.plusDays(7).withHour(8).withMinute(20),
                new BigDecimal("720000"), 180);

        // --- Hà Nội → Phú Quốc ---
        createFlight("VN130", vna, "Hà Nội", "Phú Quốc",
                now.plusDays(2).withHour(7).withMinute(0),
                now.plusDays(2).withHour(9).withMinute(30),
                new BigDecimal("1800000"), 100);
        createFlight("VJ230", vj, "Hà Nội", "Phú Quốc",
                now.plusDays(4).withHour(12).withMinute(0),
                now.plusDays(4).withHour(14).withMinute(30),
                new BigDecimal("1200000"), 180);

        // --- Phú Quốc → Hà Nội ---
        createFlight("VN131", vna, "Phú Quốc", "Hà Nội",
                now.plusDays(7).withHour(15).withMinute(0),
                now.plusDays(7).withHour(17).withMinute(30),
                new BigDecimal("1850000"), 100);

        // --- TP.HCM → Nha Trang ---
        createFlight("VJ240", vj, "TP. Hồ Chí Minh", "Nha Trang",
                now.plusDays(1).withHour(8).withMinute(0),
                now.plusDays(1).withHour(9).withMinute(0),
                new BigDecimal("590000"), 180);
        createFlight("VN140", vna, "TP. Hồ Chí Minh", "Nha Trang",
                now.plusDays(3).withHour(10).withMinute(30),
                now.plusDays(3).withHour(11).withMinute(30),
                new BigDecimal("850000"), 120);

        // --- Nha Trang → TP.HCM ---
        createFlight("VJ241", vj, "Nha Trang", "TP. Hồ Chí Minh",
                now.plusDays(5).withHour(16).withMinute(0),
                now.plusDays(5).withHour(17).withMinute(0),
                new BigDecimal("620000"), 180);

        // --- Hà Nội → Huế ---
        createFlight("VN150", vna, "Hà Nội", "Huế",
                now.plusDays(2).withHour(14).withMinute(0),
                now.plusDays(2).withHour(15).withMinute(10),
                new BigDecimal("900000"), 100);

        // --- Huế → Hà Nội ---
        createFlight("VN151", vna, "Huế", "Hà Nội",
                now.plusDays(6).withHour(8).withMinute(0),
                now.plusDays(6).withHour(9).withMinute(10),
                new BigDecimal("920000"), 100);

        // --- Quốc tế: TP.HCM → Singapore ---
        createFlight("SQ177", sq, "TP. Hồ Chí Minh", "Singapore",
                now.plusDays(3).withHour(9).withMinute(0),
                now.plusDays(3).withHour(12).withMinute(0),
                new BigDecimal("3500000"), 80);
        createFlight("VN500", vna, "TP. Hồ Chí Minh", "Singapore",
                now.plusDays(5).withHour(14).withMinute(0),
                now.plusDays(5).withHour(17).withMinute(0),
                new BigDecimal("3200000"), 90);

        // --- Singapore → TP.HCM ---
        createFlight("SQ178", sq, "Singapore", "TP. Hồ Chí Minh",
                now.plusDays(7).withHour(10).withMinute(0),
                now.plusDays(7).withHour(13).withMinute(0),
                new BigDecimal("3600000"), 80);

        // --- Hà Nội → Bangkok ---
        createFlight("TG561", tg, "Hà Nội", "Bangkok",
                now.plusDays(4).withHour(8).withMinute(0),
                now.plusDays(4).withHour(10).withMinute(30),
                new BigDecimal("2800000"), 70);
        createFlight("VJ600", vj, "Hà Nội", "Bangkok",
                now.plusDays(6).withHour(11).withMinute(0),
                now.plusDays(6).withHour(13).withMinute(30),
                new BigDecimal("2100000"), 180);

        // --- Bangkok → Hà Nội ---
        createFlight("TG562", tg, "Bangkok", "Hà Nội",
                now.plusDays(8).withHour(15).withMinute(0),
                now.plusDays(8).withHour(17).withMinute(30),
                new BigDecimal("2900000"), 70);

        // --- Thêm chuyến tuần sau ---
        createFlight("VN104", vna, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(7).withHour(6).withMinute(0),
                now.plusDays(7).withHour(8).withMinute(10),
                new BigDecimal("1550000"), 120);
        createFlight("VJ205", vj, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(8).withHour(7).withMinute(30),
                now.plusDays(8).withHour(9).withMinute(40),
                new BigDecimal("950000"), 180);
        createFlight("QH303", qh, "Hà Nội", "TP. Hồ Chí Minh",
                now.plusDays(9).withHour(10).withMinute(0),
                now.plusDays(9).withHour(12).withMinute(15),
                new BigDecimal("1150000"), 150);
        createFlight("VN105", vna, "TP. Hồ Chí Minh", "Hà Nội",
                now.plusDays(10).withHour(16).withMinute(0),
                now.plusDays(10).withHour(18).withMinute(10),
                new BigDecimal("1650000"), 110);

        log.info("=== IMPORT XONG: {} hãng bay, {} chuyến bay ===",
                airlineRepository.count(), flightRepository.count());
    }

    private Airline createAirline(String code, String name, String country) {
        Airline airline = new Airline();
        airline.setAirlineCode(code);
        airline.setAirlineName(name);
        airline.setCountry(country);
        airline.setStatus("ACTIVE");
        return airlineRepository.save(airline);
    }

    private void createFlight(String flightNumber, Airline airline,
                               String origin, String destination,
                               LocalDateTime departure, LocalDateTime arrival,
                               BigDecimal price, int seats) {
        Flight flight = new Flight();
        flight.setFlightNumber(flightNumber);
        flight.setAirline(airline);
        flight.setOrigin(origin);
        flight.setDestination(destination);
        flight.setDepartureTime(departure);
        flight.setArrivalTime(arrival);
        flight.setPrice(price);
        flight.setAvailableSeats(seats);
        flight.setStatus("AVAILABLE");
        flightRepository.save(flight);
    }
}
