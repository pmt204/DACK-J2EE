package com.example.ticketbooking.config;

import com.example.ticketbooking.entitimport com.example.ticketbooking.repository.*;
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

ate final AirlineRepository airlineRepositorate final FlightRepository flightRepository;private final PasswordEncoder passwordEncoder;@Override

    initR     initUsers();i

pository.sv

 

        User admin = newU   admin.setUsername("admin");admin.setPsword(passwoadmin.setEmail("admin@tickeadmin.setEnabled(true);Set<Role> adminRoles = new HashSet<>();roleRepository.findByNaadmin.setoles(adminoes);userRepository.save(admin);log.info(">>> Tài khoản ADMi

   user.setUsername("user1");user.setPsword(passwouser.setEmail("user1@exampuser.setEnabled(true);Set<Role> userRoles = new HashSet<>roleRepository.findByNuser.setRles(userRls);userRepository.save(user);

        log.info("Hãng bay đã tồn tại    r

// ===== 1. Tạo hãng bay =====

Airline vj  = createAirline("V
Airline qh =createAirline("QH","Bamboo Airways", "iệt Nam");Airline bl=createAirline("BL","Pacific Airlies", "Việt NAirline vu=createAirline("VU","Vietravel Airlins", "Việt NaAirline sq=createAirline("SQ","Singapore Airlines, "SingaporeAirline tg=createAirline("TG","Thai Airways", "TháiLan");// ===== 2To chuyến bay — nhiề ngày khác nhau=====


// --- Hà Nội → T.CM (nhiều hãng, nhiề

        now.plusDays(1).withHour(6).withMinute(0),
        now.plusDays().wihHour(8).ithMinute(10),new BigDecimal("1500000"), 120);ight("VN102", vna, "Hà Nội", "TP. Hồ Chí Minow.plusDays(1).withHour(1).wit        now.plusDays().wihHour(16)withMinute(40),new BigDecimal("1600000"), 100);ight("VJ201", vj, "Hà Nội", "TP. Hồ Chí Minhnow.plusDays(2).withHour(7.with        now.plusDays().wthHour(9)withMinute(15),new BigDecimal("990000"), 180);ight("VJ203", vj, "Hà Nội", "TP. Hồ Chí Minnow.plusDays(2).withHour(9).wi        now.plusDays().wthHour(21.withMinute(10),new BigDecimal("890000"), 180);ight("QH301", qh, "Hà Nội", "TP. Hồ Chí Minhnow.plusDays(3).withHour().wit        now.plusDays().wthHour(10.withMinute(45),new BigDecimal("1200000"), 150);ight("BL401", bl, "Hà Nội", "TP. Hồ Chí Minhnow.plusDays(4).withHour(1).wit        now.plusDays().wthHour(12.withMinute(15),new BigDecimal("850000"), 140);ight("VU501", vu, "Hà Nội", "TP. Hồ Chí Minhnow.plusDays(5).withHour().wit        now.plusDays().wthHour(11.withMinute(10),new BigDecimal("950000"), 130);

        now.plusDays(3).withHour(17).withMinute(0),
        now.plusDays().wihHour(19).withMinue(10),new BigDecimal("1500000"), 120);ight("VJ202", vj, "TP. Hồ Chí Minh", "Hà Nộinow.plusDays(4).withHour(6.with        now.plusDays().wthHour(8).withMinue(45),new BigDecimal("990000"), 180);ight("QH302", qh, "TP. Hồ Chí Minh", "Hà Nộnow.plusDays(5).withHour(4).wi        now.plusDays().wthHour(16).withMinte(15),new BigDecimal("1250000"), 150);ight("VN103", vna, "TP. Hồ Chí Minh", "Hà Nộnow.plusDays(6).withHour(2).wit        now.plusDays().wihHour(22).withMinue(10),new BigDecimal("1700000"), 100);à Nội → Đà Nẵng ---

        now.plusDays(1).wit
        now.plusDays().wihHour(9).ithMinute(new BigDecimal("1100000"), 130);ight("VJ210", vj, "Hà Nội", "Đà Nẵng",now.plusDays(2).withHour(1).wit        now.plusDays().wthHour(12.withMinutnew BigDecimal("750000"), 180);ight("QH310", qh, "Hà Nội", "Đà Nẵng",now.plusDays(3).withHour(5).wi        now.plusDays().wthHour(16.withMinutnew BigDecimal("1050000"), 150);à Nẵng → Hà Nội ---

        now.plusDays(5).wit
        now.plusDays().wihHour(11).ithMinutenew BigDecimal("1100000"), 130);ight("VJ211", vj, "Đà Nẵng", "Hà Nội",now.plusDays(6).withHour(1).wit        now.plusDays().wthHour(17)withMinutnew BigDecimal("780000"), 180);P.HCM → Đà Nẵng ---

        now.plusDays(1).wit
        now.plusDays().wihHour(10).withMinue(50),new BigDecimal("1050000"), 120);ight("VJ220", vj, "TP. Hồ Chí Minh", "Đà Nẵnnow.plusDays(3).withHour(1).wit        now.plusDays().wthHour(14).withMinte(20),new BigDecimal("690000"), 180);à Nẵng → TP.HCM ---

        now.plusDays(5).wit
        now.plusDays().wihHour(19).ithMinute(20),new BigDecimal("1050000"), 120);ight("VJ221", vj, "Đà Nẵng", "TP. Hồ Chí Minnow.plusDays(7).withHour(7.with        now.plusDays().wthHour(8).ithMinute(20),new BigDecimal("720000"), 180);à Nội → Phú Quốc ---

        now.plusDays(2).with
        now.plusDays().wihHour(9).ithMinute(3new BigDecimal("1800000"), 100);ight("VJ230", vj, "Hà Nội", "Phú Quốc",now.plusDays(4).withHour(1).wit        now.plusDays().wthHour(14.withMinutenew BigDecimal("1200000"), 180);hú Quốc → Hà Nội ---

        now.plusDays(7).with
        now.plusDays().wihHour(17).wthMinute(new BigDecimal("1850000"), 100);P.HCM → Nha Trang ---

        now.plusDays(1).withH
        now.plusDays().wthHour(9).withMinue(0),new BigDecimal("590000"), 180);ight("VN140", vna, "TP. Hồ Chí Minh", "Nhanow.plusDays(3).withHour(0).wi        now.plusDays().wihHour(11).withMinue(30),new BigDecimal("850000"), 120);ha Trang → TP.HCM ---

        now.plusDays(5).withH
        now.plusDays().wthHour(17).wthMinute(0),new BigDecimal("620000"), 180);à Nội → Huế ---

        now.plusDays(2)
        now.plusDays().wihHour(15)withMinew BigDecimal("900000"), 100);uế → Hà Nội ---

        now.plusDays(6)
        now.plusDays().wihHour().withMinnew BigDecimal("920000"), 100);uốc tế: TP.HCM → Singapor ---

        now.plusDays(3).withHour(9).wi
        now.plusDays().wthHour(12).withMinte(0),new BigDecimal("3500000"), 80);ight("VN500", vna, "TP. Hồ Chí Minh", "Singnow.plusDays(5).withHour(1).wi        now.plusDays().wihHour(17).withMinue(0),new BigDecimal("3200000"), 90);ingapore → TP.HCM ---

        now.plusDays(7).withH
        now.plusDays().wthHour(13).wthMinute(0),new BigDecimal("3600000"), 80);à Nội → Bangkok ---

        now.plusDays(4).wit
        now.plusDays().wthHour(10.withMinutnew BigDecimal("2800000"), 70);ight("VJ600", vj, "Hà Nội", "Bangkok",now.plusDays(6).withHour(1).wi        now.plusDays().wthHour(13.withMinutnew BigDecimal("2100000"), 180);angkok → Hà Nội ---

        now.plusDays(8).wit
        now.plusDays().wthHour(17)withMinutnew BigDecimal("2900000"), 70);hêm chuyến tuần sau ---

        now.plusDays(7).withHou
        now.plusDays().wihHour(8).ithMinute(10),new BigDecimal("1550000"), 120);ight("VJ205", vj, "Hà Nội", "TP. Hồ Chí Minnow.plusDays(8).withHour(7.with        now.plusDays().wthHour(9)withMinute(40),new BigDecimal("950000"), 180);ight("QH303", qh, "Hà Nội", "TP. Hồ Chí Minnow.plusDays(9).withHour(0).wi        now.plusDays().wthHour(12.withMinute(15),new BigDecimal("1150000"), 150);ight("VN105", vna, "TP. Hồ Chí Minh", "Hà Nộnow.plusDays(10).withHour(6).wi        now.plusDays(0).wthHour(18).withMinte(10),new BigDecimal("1650000"), 110);("=== IMPORT XONG: {} hãngbay, 



    airline.setAirlineCode(code);airline.setAirlnName(name);airline.setCountry(country);airline.setStatus("ACTIVE");return airlineRepository.sav

 
