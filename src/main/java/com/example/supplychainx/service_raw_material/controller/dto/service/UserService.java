package com.example.supplychainx.service_raw_material.controller.dto.service;


import com.example.supplychainx.service_raw_material.controller.dto.UserRequestDTO;
import com.example.supplychainx.service_raw_material.controller.dto.UserResponseDTO;
import com.example.supplychainx.service_raw_material.controller.dto.mapper.UserMapper;
import com.example.supplychainx.service_raw_material.controller.dto.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponseDTO createUser(UserRequestDTO dto) {

    }
}