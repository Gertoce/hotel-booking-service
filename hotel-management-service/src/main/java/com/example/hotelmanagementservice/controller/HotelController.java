package com.example.hotelmanagementservice.controller;

import com.example.hotelmanagementservice.model.Hotel;
import com.example.hotelmanagementservice.model.Room;
import com.example.hotelmanagementservice.service.HotelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    // --- Эндпоинты для отелей ---
    @GetMapping("/hotels")
    public List<Hotel> getAllHotels() {
        return hotelService.getAllHotels();
    }

    @PostMapping("/hotels")
    public ResponseEntity<Hotel> createHotel(@RequestBody Hotel hotel) {
        Hotel created = hotelService.createHotel(hotel);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // --- Эндпоинты для номеров ---
    @PostMapping("/hotels/{hotelId}/rooms")
    public ResponseEntity<Room> addRoom(@PathVariable Long hotelId, @RequestBody Room room) {
        Room createdRoom = hotelService.addRoomToHotel(hotelId, room);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRoom);
    }

    // Получение доступных номеров с сортировкой по популярности (times_booked)
    @GetMapping("/rooms")
    public List<Room> getAvailableRooms(@RequestParam(required = false) Long hotelId) {
        return hotelService.getAvailableRooms(hotelId);
    }

    // --- Внутренние эндпоинты для межсервисного взаимодействия (Saga / Booking Service) ---
    @PostMapping("/rooms/{roomId}/confirm-availability")
    public ResponseEntity<Boolean> confirmAvailability(@PathVariable Long roomId) {
        boolean success = hotelService.confirmAndLockRoom(roomId);
        return ResponseEntity.ok(success);
    }

    @PostMapping("/rooms/{roomId}/release")
    public ResponseEntity<Void> releaseRoom(@PathVariable Long roomId) {
        hotelService.releaseRoom(roomId);
        return ResponseEntity.ok().build();
    }
}