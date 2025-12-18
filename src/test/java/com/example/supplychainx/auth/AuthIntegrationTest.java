package com.example.supplychainx.auth;

import com.example.supplychainx.auth.dto.LoginRequest;
import com.example.supplychainx.auth.dto.TokenRefreshRequest;
import com.example.supplychainx.auth.repository.RefreshTokenRepository;
import com.example.supplychainx.service_approvisionnement.model.User;
import com.example.supplychainx.service_approvisionnement.model.enums.Role;
import com.example.supplychainx.service_approvisionnement.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import static com.jayway.jsonpath.internal.path.PathCompiler.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        refreshTokenRepository.deleteAll();
        userRepository.deleteAll();
        User testUser = new User();
        testUser.setEmail("test@gmail.com");
        testUser.setPassword(passwordEncoder.encode("password123"));
        testUser.setRole(Role.ADMIN);
        testUser.setFirstName("test2");
        testUser.setLastName("test2");
        userRepository.save(testUser);
    }

    @Test
    void login_Valide_RetourneTokens() throws Exception {
        LoginRequest request = new LoginRequest("test@gmail.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @Test
    void login_Invalide_Retourne401() throws Exception {
        LoginRequest request = new LoginRequest("test@gmail.com", "mauvais_pass");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }


    @Test
    void acces_SansToken_Retourne401() throws Exception {
        mockMvc.perform(get("/api/users"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void acces_AvecTokenValide_Retourne200() throws Exception {

        LoginRequest login = new LoginRequest("test@gmail.com", "password123");
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(login))).andReturn();

        String response = result.getResponse().getContentAsString();
        String token = objectMapper.readTree(response).get("accessToken").asText();

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void renouvellement_ViaRefreshToken_Fonctionne() throws Exception {
        LoginRequest login = new LoginRequest("test@gmail.com", "password123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();

        String responseString = loginResult.getResponse().getContentAsString();
        System.out.println("Réponse du login : " + responseString);

        var jsonNode = objectMapper.readTree(responseString);

        if (jsonNode.get("refreshToken") == null) {
            fail("Le champ 'refreshToken' est absent de la réponse. Champs présents : " + jsonNode.fieldNames().next());
        }

        String refreshToken = jsonNode.get("refreshToken").asText();

        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken(refreshToken);

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    @Test
    void refresh_Inexistant_RetourneErreur() throws Exception {
        TokenRefreshRequest refreshRequest = new TokenRefreshRequest();
        refreshRequest.setRefreshToken("token-bidon-qui-n-est-pas-en-base");

        mockMvc.perform(post("/api/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshRequest)))
                .andExpect(status().isForbidden());
    }
}
