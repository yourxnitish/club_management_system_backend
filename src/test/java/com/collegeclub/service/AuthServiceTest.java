package com.collegeclub.service;

import com.collegeclub.dto.request.LoginRequest;
import com.collegeclub.dto.request.RegisterRequest;
import com.collegeclub.dto.response.AuthResponse;
import com.collegeclub.dto.response.UserResponse;
import com.collegeclub.entity.RefreshToken;
import com.collegeclub.entity.User;
import com.collegeclub.enums.Role;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.repository.RefreshTokenRepository;
import com.collegeclub.repository.UserRepository;
import com.collegeclub.security.JwtTokenProvider;
import com.collegeclub.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private UserService userService;

    @Mock
    private ActivityLogService activityLogService;

    @InjectMocks
    private AuthService authService;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setId(10L);
        sampleUser.setEmail("student@collegeclub.edu");
        sampleUser.setFirstName("John");
        sampleUser.setLastName("Doe");
        sampleUser.setRole(Role.STUDENT);
        sampleUser.setActive(true);
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("student@collegeclub.edu");
        request.setPassword("Secret123!");
        request.setFirstName("John");
        request.setLastName("Doe");

        when(userRepository.existsByEmail("student@collegeclub.edu")).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void register_ShouldSucceed_WhenValidStudentPayload() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("newstudent@collegeclub.edu");
        request.setPassword("Secret123!");
        request.setFirstName("Jane");
        request.setLastName("Doe");

        when(userRepository.existsByEmail("newstudent@collegeclub.edu")).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("hashedSecret");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = UserPrincipal.create(sampleUser);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        when(tokenProvider.generateToken(any())).thenReturn("mock.jwt.token");
        when(tokenProvider.getExpirationMs()).thenReturn(86400000L);
        when(tokenProvider.getRefreshExpirationMs()).thenReturn(604800000L);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("mock-refresh-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail("newstudent@collegeclub.edu");
        when(userService.mapToResponse(any(User.class))).thenReturn(userResponse);

        AuthResponse result = authService.register(request);

        assertNotNull(result);
        assertEquals("mock.jwt.token", result.getAccessToken());
        assertEquals("mock-refresh-token", result.getRefreshToken());
    }

    @Test
    void login_ShouldSucceed_WhenCredentialsValid() {
        LoginRequest request = new LoginRequest("student@collegeclub.edu", "Secret123!");

        Authentication auth = mock(Authentication.class);
        UserPrincipal principal = UserPrincipal.create(sampleUser);
        when(auth.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(auth);

        when(userService.getUserEntity(10L)).thenReturn(sampleUser);
        when(tokenProvider.generateToken(any())).thenReturn("mock.jwt.token");
        when(tokenProvider.getExpirationMs()).thenReturn(86400000L);
        when(tokenProvider.getRefreshExpirationMs()).thenReturn(604800000L);

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("mock-refresh-token");
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(refreshToken);

        UserResponse userResponse = new UserResponse();
        userResponse.setEmail(sampleUser.getEmail());
        when(userService.mapToResponse(sampleUser)).thenReturn(userResponse);

        AuthResponse result = authService.login(request);

        assertNotNull(result);
        assertEquals("mock.jwt.token", result.getAccessToken());
    }
}
