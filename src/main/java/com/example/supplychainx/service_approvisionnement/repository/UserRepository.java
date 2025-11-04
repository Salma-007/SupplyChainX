package com.example.supplychainx.service_approvisionnement.repository;

import com.example.supplychainx.service_approvisionnement.dto.User.UserResponseDTO;
import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_production.repository.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<User, Long> {
    UserResponseDTO findUserByFirstNameAndLastName(String firstName, String lastName);
    Optional<User> findByEmail(String email);
}
