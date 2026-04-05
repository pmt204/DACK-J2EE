packagecom. import com.example.ticketbooking.entity.*;
import com.example.ticketbooking.repository.*;
import lombok.RequiredArgsConstructor;
import ombok.extern.

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

        private final PasswordEncoder passwordEncode
        
        @Override
        public void run(String... args) {
            initRoles();

            initA
        }
                
                ate void ini
                if (roleRepository.count(
         

                log.info(">>> Đã k
                }
                        
                        
                        void initUsers() {
                i
         

                admin.setPassword(
                    admin.setEmail("admin@ticketbooking.vn");
                        admin.setEnabled(true);
                        Set<Role> adminRoles = new 
                        roleRepository.findByName("ROLE_ADMIN").ifPresent(admi
                        admin.setRoles(adminRoles);
                        userRepository.save(adm
                        log.info(">>> Tài khoản ADMIN: username
                        
                        
                        !userRepository.existsByUse
                        User user = new User();
                 

                    user.setEmail("user1@example.com");
                        user.setEnabled(true);
                        Set<Role> userRoles = new 
                        roleRepository.findByName("ROLE_USER").ifPresent(use
                        user.setRoles(userRoles);
                        userRepository.save(us
                        log.info(">>> Tài khoản USER: username
                        
                        
                        
                        void initAirlinesAndFlights() {
                i
         

            }
                
                        info("=== BẮT ĐẦU IMPORT DỮ LIỆU MẪU ===");
                        
                /

                Airline vj  = createAirline("VJ", "Vietjet Air"

                Airline bl  = createAirline("B
                Airline vu  = createAirline("VU", "Vietravel Airlines", "Việt Nam"
                Airline sq = createAirline("SQ", "Singapore Airlines", "Sing
                Airline tg = createAirline("TG", "Thai Airways", "Thái Lan");
                 
                // ===== 2 Tạo chuyến bay — nhiều ngày khác nhau =====
                LocalDateT e now = LocalDateTime.now();
                 

                createFlight("VN100", vna, "Hà Nội", "TP. Hồ Chí Minh",
                        now.plusDays(1).withHour(6).with

                        new BigDecimal("1500000"), 120);
                createFlight("VN102", vna, "Hà Nội", "TP. Hồ Chí Minh",
                                now.plusDays(1).withHour(14).withMinute(30
                                now.plusDays(1).withHour(16).withMinute(40)
                                new BigDecimal("1600000"), 100);
                createFlight("VJ201", vj, "Hà Nội", "TP. Hồ Chí Minh",
                                now.plusDays(2).withHour(7).withMinute(0),
                                now.plusDays(2).withHour(9).withMinute(15),
                                new BigDecimal("990000"), 180);
                createFlight("VJ203", vj, "Hà Nội", "TP. Hồ Chí Minh",
                                now.plusDays(2).withHour(19).withMinute(0)
                                now.plusDays(2).withHour(21).withMinute(10)
                                new BigDecimal("890000"), 180);
                createFlight("QH301", qh, "Hà Nội", "TP. Hồ Chí Minh",
                                now.plusDays(3).withHour(8).withMinute(30),
                                now.plusDays(3).withHour(10).withMinute(45),
                                new BigDecimal("1200000"), 150)
                createFlight("BL401", bl, "Hà Nội", "TP. Hồ Chí Minh",
                                now.plusDays(4).withHour(10).withMinute(0),
                                now.plusDays(4).withHour(12).withMinute(15),
                                new BigDecimal("850000"), 140);
                createFlight("VU501", vu, "Hà Nội", "TP. Hồ Chí Minh",
                                now.plusDays(5).withHour(9).withMinute(0),
                                now.plusDays(5).withHour(11).withMinute(10),
                                new BigDecimal("950000"), 130);
                
                                P.HCM → Hà Nội (chiều ngược - cho khứ hồi)
                                ight("VN101", vna, "TP. Hồ Chí Minh", "Hà Nộ
                                now.plusDays(3).withHour(17).wi

                        new BigDecimal("1500000"), 120);
                createFlight("VJ202", vj, "TP. Hồ Chí Minh", "Hà Nội",
                                now.plusDays(4).withHour(6).withMinute(30),
                                now.plusDays(4).withHour(8).withMinute(45),
                                new BigDecimal("990000"), 180);
                createFlight("QH302", qh, "TP. Hồ Chí Minh", "Hà Nội",
                                now.plusDays(5).withHour(14).withMinute(0),
                                now.plusDays(5).withHour(16).withMinute(15)
                                new BigDecimal("1250000"), 150)
                createFlight("VN103", vna, "TP. Hồ Chí Minh", "Hà Nội"
                                now.plusDays(6).withHour(20).withMinute(0),
                                now.plusDays(6).withHour(22).withMinute(10),
                                new BigDecimal("1700000"), 100);
                
                                à Nội → Đà Nẵng ---
                                ight("VN110", vna, "Hà Nội", "Đà Nẵng",
                                now.plusDays(1).withHour(8).with

                        new BigDecimal("110
                createFlight("VJ210", vj, "Hà Nội", "Đà Nẵng",
                                now.plusDays(2).withHour(11).withMinute(30
                                now.plusDays(2).withHour(12).withMinute(50)
                                new BigDecimal("750000"), 180);
                createFlight("QH310", qh, "Hà Nội", "Đà Nẵng",
                                now.plusDays(3).withHour(15).withMinute(0),
                                now.plusDays(3).withHour(16).withMinute(20),
                                new BigDecimal("1050000"), 150)
                
                                à Nẵng → Hà Nội ---
                                ight("VN111", vna, "Đà Nẵng", "Hà Nội",
                                now.plusDays(5).withHour(10).wit

                        new BigDecimal("110
                createFlight("VJ211", vj, "Đà Nẵng", "Hà Nội",
                                now.plusDays(6).withHour(16).withMinute(0),
                                now.plusDays(6).withHour(17).withMinute(20),
                                new BigDecimal("780000"), 180);
                
                                P.HCM → Đà Nẵng ---
                                ight("VN120", vna, "TP. Hồ Chí Minh", "Đà Nẵ
                                now.plusDays(1).withHour(9).wit

                        new BigDecimal("105
                createFlight("VJ220", vj, "TP. Hồ Chí Minh", "Đà Nẵng",
                                now.plusDays(3).withHour(13).withMinute(0),
                                now.plusDays(3).withHour(14).withMinute(20),
                                new BigDecimal("690000"), 180);
                
                                à Nẵng → TP.HCM ---
                                ight("VN121", vna, "Đà Nẵng", "TP. Hồ Chí Mi
                                now.plusDays(5).withHour(18).wi

                        new BigDecimal("105
                createFlight("VJ221", vj, "Đà Nẵng", "TP. Hồ Chí Minh",
                                now.plusDays(7).withHour(7).withMinute(0),
                                now.plusDays(7).withHour(8).withMinute(20),
                                new BigDecimal("720000"), 180);
                
                                à Nội → Phú Quốc ---
                                ight("VN130", vna, "Hà Nội", "Phú Quốc",
                                now.plusDays(2).withHour(7).wit

                        new BigDecimal("1800
                createFlight("VJ230", vj, "Hà Nội", "Phú Quốc",
                                now.plusDays(4).withHour(12).withMinute(0)
                                now.plusDays(4).withHour(14).withMinute(30)
                                new BigDecimal("1200000"), 180);
                
                                hú Quốc → Hà Nội ---
                                ight("VN131", vna, "Phú Quốc", "Hà Nội",
                                now.plusDays(7).withHour(15).wit

                        new BigDecimal("1850
                
                                P.HCM → Nha Trang ---
                                ight("VJ240", vj, "TP. Hồ Chí Minh", "Nha Tr
                                now.plusDays(1).withHour(8).with

                        new BigDecimal("59000
                createFlight("VN140", vna, "TP. Hồ Chí Minh", "Nha Trang"
                                now.plusDays(3).withHour(10).withMinute(30
                                now.plusDays(3).withHour(11).withMinute(30
                                new BigDecimal("850000"), 120);
                
                                ha Trang → TP.HCM ---
                                ight("VJ241", vj, "Nha Trang", "TP. Hồ Chí M
                                now.plusDays(5).withHour(16).wi

                        new BigDecimal("62000
                
                                à Nội → Huế ---
                                ight("VN150", vna, "Hà Nội", "Huế",
                                now.plusDays(2).withHour(14).wi

                        new BigDecimal(
                
                                uế → Hà Nội ---
                                ight("VN151", vna, "Huế", "Hà Nội",
                                now.plusDays(6).withHour(8).wit

                        new BigDecimal(
                
                                uốc tế: TP.HCM → Singapore ---
                                ight("SQ177", sq, "TP. Hồ Chí Minh", "Singa
                                now.plusDays(3).withHour(9).wit

                        new BigDecimal("3500000"), 80)
                createFlight("VN500", vna, "TP. Hồ Chí Minh", "Singapore"
                                now.plusDays(5).withHour(14).withMinute(0)
                                now.plusDays(5).withHour(17).withMinute(0),
                                new BigDecimal("3200000"), 90);
                
                                ingapore → TP.HCM ---
                                ight("SQ178", sq, "Singapore", "TP. Hồ Chí 
                                now.plusDays(7).withHour(10).wi

                        new BigDecimal("36000
                
                                à Nội → Bangkok ---
                                ight("TG561", tg, "Hà Nội", "Bangkok",
                                now.plusDays(4).withHour(8).wit

                        new BigDecimal("280
                createFlight("VJ600", vj, "Hà Nội", "Bangkok",
                                now.plusDays(6).withHour(11).withMinute(0)
                                now.plusDays(6).withHour(13).withMinute(30),
                                new BigDecimal("2100000"), 180)
                
                                angkok → Hà Nội ---
                                ight("TG562", tg, "Bangkok", "Hà Nội",
                                now.plusDays(8).withHour(15).wit

                        new BigDecimal("290
                
                                hêm chuyến tuần sau ---
                                ight("VN104", vna, "Hà Nội", "TP. Hồ Chí Min
                                now.plusDays(7).withHour(6).wit

                        new BigDecimal("1550000
                createFlight("VJ205", vj, "Hà Nội", "TP. Hồ Chí Minh",
                                now.plusDays(8).withHour(7).withMinute(30)
                                now.plusDays(8).withHour(9).withMinute(40),
                                new BigDecimal("950000"), 180);
                createFlight("QH303", qh, "Hà Nội", "TP. Hồ Chí Minh",
                                now.plusDays(9).withHour(10).withMinute(0),
                                now.plusDays(9).withHour(12).withMinute(15)
                                new BigDecimal("1150000"), 150)
                createFlight("VN105", vna, "TP. Hồ Chí Minh", "Hà Nội"
                                now.plusDays(10).withHour(16).withMinute(0)
                                now.plusDays(10).withHour(18).withMinute(10)
                                new BigDecimal("1650000"), 110);
                
                                ("=== IMPORT XONG: {} hãng bay, {} chuyến ba
                                airlineRepository.count(), flightRepository.c
                                

                ate Airline createAirline(String code, String name, String 
                                airline = new Airline();
         

            airline.setCountry(country);
                airline.setStatus("ACTIVE");
                return airlineRepository.save
                
                
                ate void createFlight(String
                                       String origin, S
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


