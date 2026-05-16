package com.transport.service;

import com.transport.model.Driver;
import com.transport.model.Trip;
import com.transport.repository.DriverRepository;
import com.transport.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
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
}
