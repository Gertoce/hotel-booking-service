package com.example.bookingservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bookingId; // Уникальный идентификатор для Saga / идемпотентности (UUID)

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long hotelId;

    @Column(nullable = false)
    private Long roomId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status; // PENDING, CONFIRMED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public enum BookingStatus {
        PENDING,
        CONFIRMED,
        CANCELLED
    }
}