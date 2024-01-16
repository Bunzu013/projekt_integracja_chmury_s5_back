package com.projekt.spotifydata.controller;

import com.projekt.spotifydata.entity.Country;
import com.projekt.spotifydata.repository.CountryRepository;
import com.projekt.spotifydata.service.MethodsService;
import com.projekt.spotifydata.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private MethodsService methodsService;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private PlaylistService playlistService;

    public static class CountryNameDto {
        private String countryName;

        // Gettery i settery
        public String getCountryName() {
            return countryName;
        }

        public void setCountryName(String countryName) {
            this.countryName = countryName;
        }
    }

    @GetMapping("/update/{countryName}")
    public ResponseEntity<?> updateCountry(@PathVariable String countryName) {
        Country country = countryRepository.findByCountryName(countryName);
        String responseWithTimestamp = "";
        String response ="";
        if(country != null) {
            methodsService.deleteCountryByName(country.getCountryName());
            // Dodanie znacznika czasu

            methodsService.addCountry(country);
            playlistService.getByName(countryName);
            // Utworzenie odpowiedzi z dodanym znacznikiem czasu
            response = "Country updated";
        }
        LocalDateTime timestamp = LocalDateTime.now();
        responseWithTimestamp = response + ", Timestamp: " + timestamp.toString();
        return ResponseEntity.ok(responseWithTimestamp);
    }
}
