package com.example.bookingservice.dto;

import lombok.Data;

@Data
public class BookingRequest {
    private Long hotelId;
    private Long roomId;
}