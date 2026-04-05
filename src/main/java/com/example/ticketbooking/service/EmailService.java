package com.example.ticketbooking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Async // Chạy ngầm, không block luồng chính
    public void sendBookingConfirmationEmail(String toEmail, String passengerName, String flightNumber) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your-email@gmail.com");
        message.setTo(toEmail);
        message.setSubject("Xác nhận đặt vé máy bay thành công");
        message.setText("Xin chào " + passengerName + ",\n\n"
                + "Bạn đã đặt vé thành công cho chuyến bay: " + flightNumber + ".\n"
                + "Vui lòng thanh toán sớm để giữ chỗ.\n\n"
                + "Cảm ơn bạn đã sử dụng dịch vụ!");

        mailSender.send(message);
    }
}