package com.example.bookingservice.service;

import com.example.bookingservice.dto.BookingRequest;
import com.example.bookingservice.model.Booking;
import com.example.bookingservice.repository.BookingRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final RestClient hotelServiceClient;

    public Booking createBooking(Long userId, BookingRequest request) {
        String bookingCorrelationId = UUID.randomUUID().toString();

        // 1. Создаем бронь в статусе PENDING
        Booking booking = Booking.builder()
                .bookingId(bookingCorrelationId)
                .userId(userId)
                .hotelId(request.getHotelId())
                .roomId(request.getRoomId())
                .status(Booking.BookingStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        booking = bookingRepository.save(booking);
        log.info("Booking created with PENDING status. CorrelationId: {}", bookingCorrelationId);

        try {
            // 2. Шаг Saga: Запрос к Hotel Service на подтверждение и блокировку номера
            Boolean isLocked = hotelServiceClient.post()
                    .uri("/api/rooms/{roomId}/confirm-availability", request.getRoomId())
                    .retrieve()
                    .body(Boolean.class);

            if (Boolean.TRUE.equals(isLocked)) {
                // Успех: переводим в CONFIRMED
                booking.setStatus(Booking.BookingStatus.CONFIRMED);
                bookingRepository.save(booking);
                log.info("Booking confirmed successfully. CorrelationId: {}", bookingCorrelationId);
            } else {
                // Отказ отеля (номер занят): компенсация
                compensateBooking(booking, "Room is not available");
            }

        } catch (Exception e) {
            // Ошибка сети / таймаут / падение сервиса отелей: компенсация
            log.error("Error communicating with Hotel Service. Initiating compensation. CorrelationId: {}", bookingCorrelationId, e);
            compensateBooking(booking, "Hotel service error: " + e.getMessage());
        }

        return booking;
    }

    private void compensateBooking(Booking booking, String reason) {
        log.warn("Executing Saga compensation (CANCELLED) for booking: {}. Reason: {}", booking.getBookingId(), reason);

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        // Пытаемся освободить номер в Hotel Service (если он успел заблокироваться)
        try {
            hotelServiceClient.post()
                    .uri("/api/rooms/{roomId}/release", booking.getRoomId())
                    .retrieve()
                    .toBodilessEntity();
            log.info("Room successfully released during compensation. RoomId: {}", booking.getRoomId());
        } catch (Exception ex) {
            log.error("Failed to release room during compensation! RoomId: {}", booking.getRoomId(), ex);
        }
    }
}