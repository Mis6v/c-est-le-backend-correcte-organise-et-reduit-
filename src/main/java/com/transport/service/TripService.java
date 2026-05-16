package com.transport.service;

import com.transport.dto.TripLocationUpdateRequest;
import com.transport.dto.TripTrackingResponse;
import com.transport.model.Trip;
import com.transport.model.TripStatus;
import com.transport.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
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

    public TripTrackingResponse getTrackingByTripNumber(String tripNumber) {
        Trip trip = tripRepository.findByTripNumberIgnoreCase(tripNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trajet non trouvé"));

        return toTrackingResponse(trip);
    }

    @Transactional
    public TripTrackingResponse updateLocation(String tripNumber, TripLocationUpdateRequest request) {
        Trip trip = tripRepository.findByTripNumberIgnoreCase(tripNumber)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trajet non trouvé"));

        if (request.getCurrentLatitude() == null || request.getCurrentLongitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Latitude et longitude sont obligatoires");
        }

        trip.setCurrentLatitude(request.getCurrentLatitude());
        trip.setCurrentLongitude(request.getCurrentLongitude());
        trip.setLastLocationUpdate(LocalDateTime.now());

        if (request.getProgressPercentage() != null) {
            double progress = Math.max(0, Math.min(100, request.getProgressPercentage()));
            trip.setProgressPercentage(progress);
            trip.setStatus(progress >= 100 ? TripStatus.ARRIVED : TripStatus.IN_PROGRESS);
        } else if (trip.getStatus() == TripStatus.SCHEDULED) {
            trip.setStatus(TripStatus.IN_PROGRESS);
        }

        return toTrackingResponse(tripRepository.save(trip));
    }

    private TripTrackingResponse toTrackingResponse(Trip trip) {
        return TripTrackingResponse.builder()
                .tripId(trip.getId())
                .tripNumber(trip.getTripNumber())
                .departureCity(trip.getDepartureCity())
                .destinationCity(trip.getDestinationCity())
                .departureTime(trip.getDepartureTime())
                .arrivalTime(trip.getArrivalTime())
                .status(trip.getStatus())
                .progressPercentage(trip.getProgressPercentage())
                .departureLatitude(trip.getDepartureLatitude())
                .departureLongitude(trip.getDepartureLongitude())
                .destinationLatitude(trip.getDestinationLatitude())
                .destinationLongitude(trip.getDestinationLongitude())
                .currentLatitude(trip.getCurrentLatitude())
                .currentLongitude(trip.getCurrentLongitude())
                .lastLocationUpdate(trip.getLastLocationUpdate())
                .companyName(trip.getCompanyName())
                .driverName(trip.getDriver() != null ? trip.getDriver().getFullName() : null)
                .driverPhone(trip.getDriver() != null ? trip.getDriver().getPhone() : null)
                .vehicleName(trip.getDriver() != null ? trip.getDriver().getVehicleName() : null)
                .vehiclePlate(trip.getDriver() != null ? trip.getDriver().getVehiclePlate() : null)
                .build();
    }
}
