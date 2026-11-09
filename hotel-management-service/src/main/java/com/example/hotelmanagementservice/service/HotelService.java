package com.example.hotelmanagementservice.service;

import com.example.hotelmanagementservice.model.Hotel;
import com.example.hotelmanagementservice.model.Room;
import com.example.hotelmanagementservice.repository.HotelRepository;
import com.example.hotelmanagementservice.repository.RoomRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    // --- CRUD Отелей ---
    public List<Hotel> getAllHotels() {
        return hotelRepository.findAll();
    }

    public Hotel getHotelById(Long id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hotel not found with id: " + id));
    }

    public Hotel createHotel(Hotel hotel) {
        return hotelRepository.save(hotel);
    }

    // --- CRUD Номеров ---
    public Room addRoomToHotel(Long hotelId, Room room) {
        Hotel hotel = getHotelById(hotelId);
        room.setHotel(hotel);
        return roomRepository.save(room);
    }

    public List<Room> getAvailableRooms(Long hotelId) {
        if (hotelId != null) {
            return roomRepository.findAvailableRoomsByHotelSortedByPopularity(hotelId);
        }
        return roomRepository.findByAvailableTrueOrderByTimesBookedAsc();
    }

    // --- Логика для Saga / Бронирования (Шаг 4) ---
    @Transactional
    public boolean confirmAndLockRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + roomId));

        if (!room.getAvailable()) {
            return false; // Номер уже занят
        }

        room.setAvailable(false);
        room.setTimes_booked(room.getTimes_booked() + 1);
        roomRepository.save(room);
        return true;
    }

    @Transactional
    public void releaseRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room not found with id: " + roomId));
        room.setAvailable(true);
        roomRepository.save(room);
    }
}