package com.transport.dto;

import lombok.Data;

@Data
public class DriverRequest {
    private String fullName;
    private String phone;
    private String licenseNumber;
    private String vehicleName;
    private String vehiclePlate;
    private Boolean available;
}
