package com.transport.service;

import com.transport.model.Booking;
import com.transport.model.BookingStatus;
import com.transport.model.Trip;
import com.transport.repository.BookingRepository;
import com.transport.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;

    @Transactional
    public Booking createBooking(UUID tripId, String passengerName, String passengerPhone, Integer seatNumber) {
        // Vérifie que le trajet existe
        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trajet non trouvé"));

        // Vérifie la disponibilité
        if (trip.getAvailableSeats() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Plus de places disponibles pour ce trajet");
        }

        // Mise à jour des places disponibles
        trip.setAvailableSeats(trip.getAvailableSeats() - 1);
        tripRepository.save(trip);

        // Création de la réservation
        Booking booking = Booking.builder()
                .trip(trip)
                .passengerName(passengerName)
                .passengerPhone(passengerPhone)
                .seatNumber(seatNumber)
                .bookingDate(LocalDateTime.now())
                .status(BookingStatus.CONFIRMED)
                .build();

        return bookingRepository.save(booking);
    }

    public List<Booking> getBookingsByPhone(String phone) {
        return bookingRepository.findByPassengerPhone(phone);
    }

    public Booking getBookingById(UUID id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Réservation non trouvée"));
    }
}