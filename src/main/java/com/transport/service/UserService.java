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
    // LOGIN + CREATE ACCOUNT AUTOMATIQUE
    public String login(RegisterDto dto) {

        Optional<User> optionalUser =
                userRepository.findByTelephone(
                        dto.getTelephone());

        // utilisateur n'existe pas
        if(optionalUser.isEmpty()) {

            User newUser = new User();

            newUser.setTelephone(
                    dto.getTelephone());

            newUser.setCode(
                    dto.getCode());

            userRepository.save(newUser);

            return "SUCCESS";
        }

        // utilisateur existe déjà
        User user = optionalUser.get();

        // vérifier le code
        if(user.getCode().equals(dto.getCode())) {

            return "SUCCESS";
        }

        return "CODE_INCORRECT";
    }
}