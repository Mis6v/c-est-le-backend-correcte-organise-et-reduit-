package com.transport.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class BookingRequest {
    private UUID tripId;
    private String passengerName;
    private String passengerPhone;
    private Integer seatNumber;
}
