package com.example.supplychainx.test_approvisionnement;

import com.example.supplychainx.service_approvisionnement.dto.Supplier.SupplierRequestDTO;
import com.example.supplychainx.service_approvisionnement.model.Supplier;
import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_approvisionnement.model.enums.Role;
import com.example.supplychainx.service_approvisionnement.repository.SupplierRepository;
import com.example.supplychainx.service_approvisionnement.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;

import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class SupplierControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SupplierRepository supplierRepository;
    @Autowired UserRepository userRepository;

    private static final String USER_EMAIL = "manager@supply.com";
    private static final String USER_PASSWORD = "1234";

    @BeforeEach
    void setup() {

        User user = new User();
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);
        user.setFirstName("Manager");
        user.setLastName("User");
        user.setRole(Role.GESTIONNAIRE_APPROVISIONNEMENT);
        userRepository.save(user);
    }

    @AfterEach
    void clean() {
        supplierRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void testCreateSupplier_Success() throws Exception {

        SupplierRequestDTO request = new SupplierRequestDTO();
        request.setName("Supplier A");
        request.setContact("contact@supp.com");
        request.setRating(4.0);
        request.setLeadTime(7);

        mockMvc.perform(post("/api/suppliers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("email", USER_EMAIL)
                        .header("password", USER_PASSWORD)
                        .with(user(USER_EMAIL).roles("GESTIONNAIRE_APPROVISIONNEMENT"))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Supplier A")))
                .andExpect(jsonPath("$.rating", is(4.0)));
    }


    @Test
    void testGetAllSuppliers_Success() throws Exception {

        Supplier s1 = new Supplier();
        Supplier s2 = new Supplier();
        s1.setName("Supplier1");
        s2.setName("Supplier2");
        s1.setContact("c1@mail.com");
        s2.setContact("c2@mail.com");
        s1.setRating(3.0);
        s2.setRating(4.0);
        s1.setLeadTime(5);
        s1.setLeadTime(7);

        supplierRepository.saveAll(List.of(s1, s2));

        mockMvc.perform(get("/api/suppliers")
                        .header("email", USER_EMAIL)
                        .header("password", USER_PASSWORD)
                        .with(SecurityMockMvcRequestPostProcessors.user(USER_EMAIL).roles("GESTIONNAIRE_APPROVISIONNEMENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }


    @Test
    void testGetSupplierById_Success() throws Exception {

        Supplier supplier = new Supplier();
        supplier.setName("S1");
        supplier.setContact("s1@mail.com");
        supplier.setRating(3.5);
        supplier.setLeadTime(4);
        Supplier saved = supplierRepository.save(supplier);

        mockMvc.perform(get("/api/suppliers/" + saved.getId())
                        .header("email", USER_EMAIL)
                        .header("password", USER_PASSWORD)
                        .with(user(USER_EMAIL).roles("GESTIONNAIRE_APPROVISIONNEMENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.name", is("S1")));
    }


    @Test
    void testUpdateSupplier_Success() throws Exception {

        Supplier supplier = new Supplier();
        supplier.setName("OldName");
        supplier.setContact("old@mail.com");
        supplier.setRating(2.0);
        supplier.setLeadTime(10);

        Supplier saved = supplierRepository.save(supplier);

        SupplierRequestDTO dto = new SupplierRequestDTO();
        dto.setName("NewName");
        dto.setContact("new@mail.com");
        dto.setRating(5.0);
        dto.setLeadTime(6);

        mockMvc.perform(put("/api/suppliers/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("email", USER_EMAIL)
                        .header("password", USER_PASSWORD)
                        .with(user(USER_EMAIL).roles("GESTIONNAIRE_APPROVISIONNEMENT"))
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("NewName")))
                .andExpect(jsonPath("$.rating", is(5.0)));
    }


    @Test
    void testDeleteSupplier_Success() throws Exception {

        Supplier supplier = new Supplier();
        supplier.setName("ToDelete");
        supplier.setContact("d@mail.com");
        supplier.setRating(3.0);
        supplier.setLeadTime(4);
        Supplier saved = supplierRepository.save(supplier);

        mockMvc.perform(delete("/api/suppliers/" + saved.getId())
                        .header("email", USER_EMAIL)
                        .header("password", USER_PASSWORD)
                        .with(user(USER_EMAIL).roles("GESTIONNAIRE_APPROVISIONNEMENT")))
                .andExpect(status().isNoContent());
    }


    @Test
    void testSearchSupplierByName_Success() throws Exception {

        Supplier supplier = new Supplier();
        supplier.setName("SearchMe");
        supplier.setContact("c@mail.com");
        supplier.setRating(4.0);
        supplier.setLeadTime(5);
        supplierRepository.save(supplier);

        mockMvc.perform(get("/api/suppliers/search")
                        .param("name", "SearchMe")
                        .header("email", USER_EMAIL)
                        .header("password", USER_PASSWORD)
                        .with(user(USER_EMAIL).roles("GESTIONNAIRE_APPROVISIONNEMENT")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("SearchMe")));
    }
}