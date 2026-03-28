package com.transport.controller;

import com.transport.model.Trip;
import com.transport.service.TripService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/trips")
@RequiredArgsConstructor
@Tag(name = "Trajets", description = "API pour la gestion des trajets de transport")
@CrossOrigin(origins = "*")
public class TripController {
    private final TripService tripService;

    @GetMapping
    @Operation(summary = "Récupérer tous les trajets")
    public List<Trip> getAllTrips() {
        return tripService.getAllTrips();
    }

    @GetMapping("/search")
    @Operation(summary = "Rechercher des trajets par ville et date")
    public List<Trip> searchTrips(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime date) {
        return tripService.searchTrips(from, to, date);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer les détails d'un trajet par ID")
    public ResponseEntity<Trip> getTripById(@PathVariable UUID id) {
        return ResponseEntity.ok(tripService.getTripById(id));
    }
}
