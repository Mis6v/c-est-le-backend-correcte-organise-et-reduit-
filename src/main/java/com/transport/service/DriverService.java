package com.transport.service;

import com.transport.dto.DriverRequest;
import com.transport.model.Driver;
import com.transport.model.Trip;
import com.transport.repository.DriverRepository;
import com.transport.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DriverService {
    private final DriverRepository driverRepository;
    private final TripRepository tripRepository;

    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    public Driver getDriverById(UUID id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chauffeur non trouvé"));
    }

    public Driver getDriverByPhone(String phone) {
        return driverRepository.findByPhone(phone)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Chauffeur non trouvé"));
    }

    public List<Trip> getTripsByDriver(UUID driverId) {
        getDriverById(driverId);
        return tripRepository.findByDriverId(driverId);
    }

    public List<Trip> getTripsByDriverPhone(String phone) {
        Driver driver = getDriverByPhone(phone);
        return tripRepository.findByDriverId(driver.getId());
    }

    public Driver createDriver(DriverRequest request) {
        validateDriverRequest(request);

        if (driverRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce téléphone est déjà utilisé");
        }

        Driver driver = Driver.builder()
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .licenseNumber(request.getLicenseNumber())
                .vehicleName(request.getVehicleName())
                .vehiclePlate(request.getVehiclePlate())
                .available(request.getAvailable() != null ? request.getAvailable() : true)
                .build();

        return driverRepository.save(driver);
    }

    public Driver updateDriver(UUID id, DriverRequest request) {
        validateDriverRequest(request);

        Driver driver = getDriverById(id);

        driverRepository.findByPhone(request.getPhone())
                .filter(existingDriver -> !existingDriver.getId().equals(id))
                .ifPresent(existingDriver -> {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ce téléphone est déjà utilisé");
                });

        driver.setFullName(request.getFullName());
        driver.setPhone(request.getPhone());
        driver.setLicenseNumber(request.getLicenseNumber());
        driver.setVehicleName(request.getVehicleName());
        driver.setVehiclePlate(request.getVehiclePlate());
        driver.setAvailable(request.getAvailable() != null ? request.getAvailable() : driver.getAvailable());

        return driverRepository.save(driver);
    }

    @Transactional
    public void deleteDriver(UUID id) {
        Driver driver = getDriverById(id);
        List<Trip> trips = tripRepository.findByDriverId(id);

        for (Trip trip : trips) {
            trip.setDriver(null);
        }

        tripRepository.saveAll(trips);
        driverRepository.delete(driver);
    }

    private void validateDriverRequest(DriverRequest request) {
        if (request.getFullName() == null || request.getFullName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le nom du chauffeur est obligatoire");
        }
        if (request.getPhone() == null || request.getPhone().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le téléphone du chauffeur est obligatoire");
        }
        if (request.getLicenseNumber() == null || request.getLicenseNumber().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le numéro de permis est obligatoire");
        }
        if (request.getVehicleName() == null || request.getVehicleName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Le véhicule est obligatoire");
        }
        if (request.getVehiclePlate() == null || request.getVehiclePlate().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La plaque du véhicule est obligatoire");
        }
    }
}
