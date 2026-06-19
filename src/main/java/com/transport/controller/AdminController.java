package com.transport.controller;

import com.transport.dto.DriverRequest;
import com.transport.dto.TripRequest;
import com.transport.model.Driver;
import com.transport.model.Trip;
import com.transport.service.DriverService;
import com.transport.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "Admin", description = "API d'administration des chauffeurs et trajets")
@CrossOrigin(origins = "*")
public class AdminController {
    private final DriverService driverService;
    private final TripService tripService;

    @GetMapping("/drivers")
    @Operation(summary = "Lister tous les chauffeurs")
    public List<Driver> getDrivers() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/drivers/{id}")
    @Operation(summary = "Récupérer un chauffeur par ID")
    public Driver getDriver(@PathVariable UUID id) {
        return driverService.getDriverById(id);
    }

    @PostMapping("/drivers")
    @Operation(summary = "Créer un chauffeur")
    public Driver createDriver(@RequestBody DriverRequest request) {
        return driverService.createDriver(request);
    }

    @PutMapping("/drivers/{id}")
    @Operation(summary = "Modifier un chauffeur")
    public Driver updateDriver(@PathVariable UUID id, @RequestBody DriverRequest request) {
        return driverService.updateDriver(id, request);
    }

    @DeleteMapping("/drivers/{id}")
    @Operation(summary = "Supprimer un chauffeur")
    public ResponseEntity<Void> deleteDriver(@PathVariable UUID id) {
        driverService.deleteDriver(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/drivers/{id}/trips")
    @Operation(summary = "Lister les trajets d'un chauffeur")
    public List<Trip> getDriverTrips(@PathVariable UUID id) {
        return driverService.getTripsByDriver(id);
    }

    @GetMapping("/trips")
    @Operation(summary = "Lister tous les trajets")
    public List<Trip> getTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/trips/{id}")
    @Operation(summary = "Récupérer un trajet par ID")
    public Trip getTrip(@PathVariable UUID id) {
        return tripService.getTripById(id);
    }

    @PostMapping("/trips")
    @Operation(summary = "Créer un trajet")
    public Trip createTrip(@RequestBody TripRequest request) {
        return tripService.createTrip(request);
    }

    @PutMapping("/trips/{id}")
    @Operation(summary = "Modifier un trajet")
    public Trip updateTrip(@PathVariable UUID id, @RequestBody TripRequest request) {
        return tripService.updateTrip(id, request);
    }

    @DeleteMapping("/trips/{id}")
    @Operation(summary = "Supprimer un trajet")
    public ResponseEntity<Void> deleteTrip(@PathVariable UUID id) {
        tripService.deleteTrip(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/trips/{tripId}/driver/{driverId}")
    @Operation(summary = "Assigner un chauffeur à un trajet")
    public Trip assignDriver(@PathVariable UUID tripId, @PathVariable UUID driverId) {
        return tripService.assignDriver(tripId, driverId);
    }

    @DeleteMapping("/trips/{tripId}/driver")
    @Operation(summary = "Retirer le chauffeur d'un trajet")
    public Trip removeDriver(@PathVariable UUID tripId) {
        return tripService.removeDriver(tripId);
    }
}
