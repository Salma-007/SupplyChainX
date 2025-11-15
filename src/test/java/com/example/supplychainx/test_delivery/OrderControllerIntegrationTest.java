package com.example.supplychainx.test_delivery;

import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_approvisionnement.model.enums.Role;
import com.example.supplychainx.service_approvisionnement.repository.UserRepository;
import com.example.supplychainx.service_delivery.dto.order.OrderRequestDTO;
import com.example.supplychainx.service_delivery.model.Customer;
import com.example.supplychainx.service_delivery.model.Order;
import com.example.supplychainx.service_delivery.model.enums.OrderStatus;
import com.example.supplychainx.service_delivery.repository.CustomerRepository;
import com.example.supplychainx.service_delivery.repository.OrderRepository;
import com.example.supplychainx.service_production.model.Product;
import com.example.supplychainx.service_production.repository.ProductRepository;
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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class OrderControllerIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
            new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;
    @Autowired OrderRepository orderRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired CustomerRepository customerRepository;

    private Product product;
    private Customer customer;

    private static final String GESTIONNAIRE_COMMERCIAL_EMAIL = "gc@delivery.com";
    private static final String SUPERVISEUR_LIVRAISONS_EMAIL = "sl@delivery.com";
    private static final String ADMIN_EMAIL = "admin@delivery.com";
    private static final String PASSWORD = "1234";
    private static final String BASE_URL = "/api/orders";

    @BeforeEach
    void setup() {
        // Utilisateurs pour les tests d'autorisation
        userRepository.save(createUser(ADMIN_EMAIL, Role.ADMIN));
        userRepository.save(createUser(GESTIONNAIRE_COMMERCIAL_EMAIL, Role.GESTIONNAIRE_COMMERCIAL));
        userRepository.save(createUser(SUPERVISEUR_LIVRAISONS_EMAIL, Role.SUPERVISEUR_LIVRAISONS));

        // Entités requises
        product = new Product();
        product.setProductionTime(10);
        product.setStock(20);
        product.setCost(100.0);
        product.setName("ProductX");

        productRepository.save(product);

        customer = new Customer();
        customer.setName("Client Y");
        customer.setAddress("nyc");
        customer.setCity("new york");
        customerRepository.save(customer);
    }

    private User createUser(String email, Role role) {
        User user = new User();
        user.setEmail(email);
        user.setFirstName("user");
        user.setLastName("admin");
        user.setPassword(PASSWORD);
        user.setRole(role);
        return user;
    }

    @AfterEach
    void clean() {
        orderRepository.deleteAll();
        productRepository.deleteAll();
        customerRepository.deleteAll();
        userRepository.deleteAll();
    }

    // --- Tests de Création (Rôle requis: GESTIONNAIRE_COMMERCIAL ou ADMIN) ---

    @Test
    void testCreateOrder_Success_GC() throws Exception {
        // GIVEN
        OrderRequestDTO request = new OrderRequestDTO(product.getId(), customer.getId(), 5);

        // WHEN & THEN
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("email", GESTIONNAIRE_COMMERCIAL_EMAIL)
                        .header("password", PASSWORD)
                        .with(user(GESTIONNAIRE_COMMERCIAL_EMAIL).roles("GESTIONNAIRE_COMMERCIAL"))
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.quantity", is(5)))
                .andExpect(jsonPath("$.customerId", is(customer.getId().intValue())));
    }


    @Test
    void testGetAllOrders_Success_SL() throws Exception {
        // GIVEN: Créer des commandes
        Order o1 = createAndSaveOrder(OrderStatus.EN_PREPARATION, product, customer, 10);
        Order o2 = createAndSaveOrder(OrderStatus.EN_ROUTE, product, customer, 20);

        // WHEN & THEN: Testé par un SUPERVISEUR_LIVRAISONS
        mockMvc.perform(get(BASE_URL)
                        .header("email", SUPERVISEUR_LIVRAISONS_EMAIL)
                        .header("password", PASSWORD)
                        .with(user(SUPERVISEUR_LIVRAISONS_EMAIL).roles("SUPERVISEUR_LIVRAISONS")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].status", is("EN_PREPARATION")));
    }

    @Test
    void testGetOrderById_Success_Admin() throws Exception {
        // GIVEN
        Order order = createAndSaveOrder(OrderStatus.EN_PREPARATION, product, customer, 10);

        // WHEN & THEN: Testé par un ADMIN
        mockMvc.perform(get(BASE_URL + "/" + order.getId())
                        .header("email", ADMIN_EMAIL)
                        .header("password", PASSWORD)
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(order.getId().intValue())))
                .andExpect(jsonPath("$.status", is("EN_PREPARATION")));
    }

    // --- Tests de Mise à jour (Rôle requis: GESTIONNAIRE_COMMERCIAL ou ADMIN) ---

    @Test
    void testUpdateOrder_Success_Admin() throws Exception {
        // GIVEN
        Order order = createAndSaveOrder(OrderStatus.EN_PREPARATION, product, customer, 10);
        OrderRequestDTO updateRequest = new OrderRequestDTO(product.getId(), customer.getId(), 50);

        mockMvc.perform(put(BASE_URL + "/" + order.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("email", ADMIN_EMAIL)
                        .header("password", PASSWORD)
                        .with(user(ADMIN_EMAIL).roles("ADMIN"))
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity", is(50)));
    }

    // --- Tests de Suppression (Rôle requis: GESTIONNAIRE_COMMERCIAL ou ADMIN) ---

    @Test
    void testDeleteOrder_Success_GC() throws Exception {
        // GIVEN
        Order order = createAndSaveOrder(OrderStatus.EN_PREPARATION, product, customer, 10);

        // WHEN
        mockMvc.perform(delete(BASE_URL + "/" + order.getId())
                        .header("email", GESTIONNAIRE_COMMERCIAL_EMAIL)
                        .header("password", PASSWORD)
                        .with(user(GESTIONNAIRE_COMMERCIAL_EMAIL).roles("GESTIONNAIRE_COMMERCIAL")))
                .andExpect(status().isNoContent());

        // THEN
        Assertions.assertEquals(0, orderRepository.count());
    }

    // --- Tests de Méthodes Spécifiques ---

    @Test
    void testCancelDeliveryOrder_Success_Admin() throws Exception {
        // GIVEN: Commande non expédiée
        Order order = createAndSaveOrder(OrderStatus.EN_PREPARATION, product, customer, 10);

        // WHEN & THEN
        mockMvc.perform(patch(BASE_URL + "/" + order.getId() + "/cancel")
                        .header("email", ADMIN_EMAIL)
                        .header("password", PASSWORD)
                        .with(user(ADMIN_EMAIL).roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("ANNULEE")));
    }

    @Test
    void testChangeStatus_Success_GC() throws Exception {
        // GIVEN
        Order order = createAndSaveOrder(OrderStatus.EN_PREPARATION, product, customer, 10);

        // WHEN & THEN
        mockMvc.perform(get(BASE_URL + "/" + order.getId() + "/update-status")
                        .param("status", "LIVREE")
                        .header("email", GESTIONNAIRE_COMMERCIAL_EMAIL)
                        .header("password", PASSWORD)
                        .with(user(GESTIONNAIRE_COMMERCIAL_EMAIL).roles("GESTIONNAIRE_COMMERCIAL")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("LIVREE")));
    }


    // --- Méthodes utilitaires ---

    private Order createAndSaveOrder(OrderStatus status, Product p, Customer c, int quantity) {
        Order order = new Order();
        order.setProduct(p);
        order.setCustomer(c);
        order.setQuantity(quantity);
        // Assurez-vous d'avoir un champ 'status' de type OrderStatus dans votre modèle Order
        order.setStatus(status);
        return orderRepository.save(order);
    }
}
