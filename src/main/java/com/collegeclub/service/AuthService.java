package com.collegeclub.service;

import com.collegeclub.dto.request.LoginRequest;
import com.collegeclub.dto.request.RefreshTokenRequest;
import com.collegeclub.dto.request.RegisterRequest;
import com.collegeclub.dto.response.AuthResponse;
import com.collegeclub.dto.response.UserResponse;
import com.collegeclub.entity.RefreshToken;
import com.collegeclub.entity.User;
import com.collegeclub.enums.Role;
import com.collegeclub.exception.BadRequestException;
import com.collegeclub.exception.UnauthorizedException;
import com.collegeclub.repository.RefreshTokenRepository;
import com.collegeclub.repository.UserRepository;
import com.collegeclub.security.JwtTokenProvider;
import com.collegeclub.security.UserPrincipal;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;
    private final UserService userService;
    private final ActivityLogService activityLogService;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       RefreshTokenRepository refreshTokenRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider,
                       UserService userService,
                       ActivityLogService activityLogService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
        this.userService = userService;
        this.activityLogService = activityLogService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        if (userRepository.existsByEmail(email)) {
            throw new BadRequestException("An account with email " + email + " already exists.");
        }

        if (request.getStudentId() != null && !request.getStudentId().isBlank()) {
            if (userRepository.existsByStudentId(request.getStudentId().trim())) {
                throw new BadRequestException("Student ID " + request.getStudentId().trim() + " is already registered.");
            }
        }

        // Enforcement: Public registration CANNOT create administrative accounts
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setStudentId(request.getStudentId() != null ? request.getStudentId().trim() : null);
        user.setDepartment(request.getDepartment() != null ? request.getDepartment().trim() : null);
        user.setPhone(request.getPhone() != null ? request.getPhone().trim() : null);
        user.setRole(Role.STUDENT);
        user.setActive(true);

        user = userRepository.save(user);

        activityLogService.log(user, "USER_REGISTERED", "USER", user.getId(), "Student registration completed: " + user.getEmail(), null);

        // Authenticate new user directly
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        String accessToken = tokenProvider.generateToken(authentication);
        RefreshToken refreshToken = createRefreshToken(user);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                tokenProvider.getExpirationMs() / 1000,
                userService.mapToResponse(user)
        );
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        String email = request.getEmail().trim().toLowerCase();

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, request.getPassword())
        );

        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        User user = userService.getUserEntity(principal.getId());

        if (!user.isActive()) {
            throw new UnauthorizedException("This account has been deactivated by a system administrator.");
        }

        String accessToken = tokenProvider.generateToken(authentication);
        RefreshToken refreshToken = createRefreshToken(user);

        activityLogService.log(user, "USER_LOGIN", "USER", user.getId(), "User logged in successfully", null);

        return new AuthResponse(
                accessToken,
                refreshToken.getToken(),
                tokenProvider.getExpirationMs() / 1000,
                userService.mapToResponse(user)
        );
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        String tokenStr = request.getRefreshToken();
        RefreshToken refreshToken = refreshTokenRepository.findByToken(tokenStr)
                .orElseThrow(() -> new UnauthorizedException("Refresh token is invalid or expired."));

        if (refreshToken.isRevoked() || refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new UnauthorizedException("Refresh token is expired or revoked. Please log in again.");
        }

        User user = refreshToken.getUser();
        if (!user.isActive()) {
            throw new UnauthorizedException("Account is deactivated.");
        }

        String newAccessToken = tokenProvider.generateTokenFromUserId(user.getId(), user.getEmail(), user.getRole().name());

        return new AuthResponse(
                newAccessToken,
                refreshToken.getToken(),
                tokenProvider.getExpirationMs() / 1000,
                userService.mapToResponse(user)
        );
    }

    @Transactional
    public void logout(String refreshTokenStr) {
        if (refreshTokenStr != null && !refreshTokenStr.isBlank()) {
            refreshTokenRepository.findByToken(refreshTokenStr).ifPresent(refreshTokenRepository::delete);
        }
    }

    @Transactional(readOnly = true)
    public UserResponse getCurrentUser(UserPrincipal principal) {
        return userService.getUserById(principal.getId());
    }

    private RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString() + "-" + UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(tokenProvider.getRefreshExpirationMs()));
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);
    }
}
