package com.transport.model;


import jakarta.persistence.*;
import lombok.Setter;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    private String telephone;

    @Setter
    private String code;

    public Long getId() {
        return id;
    }

    public String getTelephone() {
        return telephone;
    }

    public String getCode() {
        return code;
    }

}