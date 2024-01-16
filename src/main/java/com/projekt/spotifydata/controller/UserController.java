package com.projekt.spotifydata.controller;

import com.projekt.spotifydata.configuration.JwtTokenUtil;
import com.projekt.spotifydata.entity.Country;
import com.projekt.spotifydata.entity.User;
import com.projekt.spotifydata.repository.CountryRepository;
import com.projekt.spotifydata.repository.UserRepository;
import com.projekt.spotifydata.service.MethodsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final JwtTokenUtil jwtTokenUtil;

    private final MethodsService methodsService;
    private final CountryRepository countryRepository;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder,JwtTokenUtil jwtTokenUtil, MethodsService methodsService, CountryRepository countryRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.methodsService = methodsService;
        this.countryRepository = countryRepository;
    }

   @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return ResponseEntity.badRequest().body(errors);
        }

        User existingUser = userRepository.findByUserName(user.getUserName());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Signup failed: email already in use");
        }
        try {
            String hashedPassword = passwordEncoder.encode(user.getPassword());
            user.setPassword(hashedPassword);
            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User created successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Signup error");
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        try {
            User existingUser = userRepository.findByUserName(user.getUserName());
            if (existingUser == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: user not found");
            }

            if (!passwordEncoder.matches(user.getPassword(), existingUser.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Login failed: incorrect password");
            }

            String token = jwtTokenUtil.generateJwtToken(existingUser); // Generate the JWT token
            return ResponseEntity.ok(token);

            //return ResponseEntity.ok("Login successful");
        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/getData/{countryName}")
    public ResponseEntity<?> getData(@PathVariable String countryName) {
            Country country = countryRepository.findByCountryName(countryName);
            return methodsService.getData(countryName);
    }

}

