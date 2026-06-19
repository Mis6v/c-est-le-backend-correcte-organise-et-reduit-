package com.transport.dto;

import com.transport.model.TripStatus;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class TripRequest {
    private String tripNumber;
    private UUID driverId;
    private String departureCity;
    private String destinationCity;
    private LocalDateTime departureTime;
    private LocalDateTime arrivalTime;
    private BigDecimal price;
    private String transportType;
    private Integer availableSeats;
    private String companyName;
    private TripStatus status;
    private Double progressPercentage;
    private Double departureLatitude;
    private Double departureLongitude;
    private Double destinationLatitude;
    private Double destinationLongitude;
    private Double currentLatitude;
    private Double currentLongitude;
}
