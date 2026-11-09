package com.example.hotelmanagementservice.repository;

import com.example.hotelmanagementservice.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    // Найти все доступные номера в конкретном отеле, отсортированные по наименьшему числу броней
    @Query("SELECT r FROM Room r WHERE r.hotel.id = :hotelId AND r.available = true ORDER BY r.times_booked ASC")
    List<Room> findAvailableRoomsByHotelSortedByPopularity(@Param("hotelId") Long hotelId);

    // Общий метод для получения всех свободных номеров по системе (для рекомендаций)
    List<Room> findByAvailableTrueOrderByTimesBookedAsc();
}