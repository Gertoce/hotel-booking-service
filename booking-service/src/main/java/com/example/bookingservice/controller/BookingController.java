package com.example.bookingservice.controller;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.model.User;
import com.example.bookingservice.repository.UserRepository;
import com.example.bookingservice.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<Booking> createBooking(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody BookingRequest request) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Booking booking = bookingService.createBooking(user.getId(), request);
        return ResponseEntity.ok(booking);
    }
}