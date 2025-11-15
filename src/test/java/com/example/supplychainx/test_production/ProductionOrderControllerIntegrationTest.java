package com.example.supplychainx.test_production;

import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_approvisionnement.model.enums.Role;
import com.example.supplychainx.service_approvisionnement.repository.UserRepository;
import com.example.supplychainx.service_production.dto.productionOrder.ProductionOrderRequestDTO;
import com.example.supplychainx.service_production.model.Product;
import com.example.supplychainx.service_production.model.ProductionOrder;
import com.example.supplychainx.service_production.model.enums.ProductionOrderStatus;
import com.example.supplychainx.service_production.repository.ProductRepository;
import com.example.supplychainx.service_production.repository.ProductionOrderRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDate;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class ProductionOrderControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired ProductionOrderRepository productionOrderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired ProductRepository productRepository;

    private Product product;

    private static final String ADMIN_EMAIL = "admin@production.com";
    private static final String ADMIN_PASSWORD_CLEAR = "1234";
    private static final String BASE_URL = "/api/production-orders";

    @BeforeEach
    void setup() {

        // ADMIN User
        User admin = new User();
        admin.setEmail(ADMIN_EMAIL);
        // Note: setting clear password for simulation purposes
        admin.setPassword(ADMIN_PASSWORD_CLEAR);
        admin.setFirstName("admin");
        admin.setLastName("admin");
        admin.setRole(Role.ADMIN);
        userRepository.save(admin);

        // Product pour les tests
        product = new Product();
        product.setName("Produit Fini X");
        product.setProductionTime(10);
        product.setStock(20);
        product.setCost(100.0);
        productRepository.save(product);
    }

    @AfterEach
    void clean() {
        productionOrderRepository.deleteAll();
        productRepository.deleteAll();
        userRepository.deleteAll();
    }

    // --- Tests CRUD ---

    @Test
    void testCreateProductionOrder_Success() throws Exception {
        // GIVEN
        ProductionOrderRequestDTO request = new ProductionOrderRequestDTO(
                50,
                ProductionOrderStatus.EN_ATTENTE,
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(5),
                product.getId()
        );

        // WHEN & THEN
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("email", ADMIN_EMAIL) // <-- AJOUTÉ
                        .header("password", ADMIN_PASSWORD_CLEAR) // <-- AJOUTÉ
                        .with(user(ADMIN_EMAIL).roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity", is(50)))
                .andExpect(jsonPath("$.productId", is(product.getId().intValue())))
                .andExpect(jsonPath("$.status", is("EN_ATTENTE")));
    }


    @Test
    void testGetProductionOrderById_Success() throws Exception {
        // GIVEN
        ProductionOrder order = new ProductionOrder();
        order.setProduct(product);
        order.setQuantity(10);
        order.setStatus(ProductionOrderStatus.EN_ATTENTE);
        order.setStartDate(LocalDate.now().plusDays(1));
        order.setEndDate(LocalDate.now().plusDays(5));
        ProductionOrder saved = productionOrderRepository.save(order);

        // WHEN & THEN
        mockMvc.perform(get(BASE_URL + "/" + saved.getId())
                        .header("email", ADMIN_EMAIL) // <-- AJOUTÉ
                        .header("password", ADMIN_PASSWORD_CLEAR) // <-- AJOUTÉ
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(saved.getId().intValue())))
                .andExpect(jsonPath("$.quantity", is(10)));
    }

    @Test
    void testGetAllProductionOrders_Success() throws Exception {
        // GIVEN
        ProductionOrder o1 = new ProductionOrder();
        o1.setProduct(product); o1.setQuantity(10); o1.setStatus(ProductionOrderStatus.EN_ATTENTE); o1.setStartDate(LocalDate.now().plusDays(1)); o1.setEndDate(LocalDate.now().plusDays(5));
        ProductionOrder o2 = new ProductionOrder();
        o2.setProduct(product); o2.setQuantity(20); o2.setStatus(ProductionOrderStatus.EN_PRODUCTION); o2.setStartDate(LocalDate.now().plusDays(2)); o2.setEndDate(LocalDate.now().plusDays(6));

        productionOrderRepository.save(o1);
        productionOrderRepository.save(o2);

        // WHEN & THEN
        mockMvc.perform(get(BASE_URL)
                        .header("email", ADMIN_EMAIL) // <-- AJOUTÉ
                        .header("password", ADMIN_PASSWORD_CLEAR) // <-- AJOUTÉ
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()", is(2)))
                .andExpect(jsonPath("$.content.[0].status", is("EN_ATTENTE")))
                .andExpect(jsonPath("$.content.[1].status", is("EN_PRODUCTION")));
    }

    @Test
    void testUpdateProductionOrder_Success() throws Exception {
        // GIVEN
        ProductionOrder order = new ProductionOrder();
        order.setProduct(product);
        order.setQuantity(10);
        order.setStatus(ProductionOrderStatus.EN_ATTENTE);
        order.setStartDate(LocalDate.now().plusDays(1));
        order.setEndDate(LocalDate.now().plusDays(5));
        ProductionOrder saved = productionOrderRepository.save(order);

        ProductionOrderRequestDTO updateRequest = new ProductionOrderRequestDTO(
                15, // Nouvelle quantité
                ProductionOrderStatus.TERMINE, // Nouveau statut
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3), // Nouvelle date de fin
                product.getId()
        );

        // WHEN & THEN
        mockMvc.perform(put(BASE_URL + "/" + saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("email", ADMIN_EMAIL) // <-- AJOUTÉ
                        .header("password", ADMIN_PASSWORD_CLEAR) // <-- AJOUTÉ
                        .with(user(ADMIN_EMAIL).roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(15)))
                .andExpect(jsonPath("$.status", is("TERMINE")));
    }


    @Test
    void testUpdateProductionOrderStatus_Success() throws Exception {
        // GIVEN
        ProductionOrder order = new ProductionOrder();
        order.setProduct(product);
        order.setQuantity(10);
        order.setStatus(ProductionOrderStatus.EN_ATTENTE);
        order.setStartDate(LocalDate.now().plusDays(1));
        order.setEndDate(LocalDate.now().plusDays(5));
        ProductionOrder saved = productionOrderRepository.save(order);

        // WHEN & THEN
        mockMvc.perform(get(BASE_URL + "/" + saved.getId() + "/update-status")
                        .param("status", "EN_PRODUCTION")
                        .header("email", ADMIN_EMAIL)
                        .header("password", ADMIN_PASSWORD_CLEAR)
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("EN_PRODUCTION")));
    }


    @Test
    void testDeleteProductionOrder_Success() throws Exception {
        // GIVEN
        ProductionOrder order = new ProductionOrder();
        order.setProduct(product);
        order.setQuantity(10);
        order.setStatus(ProductionOrderStatus.EN_ATTENTE);
        order.setStartDate(LocalDate.now().plusDays(1));
        order.setEndDate(LocalDate.now().plusDays(5));
        ProductionOrder saved = productionOrderRepository.save(order);

        // WHEN
        mockMvc.perform(delete(BASE_URL + "/" + saved.getId())
                        .header("email", ADMIN_EMAIL) // <-- AJOUTÉ
                        .header("password", ADMIN_PASSWORD_CLEAR) // <-- AJOUTÉ
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isNoContent());

        // THEN
        Assertions.assertEquals(0, productionOrderRepository.count());
    }
}