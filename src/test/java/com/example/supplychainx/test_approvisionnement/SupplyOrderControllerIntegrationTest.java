package com.example.supplychainx.test_approvisionnement;

import com.example.supplychainx.service_approvisionnement.dto.SupplyOrder.SupplyOrderRequestDTO;
import com.example.supplychainx.service_approvisionnement.dto.SupplyOrderItem.SupplyOrderItemDTO;
import com.example.supplychainx.service_approvisionnement.model.RawMaterial;
import com.example.supplychainx.service_approvisionnement.model.Supplier;
import com.example.supplychainx.service_approvisionnement.model.SupplyOrder;
import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_approvisionnement.model.enums.Role;
import com.example.supplychainx.service_approvisionnement.model.enums.SupplyOrderStatus;
import com.example.supplychainx.service_approvisionnement.repository.RawMaterialRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplierRepository;
import com.example.supplychainx.service_approvisionnement.repository.SupplyOrderRepository;
import com.example.supplychainx.service_approvisionnement.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class SupplyOrderControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired SupplyOrderRepository supplyOrderRepository;
    @Autowired UserRepository userRepository;
    @Autowired
    SupplierRepository supplierRepository;

    @Autowired
    RawMaterialRepository rawMaterialRepository;

    private Supplier supplier;
    private RawMaterial rawMaterial;

    private static final String ADMIN_EMAIL = "admin@supply.com";
    private static final String ADMIN_PASSWORD_CLEAR = "1234";

    @BeforeEach
    void setup() {

        // ADMIN
        User admin = new User();
        admin.setEmail(ADMIN_EMAIL);
        admin.setPassword(ADMIN_PASSWORD_CLEAR);
        admin.setPassword("1234");
        admin.setFirstName("admin");
        admin.setLastName("admin");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Supplier utilisé dans les tests
        supplier = new Supplier();
        supplier.setName("SupplierTest");
        supplier.setRating(3.0);
        supplier.setContact("supp@gmail.com");
        supplier.setLeadTime(10);
        supplierRepository.save(supplier);

        // raw material pour tester
        rawMaterial = new RawMaterial();
        rawMaterial.setStock(20);
        rawMaterial.setName("matiere 1");
        rawMaterial.setUnit("kg");
        rawMaterial.setStockMin(5);
        rawMaterialRepository.save(rawMaterial);
    }

    @AfterEach
    void clean() {
        supplyOrderRepository.deleteAll();
        supplierRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void testCreateOrder_Success() throws Exception {

        SupplyOrderItemDTO item = new SupplyOrderItemDTO();
        item.setMaterialId(1L);
        item.setQuantity(10);

        SupplyOrderRequestDTO request = new SupplyOrderRequestDTO();
        request.setSupplierId(supplier.getId());
        request.setOrderItems(List.of(item));

        mockMvc.perform(post("/api/supply-orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("email", ADMIN_EMAIL)
                        .header("password", ADMIN_PASSWORD_CLEAR)
                        .with(user(ADMIN_EMAIL).roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.supplierId", is(1)))
                .andExpect(jsonPath("$.orderItems.length()", is(1)));
    }


    @Test
    void testGetOrderById_Success() throws Exception {

        SupplyOrder order = new SupplyOrder();
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setOrderdate(LocalDate.now());
        order.setSupplier(supplier);

        SupplyOrder saved = supplyOrderRepository.save(order);

        mockMvc.perform(get("/api/supply-orders/" + saved.getId())
                        .header("email", ADMIN_EMAIL)
                        .header("password", ADMIN_PASSWORD_CLEAR)
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.supplierId", is(supplier.getId().intValue())));
    }


    @Test
    void testGetAllOrders_Success() throws Exception {

        SupplyOrder o1 = new SupplyOrder();
        o1.setSupplier(supplier);
        o1.setStatus(SupplyOrderStatus.EN_ATTENTE);
        o1.setOrderdate(LocalDate.now());

        SupplyOrder o2 = new SupplyOrder();
        o2.setSupplier(supplier);
        o2.setStatus(SupplyOrderStatus.EN_ATTENTE);
        o2.setOrderdate(LocalDate.now());

        supplyOrderRepository.save(o1);
        supplyOrderRepository.save(o2);

        mockMvc.perform(get("/api/supply-orders")
                        .header("email", ADMIN_EMAIL)
                        .header("password", ADMIN_PASSWORD_CLEAR)
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)));
    }


    @Test
    void testUpdateOrderStatus_Success() throws Exception {

        SupplyOrder order = new SupplyOrder();
        order.setSupplier(supplier);
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setOrderdate(LocalDate.now());

        SupplyOrder saved = supplyOrderRepository.save(order);

        mockMvc.perform(get("/api/supply-orders/" + saved.getId() + "/update-status")
                        .param("status", "RECUE")
                        .header("email", ADMIN_EMAIL)
                        .header("password", ADMIN_PASSWORD_CLEAR)
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RECUE")));
    }


    @Test
    void testDeleteOrder_Success() throws Exception {

        SupplyOrder order = new SupplyOrder();
        order.setSupplier(supplier);
        order.setStatus(SupplyOrderStatus.EN_ATTENTE);
        order.setOrderdate(LocalDate.now());

        SupplyOrder saved = supplyOrderRepository.save(order);

        mockMvc.perform(delete("/api/supply-orders/" + saved.getId())
                        .header("email", ADMIN_EMAIL)
                        .header("password", ADMIN_PASSWORD_CLEAR)
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isNoContent());

        Assertions.assertEquals(0, supplyOrderRepository.count());
    }
}