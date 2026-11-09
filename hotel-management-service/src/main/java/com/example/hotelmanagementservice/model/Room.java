package com.example.hotelmanagementservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String roomNumber;

    @Column(nullable = false)
    private String type; // Например: SINGLE, DOUBLE, DELUXE, SUITE

    @Column(nullable = false)
    private Double price;

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Column(nullable = false)
    @Builder.Default
    private Long times_booked = 0L; // Для сортировки и равномерного распределения нагрузки

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    @JsonIgnore
    private Hotel hotel;
}