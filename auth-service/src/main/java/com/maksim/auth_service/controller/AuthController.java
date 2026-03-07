package com.maksim.auth_service.controller;


import com.maksim.auth_service.dto.TokenRequest;
import com.maksim.auth_service.dto.RegisterRequest;
import com.maksim.auth_service.dto.TokenResponse;
import com.maksim.auth_service.dto.ValidateRequest;
import com.maksim.auth_service.service.AuthException;
import com.maksim.auth_service.service.UserService;
import com.maksim.auth_service.service.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final JwtService jwtService;

    private final UserService userService;

    public AuthController(JwtService jwtService, UserService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        try {
            userService.register(request);
            return ResponseEntity.ok("User registered successfully");
        } catch (AuthException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @PostMapping("/token")
    public ResponseEntity<Object> getToken(@RequestBody TokenRequest request) {
        try {
            var user = userService.authenticate(request.getEmail(), request.getPassword());
            String token = jwtService.generateToken(user.getHandle(), user.getId());
            return ResponseEntity.ok(new TokenResponse(token));
        } catch (AuthException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    // for gateway
    @PostMapping("/validate")
    public ResponseEntity<Object> validate(@RequestBody ValidateRequest validateRequest) {
        var response = jwtService.validate(validateRequest.getToken());
        if (response == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(response);
    }



}
