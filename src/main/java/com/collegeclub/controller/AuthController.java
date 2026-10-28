package com.collegeclub.controller;

import com.collegeclub.dto.request.LoginRequest;
import com.collegeclub.dto.request.RefreshTokenRequest;
import com.collegeclub.dto.request.RegisterRequest;
import com.collegeclub.dto.response.ApiResponse;
import com.collegeclub.dto.response.AuthResponse;
import com.collegeclub.dto.response.UserResponse;
import com.collegeclub.security.UserPrincipal;
import com.collegeclub.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration, authentication, token refresh, and logout")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register new student account")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request,
                                                              HttpServletResponse response) {
        AuthResponse authResponse = authService.register(request);
        setHttpOnlyCookie(response, "refreshToken", authResponse.getRefreshToken(), 7 * 24 * 3600);
        return ResponseEntity.ok(ApiResponse.ok("Registration successful", authResponse));
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return JWT access and refresh tokens")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request,
                                                           HttpServletResponse response) {
        AuthResponse authResponse = authService.login(request);
        setHttpOnlyCookie(response, "refreshToken", authResponse.getRefreshToken(), 7 * 24 * 3600);
        return ResponseEntity.ok(ApiResponse.ok("Login successful", authResponse));
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh JWT access token using valid refresh token")
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestBody(required = false) RefreshTokenRequest request,
            @CookieValue(value = "refreshToken", required = false) String cookieRefreshToken) {

        String token = (request != null && request.getRefreshToken() != null && !request.getRefreshToken().isBlank())
                ? request.getRefreshToken() : cookieRefreshToken;

        if (token == null || token.isBlank()) {
            return ResponseEntity.badRequest().body(ApiResponse.error("Refresh token must be provided in body or cookie."));
        }

        AuthResponse authResponse = authService.refreshToken(new RefreshTokenRequest(token));
        return ResponseEntity.ok(ApiResponse.ok("Token refreshed successfully", authResponse));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user and revoke refresh token")
    public ResponseEntity<ApiResponse<Void>> logout(
            @RequestBody(required = false) RefreshTokenRequest request,
            @CookieValue(value = "refreshToken", required = false) String cookieRefreshToken,
            HttpServletResponse response) {

        String token = (request != null && request.getRefreshToken() != null)
                ? request.getRefreshToken() : cookieRefreshToken;

        authService.logout(token);
        clearCookie(response, "refreshToken");

        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
    }

    @GetMapping("/me")
    @Operation(summary = "Get currently authenticated user details")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(@AuthenticationPrincipal UserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).body(ApiResponse.error("Not authenticated"));
        }
        UserResponse user = authService.getCurrentUser(principal);
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    private void setHttpOnlyCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Can be enabled in prod with HTTPS
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    private void clearCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, "");
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
