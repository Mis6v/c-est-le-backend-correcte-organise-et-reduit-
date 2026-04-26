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
    public Booking createBooking(UUID tripId,
                                 String passengerName,
                                 String passengerPhone,
                                 List<Integer> seatNumbers) {

        Trip trip = tripRepository.findById(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trajet non trouvé"));

        if (trip.getAvailableSeats() < seatNumbers.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Pas assez de places disponibles");
        }

        // update seats correctly
        trip.setAvailableSeats(trip.getAvailableSeats() - seatNumbers.size());
        tripRepository.save(trip);

        Booking booking = Booking.builder()
                .trip(trip)
                .passengerName(passengerName)
                .passengerPhone(passengerPhone)
                .seatNumbers(seatNumbers)
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