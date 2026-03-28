package com.transport.service;

import com.transport.model.Trip;
import com.transport.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TripService {
    private final TripRepository tripRepository;

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public List<Trip> searchTrips(String from, String to, LocalDateTime date) {
        return tripRepository.findByDepartureCityIgnoreCaseAndDestinationCityIgnoreCaseAndDepartureTimeAfter(
            from, to, date
        );
    }

    public Trip getTripById(UUID id) {
        return tripRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Trajet non trouvé avec l'ID: " + id));
    }
}
