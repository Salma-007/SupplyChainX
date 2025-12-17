package com.example.supplychainx.service_approvisionnement.service;

import com.example.supplychainx.service_approvisionnement.dto.User.UserRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.User.UserResponseDTO;
import com.example.supplychainx.service_approvisionnement.exceptions.UserNotFoundException;
import com.example.supplychainx.service_approvisionnement.mapper.UserMapper;
import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_approvisionnement.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public UserResponseDTO createUser(UserRequestDTO dto) {
        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        User saved = userRepository.save(user);
        return userMapper.toResponseDto(saved);
    }

    public List<UserResponseDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    public UserResponseDTO getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found with id : " + id));
        return userMapper.toResponseDto(user);
    }

    public UserResponseDTO updateUser(Long id, UserRequestDTO dto) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("user not found with id : " + id));

        User updated = userRepository.save(existingUser);
        return userMapper.toResponseDto(updated);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException("user not found with id : " + id);
        }
        userRepository.deleteById(id);
    }

    public User findUserByEmail(String email){
        Optional<User> findHim = userRepository.findByEmail(email);
        if(findHim.isEmpty()){
            throw new UserNotFoundException("user not found with email : " + email);
        }
        return findHim.get();
    }


}