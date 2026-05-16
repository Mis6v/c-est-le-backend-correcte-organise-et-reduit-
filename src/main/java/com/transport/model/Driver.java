package com.transport.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "drivers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Driver {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column(nullable = false)
    private String licenseNumber;

    @Column(nullable = false)
    private String vehicleName;

    @Column(nullable = false)
    private String vehiclePlate;

    @Column(nullable = false)
    private Boolean available;

    @JsonIgnore
    @OneToMany(mappedBy = "driver")
    private List<Trip> trips;
}
