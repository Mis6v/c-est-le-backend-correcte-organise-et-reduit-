package com.transport.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BookingRequest {
    private UUID tripId;
    private String passengerName;
    private String passengerPhone;
    private List<Integer> seatNumbers;
}
