package com.transport.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
public class TripShare {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String shareToken;

    @ManyToOne
    @JoinColumn(name = "trip_id")
    private Trip trip;

    private LocalDateTime createdAt;

    private LocalDateTime expiresAt;

    private Boolean active = true;
}