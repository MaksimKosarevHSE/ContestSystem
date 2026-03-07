package com.maksim.auth_service.service;

import com.maksim.auth_service.dto.RegisterRequest;
import com.maksim.auth_service.entity.User;
import com.maksim.auth_service.repository.UserRepository;
import org.springframework.boot.autoconfigure.jmx.ParentAwareNamingStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    public UserService(UserRepository userRepository, PasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public User authenticate(String email, String password){
        var dbUser = userRepository.findByEmail(email).orElseThrow(() -> new AuthException("User with email " + email + " is not found"));
        if (!encoder.matches(password, dbUser.getPasswordHash())) throw new AuthException("Incorrect password");
        return dbUser;
    }

    public void register(RegisterRequest registerRequest) {
        String email = registerRequest.getEmail();
        String handle = registerRequest.getHandle();

        if (userRepository.existsByEmail(email))
            throw new AuthException("User with email " + email + " already exists");

        if (userRepository.existsByHandle(handle))
            throw new AuthException("User with handle " + handle + " already exists");

        var user = new User();
        user.setEmail(email);
        user.setHandle(handle);
        user.setPasswordHash(encoder.encode(registerRequest.getPassword()));
        userRepository.save(user);
    }
}
