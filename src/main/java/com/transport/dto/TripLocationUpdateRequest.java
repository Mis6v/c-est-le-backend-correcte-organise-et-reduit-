package com.transport.dto;

import lombok.Data;

@Data
public class TripLocationUpdateRequest {
    private Double currentLatitude;
    private Double currentLongitude;
    private Double progressPercentage;
}
