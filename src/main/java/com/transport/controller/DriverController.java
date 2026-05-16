package com.transport.controller;

import com.transport.model.Driver;
import com.transport.model.Trip;
import com.transport.service.DriverService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/drivers")
@RequiredArgsConstructor
@Tag(name = "Chauffeurs", description = "API pour la gestion des chauffeurs et leurs trajets")
@CrossOrigin(origins = "*")
public class DriverController {
    private final DriverService driverService;

    @GetMapping
    @Operation(summary = "Récupérer tous les chauffeurs")
    public List<Driver> getAllDrivers() {
        return driverService.getAllDrivers();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Récupérer un chauffeur par ID")
    public Driver getDriverById(@PathVariable UUID id) {
        return driverService.getDriverById(id);
    }

    @GetMapping("/phone/{phone}")
    @Operation(summary = "Récupérer un chauffeur par téléphone")
    public Driver getDriverByPhone(@PathVariable String phone) {
        return driverService.getDriverByPhone(phone);
    }

    @GetMapping("/{id}/trips")
    @Operation(summary = "Récupérer les trajets associés à un chauffeur")
    public List<Trip> getTripsByDriver(@PathVariable UUID id) {
        return driverService.getTripsByDriver(id);
    }

    @GetMapping("/phone/{phone}/trips")
    @Operation(summary = "Récupérer les trajets associés à un chauffeur par téléphone")
    public List<Trip> getTripsByDriverPhone(@PathVariable String phone) {
        return driverService.getTripsByDriverPhone(phone);
    }
}
