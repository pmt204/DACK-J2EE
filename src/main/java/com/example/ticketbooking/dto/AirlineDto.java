package com.example.ticketbooking.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AirlineDto {

    private Long id;

    @NotBlank(message = "Mã hãng bay không được để trống")
    @Size(max = 10, message = "Mã hãng bay tối đa 10 ký tự")
    private String airlineCode;

    @NotBlank(message = "Tên hãng bay không được để trống")
    @Size(max = 100, message = "Tên hãng bay tối đa 100 ký tự")
    private String airlineName;

    @Size(max = 100)
    private String country;

    @Size(max = 255)
    private String logoUrl;

    private String status = "ACTIVE";
}
