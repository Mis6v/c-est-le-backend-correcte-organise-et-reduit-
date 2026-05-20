package com.transport.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.transport.model.User;

import java.util.Optional;

@Repository
public interface UserRepository
        extends JpaRepository<User, Long> {

    boolean existsByTelephone(String telephone);

    Optional<User> findByTelephone(String telephone);
}