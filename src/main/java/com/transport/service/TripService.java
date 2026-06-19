package com.transport.service;

import com.transport.dto.TripLocationUpdateRequest;
import com.transport.dto.TripRequest;
import com.transport.dto.TripTrackingResponse;
import com.transport.model.Driver;
import com.transport.model.Trip;
import com.transport.model.TripStatus;
import com.transport.repository.BookingRepository;
import com.transport.repository.DriverRepository;
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
    private final DriverRepository driverRepository;
    private final BookingRepository bookingRepository;

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
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Trajet non trouvé avec l'ID: " + id));
    }

    public Trip createTrip(TripRequest request) {
        validateTripRequest(request);

        tripRepository.findByTripNumberIgnoreCase(request.getTripNumber())
                .ifPresent(existingTrip -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce numéro de trajet est déjà utilisé");
                });

        Trip trip = Trip.builder()
                .tripNumber(request.getTripNumber())
                .driver(resolveDriver(request.getDriverId()))
                .departureCity(request.getDepartureCity())
                .destinationCity(request.getDestinationCity())
                .departureTime(request.getDepartureTime())
                .arrivalTime(request.getArrivalTime())
                .price(request.getPrice())
                .transportType(request.getTransportType())
                .availableSeats(request.getAvailableSeats())
                .companyName(request.getCompanyName())
                .status(request.getStatus() != null ? request.getStatus() : TripStatus.SCHEDULED)
                .progressPercentage(request.getProgressPercentage() != null ? clampProgress(request.getProgressPercentage()) : 0.0)
                .departureLatitude(request.getDepartureLatitude())
                .departureLongitude(request.getDepartureLongitude())
                .destinationLatitude(request.getDestinationLatitude())
                .destinationLongitude(request.getDestinationLongitude())
                .currentLatitude(request.getCurrentLatitude())
                .currentLongitude(request.getCurrentLongitude())
                .lastLocationUpdate(request.getCurrentLatitude() != null && request.getCurrentLongitude() != null
                        ? LocalDateTime.now()
                        : null)
                .build();

        return tripRepository.save(trip);
    }

    public Trip updateTrip(UUID id, TripRequest request) {
        validateTripRequest(request);

        Trip trip = getTripById(id);

        tripRepository.findByTripNumberIgnoreCase(request.getTripNumber())
                .filter(existingTrip -> !existingTrip.getId().equals(id))
                .ifPresent(existingTrip -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce numéro de trajet est déjà utilisé");
                });

        trip.setTripNumber(request.getTripNumber());
        trip.setDriver(resolveDriver(request.getDriverId()));
        trip.setDepartureCity(request.getDepartureCity());
        trip.setDestinationCity(request.getDestinationCity());
        trip.setDepartureTime(request.getDepartureTime());
        trip.setArrivalTime(request.getArrivalTime());
        trip.setPrice(request.getPrice());
        trip.setTransportType(request.getTransportType());
        trip.setAvailableSeats(request.getAvailableSeats());
        trip.setCompanyName(request.getCompanyName());
        trip.setStatus(request.getStatus() != null ? request.getStatus() : trip.getStatus());
        trip.setProgressPercentage(request.getProgressPercentage() != null
                ? clampProgress(request.getProgressPercentage())
                : trip.getProgressPercentage());
        trip.setDepartureLatitude(request.getDepartureLatitude());
        trip.setDepartureLongitude(request.getDepartureLongitude());
        trip.setDestinationLatitude(request.getDestinationLatitude());
        trip.setDestinationLongitude(request.getDestinationLongitude());
        trip.setCurrentLatitude(request.getCurrentLatitude());
        trip.setCurrentLongitude(request.getCurrentLongitude());
        trip.setLastLocationUpdate(request.getCurrentLatitude() != null && request.getCurrentLongitude() != null
                ? LocalDateTime.now()
                : trip.getLastLocationUpdate());

        return tripRepository.save(trip);
    }

    public Trip assignDriver(UUID tripId, UUID driverId) {
        Trip trip = getTripById(tripId);
        trip.setDriver(resolveRequiredDriver(driverId));
        return tripRepository.save(trip);
    }

    public Trip removeDriver(UUID tripId) {
        Trip trip = getTripById(tripId);
        trip.setDriver(null);
        return tripRepository.save(trip);
    }

    public void deleteTrip(UUID id) {
        Trip trip = getTripById(id);

        if (bookingRepository.existsByTripId(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Impossible de supprimer un trajet avec des réservations");
        }

        tripRepository.delete(trip);
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
            double progress = clampProgress(request.getProgressPercentage());
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

    private Driver resolveDriver(UUID driverId) {
        return driverId == null ? null : resolveRequiredDriver(driverId);
    }

    private Driver resolveRequiredDriver(UUID driverId) {
        if (driverId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "L'ID du chauffeur est obligatoire");
        }

        return driverRepository.findById(driverId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chauffeur non trouvé"));
    }

    private double clampProgress(Double progressPercentage) {
        return Math.max(0, Math.min(100, progressPercentage));
    }

    private void validateTripRequest(TripRequest request) {
        if (request.getTripNumber() == null || request.getTripNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le numéro du trajet est obligatoire");
        }
        if (request.getDepartureCity() == null || request.getDepartureCity().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ville de départ est obligatoire");
        }
        if (request.getDestinationCity() == null || request.getDestinationCity().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La ville d'arrivée est obligatoire");
        }
        if (request.getDepartureTime() == null || request.getArrivalTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les dates de départ et d'arrivée sont obligatoires");
        }
        if (!request.getArrivalTime().isAfter(request.getDepartureTime())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La date d'arrivée doit être après le départ");
        }
        if (request.getPrice() == null || request.getPrice().signum() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le prix est obligatoire");
        }
        if (request.getTransportType() == null || request.getTransportType().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le type de transport est obligatoire");
        }
        if (request.getAvailableSeats() == null || request.getAvailableSeats() < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nombre de places est obligatoire");
        }
        if (request.getCompanyName() == null || request.getCompanyName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom de la compagnie est obligatoire");
        }
        if (request.getDepartureLatitude() == null || request.getDepartureLongitude() == null
                || request.getDestinationLatitude() == null || request.getDestinationLongitude() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Les coordonnées du trajet sont obligatoires");
        }
    }
}
