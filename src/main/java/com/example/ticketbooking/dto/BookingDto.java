package com.example.ticketbooking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

/**
 * DTO cho form đặt vé máy bay.
 * Hỗ trợ vé một chiều và khứ hồi.
 */
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingDto {

    private Long flightId;

    /** ID chuyến bay về (chỉ khi khứ hồi) */
    private Long returnFlightId;

    @NotBlank(message = "Loại vé không được để trống")
    private String bookingType = "FLIGHT";

    /** Loại hành trình: ONE_WAY hoặc ROUND_TRIP */
    private String tripType = "ONE_WAY";

    @NotBlank(message = "Tên hành khách không được để trống")
    @Size(max = 100, message = "Tên tối đa 100 ký tự")
    private String passengerName;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không hợp lệ")
    private String passengerEmail;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "Số điện thoại không hợp lệ (9-11 chữ số)")
    private String passengerPhone;

    @NotNull(message = "Số ghế không được để trống")
    @Min(value = 1, message = "Phải đặt ít nhất 1 ghế")
    @Max(value = 10, message = "Tối đa 10 ghế mỗi lần đặt")
    private Integer seatCount = 1;

    /** Hạng ghế: ECONOMY hoặc BUSINESS */
    @NotBlank(message = "Vui lòng chọn hạng ghế")
    private String seatClass = "ECONOMY";

    /** Phương thức thanh toán */
    @NotBlank(message = "Vui lòng chọn phương thức thanh toán")
    private String paymentMethod = "BANK_TRANSFER";
}
