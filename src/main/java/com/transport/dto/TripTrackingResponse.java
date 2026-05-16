package com.transport.dto;

import com.transport.model.TripStatus;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class TripTrackingResponse {
    private UUID tripId;
    private String tripNumber;
    private String departureCity;
    private String destinationCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private TripStatus status;
    private Double progressPercentage;
    private Double departureLatitude;
    private Double departureLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private Double currentLatitude;
    private Double currentLongitude;
    private LocalDateTime lastLocationUpdate;
    private String companyName;
    private String driverName;
    private String driverPhone;
    private String vehicleName;
    private String vehiclePlate;
}
