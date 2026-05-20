package com.transport.model;

import jakarta.persistence.*;
import lombok.*;
@Data
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String telephone;

    private String code;

    // getters setters
}