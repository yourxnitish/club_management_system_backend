package com.collegeclub.controller;

import com.collegeclub.config.CorsConfig;
import com.collegeclub.dto.request.LoginRequest;
import com.collegeclub.dto.response.AuthResponse;
import com.collegeclub.dto.response.UserResponse;
import com.collegeclub.enums.Role;
import com.collegeclub.security.CustomUserDetailsService;
import com.collegeclub.security.JwtAuthenticationEntryPoint;
import com.collegeclub.security.JwtTokenProvider;
import com.collegeclub.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(CorsConfig.class)
@ActiveProfiles("test")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private CustomUserDetailsService customUserDetailsService;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Test
    void login_ShouldReturnAuthResponse_WhenValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest("admin@collegeclub.edu", "Admin@123456");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(1L);
        userResponse.setEmail("admin@collegeclub.edu");
        userResponse.setRole(Role.SYSTEM_ADMIN);

        AuthResponse authResponse = new AuthResponse("mock-jwt-token", "mock-refresh-token", 86400, userResponse);

        when(authService.login(any(LoginRequest.class))).thenReturn(authResponse);

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value("mock-jwt-token"))
                .andExpect(jsonPath("$.data.user.email").value("admin@collegeclub.edu"));
    }

    @Test
    void login_ShouldReturnBadRequest_WhenEmailIsInvalid() throws Exception {
        LoginRequest request = new LoginRequest("invalid-email-format", "pass");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
