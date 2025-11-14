package com.example.supplychainx.test_approvisionnement;

import com.example.supplychainx.service_approvisionnement.dto.RawMaterial.RawMaterialRequestDTO;
import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_approvisionnement.model.enums.Role;
import com.example.supplychainx.service_approvisionnement.repository.RawMaterialRepository;
import com.example.supplychainx.service_approvisionnement.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class RawMaterialControllerIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String ADMIN_EMAIL = "admin@supply.com";
    private static final String ADMIN_PASSWORD_CLEAR = "1234";


    @BeforeEach
    void setupAdminUser() {
        User admin = new User();
        admin.setFirstName("Test");
        admin.setLastName("Admin");
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(ADMIN_PASSWORD_CLEAR);
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);
    }

    @AfterEach
    void tearDown() {
        rawMaterialRepository.deleteAll();
    }

    @Test
    void testCreateRawMaterial_Success() throws Exception {
        RawMaterialRequestDTO requestDTO = new RawMaterialRequestDTO();
        requestDTO.setName("Acier Inoxydable");
        requestDTO.setStock(100);
        requestDTO.setStockMin(10);
        requestDTO.setUnit("kg");

        mockMvc.perform(post("/api/raw-materials")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("email", ADMIN_EMAIL)
                        .header("password", ADMIN_PASSWORD_CLEAR)
                        .with(user(ADMIN_EMAIL).roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(requestDTO)))

                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Acier Inoxydable")));

        org.assertj.core.api.Assertions.assertThat(rawMaterialRepository.count()).isEqualTo(1);
    }


    @Test
    void testGetMaterialById_Success() throws Exception {
        RawMaterial material = new RawMaterial();
        material.setName("Plastique ABS");
        material.setStock(50);
        material.setStockMin(5);
        material.setUnit("pièce");
        RawMaterial savedMaterial = rawMaterialRepository.save(material);

        mockMvc.perform(get("/api/raw-materials/{id}", savedMaterial.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("email", ADMIN_EMAIL)
                        .header("password", ADMIN_PASSWORD_CLEAR)
                        .with(user(ADMIN_EMAIL).roles("ADMIN"))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(savedMaterial.getId().intValue())))
                .andExpect(jsonPath("$.name", is("Plastique ABS")))
                .andExpect(jsonPath("$.stock", is(50)));
    }
}
