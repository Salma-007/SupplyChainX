package com.example.supplychainx.test_approvisionnement;

import com.example.supplychainx.service_approvisionnement.dto.User.UserRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.User.UserResponseDTO;
import com.example.supplychainx.service_approvisionnement.exceptions.UserNotFoundException;
import com.example.supplychainx.service_approvisionnement.mapper.UserMapper;
import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_approvisionnement.model.enums.Role;
import com.example.supplychainx.service_approvisionnement.repository.UserRepository;
import com.example.supplychainx.service_approvisionnement.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
    class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private UserRequestDTO userRequestDTO;
    private User user;
    private UserResponseDTO userResponseDTO;
    private final Long USER_ID = 1L;
    private final String EMAIL = "test@example.com";

    @BeforeEach
    void setUp() {
        userRequestDTO = new UserRequestDTO();
        userRequestDTO.setFirstName("John");
        userRequestDTO.setEmail(EMAIL);
        userRequestDTO.setRole(Role.ADMIN);

        user = new User();
        user.setId(USER_ID);
        user.setFirstName("John");
        user.setEmail(EMAIL);
        user.setRole(Role.ADMIN);

        userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(USER_ID);
        userResponseDTO.setFirstName("John");
        userResponseDTO.setEmail(EMAIL);
        userResponseDTO.setRole(Role.ADMIN);
    }


    @Test
    void createUser_Success() {
        when(userMapper.toEntity(userRequestDTO)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.createUser(userRequestDTO);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
        assertEquals(EMAIL, result.getEmail());
        verify(userRepository).save(user);
    }


    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(user, new User());
        when(userRepository.findAll()).thenReturn(users);

        when(userMapper.toResponseDto(any(User.class)))
            .thenReturn(userResponseDTO)
            .thenReturn(new UserResponseDTO());

        List<UserResponseDTO> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository).findAll();
    }

    @Test
    void getUserById_Success() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDTO);

        UserResponseDTO result = userService.getUserById(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getId());
    }

    @Test
    void getUserById_ThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(USER_ID));
    }


    @Test
    void updateUser_Success() {
        UserRequestDTO updateDto = new UserRequestDTO();
        updateDto.setFirstName("Jane");

        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));


        User savedUser = new User();
        savedUser.setId(USER_ID);
        savedUser.setFirstName("Jane");

        when(userRepository.save(user)).thenReturn(savedUser);

        UserResponseDTO expectedResponse = new UserResponseDTO();
        expectedResponse.setId(USER_ID);
        expectedResponse.setFirstName("Jane");
        when(userMapper.toResponseDto(savedUser)).thenReturn(expectedResponse);

        UserResponseDTO result = userService.updateUser(USER_ID, updateDto);

        assertNotNull(result);
        assertEquals("Jane", result.getFirstName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_ThrowsUserNotFoundException() {
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(USER_ID, userRequestDTO));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        userService.deleteUser(USER_ID);

        verify(userRepository).existsById(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void deleteUser_ThrowsUserNotFoundException() {
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(USER_ID));
        verify(userRepository, never()).deleteById(anyLong());
    }
}