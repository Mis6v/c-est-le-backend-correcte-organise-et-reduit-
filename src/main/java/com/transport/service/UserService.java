package com.transport.service;
import com.transport.dto.RegisterDto;
import com.transport.model.User;
import com.transport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // CREATE ACCOUNT
    public String register(RegisterDto dto) {

        // vérifier numéro existe
        if(userRepository.existsByTelephone(
                dto.getTelephone())) {

            return "NUMERO_EXISTE";
        }

        User user = new User();

        user.setTelephone(dto.getTelephone());

        user.setCode(dto.getCode());

        userRepository.save(user);

        return "COMPTE_CREE";
    }

    // LOGIN
    public String login(RegisterDto dto) {

        Optional<User> optionalUser =
                userRepository.findByTelephone(
                        dto.getTelephone());

        if(optionalUser.isEmpty()) {
            return "UTILISATEUR_INTROUVABLE";
        }

        User user = optionalUser.get();

        if(user.getCode().equals(dto.getCode())) {
            return "SUCCESS";
        }

        return "CODE_INCORRECT";
    }
}