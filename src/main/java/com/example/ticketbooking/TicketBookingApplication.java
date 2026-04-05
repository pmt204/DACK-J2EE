package com.example.ticketbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Điểm khởi động của ứng dụng Spring Boot.
 * 
 * @SpringBootApplication = @Configuration + @EnableAutoConfiguration
 *                        + @ComponentScan
 */
@SpringBootApplication
@EnableAsync // Kích hoạt tính năng chạy ngầm (asynchronous) cho EmailService
@EnableScheduling
public class TicketBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(TicketBookingApplication.class, args);
    }
}
