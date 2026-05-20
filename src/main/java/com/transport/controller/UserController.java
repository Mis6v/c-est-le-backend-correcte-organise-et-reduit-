package com.transport.controller;

import com.transport.dto.RegisterDto;
import com.transport.service.UserService;
import org.springframework.beans.factory.annotation.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserService userService;

    // REGISTER
    @PostMapping("/register")
    public String register(
            @RequestBody RegisterDto dto) {

        return userService.register(dto);
    }

    // LOGIN
    @PostMapping("/login")
    public String login(
            @RequestBody RegisterDto dto) {

        return userService.login(dto);
    }
}