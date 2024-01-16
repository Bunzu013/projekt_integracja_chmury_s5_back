package com.projekt.spotifydata.service;

import com.projekt.spotifydata.entity.User;
import com.projekt.spotifydata.repository.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        // Przygotowanie obiektu UserDetails na podstawie danych użytkownika
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER")); // Dodanie roli użytkownika
        return new org.springframework.security.core.userdetails.User(
                user.getUserName(),
                user.getPassword(),
                authorities
        );
    }


    public User findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User createUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        return userRepository.save(user);
    }

    // Inne metody związane z obsługą użytkowników, np. pobieranie wszystkich użytkowników, usuwanie użytkownika itp.
}