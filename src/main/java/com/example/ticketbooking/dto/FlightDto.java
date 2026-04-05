package com.example.ticketbooking.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO cho form thêm/sửa chuyến bay (dành cho ADMIN).
 * Dùng airlineId thay vì chuỗi airline.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightDto {

    private Long id;

    @NotBlank(message = "Số hiệu chuyến bay không được để trống")
    @Size(max = 20, message = "Số hiệu tối đa 20 ký tự")
    private String flightNumber;

    /** ID hãng bay (liên kết bảng airlines) */
    @NotNull(message = "Vui lòng chọn hãng hàng không")
    private Long airlineId;

    @NotBlank(message = "Điểm khởi hành không được để trống")
    @Size(max = 100)
    private String origin;

    @NotBlank(message = "Điểm đến không được để trống")
    @Size(max = 100)
    private String destination;

    @NotNull(message = "Thời gian khởi hành không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime departureTime;

    @NotNull(message = "Thời gian đến không được để trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime arrivalTime;

    @NotNull(message = "Giá vé không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá vé phải lớn hơn 0")
    private BigDecimal price;

    @NotNull(message = "Số ghế không được để trống")
    @Min(value = 0, message = "Số ghế không được âm")
    private Integer availableSeats;

    private String description;

    private String status = "AVAILABLE";
}
