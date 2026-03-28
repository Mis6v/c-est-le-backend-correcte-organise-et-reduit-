package com.transport.controller;

import com.transport.dto.BookingRequest;
import com.transport.model.Booking;
import com.transport.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/bookings")
@RequiredArgsConstructor
@Tag(name = "Réservations", description = "API pour la gestion des réservations de billets")
@CrossOrigin(origins = "*")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    @Operation(summary = "Créer une nouvelle réservation")
    public ResponseEntity<Booking> createBooking(@RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(
            request.getTripId(),
            request.getPassengerName(),
            request.getPassengerPhone(),
            request.getSeatNumber()
        );
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/user/{phone}")
    @Operation(summary = "Récupérer les réservations d'un utilisateur par téléphone")
    public List<Booking> getBookingsByPhone(@PathVariable String phone) {
        return bookingService.getBookingsByPhone(phone);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer les détails d'une réservation par ID")
    public ResponseEntity<Booking> getBookingById(@PathVariable UUID id) {
        return ResponseEntity.ok(bookingService.getBookingById(id));
    }
}
