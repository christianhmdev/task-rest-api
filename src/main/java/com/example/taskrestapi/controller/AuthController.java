package com.example.taskrestapi.controller;

import com.example.taskrestapi.model.User;
import com.example.taskrestapi.repository.UserRepository;
import com.example.taskrestapi.security.UserDetailsImpl;
import com.example.taskrestapi.security.authRequestBody.LoginRequest;
import com.example.taskrestapi.security.authRequestBody.SignupRequest;
import com.example.taskrestapi.security.jwt.JwtUtils;
import com.example.taskrestapi.security.responseformat.JwtResponse;
import com.example.taskrestapi.security.responseformat.MessageResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/taskmanager/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder encoder;
    @Autowired
    JwtUtils jwtUtils;

    @Operation(summary = "Authenticate a user using username and password", description = "Authenticate a user using username and password")
    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        org.springframework.security.core.Authentication authentication = authenticationManager.authenticate
                (new UsernamePasswordAuthenticationToken
                        (loginRequest.getUsername(),
                                loginRequest.getPassword()));

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);
        String jwt = jwtUtils.generateJwtToken(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl)
                authentication.getPrincipal();

        return ResponseEntity
                .ok(new JwtResponse(jwt, userDetails.getId(),
                        userDetails.getUsername(),
                        userDetails.getEmail()));
    }

    @Operation(summary = "Register a new user with a unique username and email", description = "Register a new user with a unique username and email")
    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {

        if (userRepository.existsByUsername(signUpRequest
                .getUsername())) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse
                            ("Error: username is already taken!"));
        }

        if (userRepository.existsByEmail
                (signUpRequest.getEmail())) {

            return ResponseEntity.badRequest()
                    .body(new MessageResponse
                            ("Error: Email is already in use!"));
        }

        // Create new user account
        User user = new User(signUpRequest.getUsername(),
                signUpRequest.getEmail(),
                encoder.encode(signUpRequest.getPassword()));

        userRepository.save(user);

        return ResponseEntity
                .ok(new MessageResponse("user registered successfully!"));
    }
}
