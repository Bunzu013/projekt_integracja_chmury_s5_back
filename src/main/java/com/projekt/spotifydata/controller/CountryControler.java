package com.projekt.spotifydata.controller;

import com.projekt.spotifydata.entity.Country;
import com.projekt.spotifydata.repository.AlbumRepository;
import com.projekt.spotifydata.repository.CountryRepository;
import com.projekt.spotifydata.repository.PlayListRepository;
import com.projekt.spotifydata.service.MethodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api")
//   @PreAuthorize("@user.isAdmin()")
public class CountryControler {
    @Autowired
    CountryRepository countryRepository;
    @Autowired
        PlayListRepository playListRepository;
    @Autowired
       AlbumRepository albumRepository;

    @Autowired
        MethodsService methodsService;

  //wyswietlenie wszystkich krajów z możliwościa wyszukania tylko jednego poprzez dodanie param
    @GetMapping("/countries")
    public ResponseEntity<?> getAllCountries() {
        return methodsService.getAllCountries();
    }
    //Wyszukanie tylko jednego kraju ponazwie
    @GetMapping("/countries/{countryName}")
    public ResponseEntity<?> getCountryByName(@PathVariable("countryName") String countryName) {
        return methodsService.getCountryByName(countryName);
    }

    @PostMapping("/countries")
    public ResponseEntity<?> addCountry(@RequestBody Country country) {
       return methodsService.addCountry(country);
    }

    //usuwanie wszystkich krajów
   @DeleteMapping("/countries")
    public ResponseEntity<?> deleteAllCountries() {
       return methodsService.deleteAllCountries();
    }

    @DeleteMapping("/countries/{countryName}")
    public ResponseEntity<?> deleteCountryByName(@PathVariable("countryName") String countryName) {
        return methodsService.deleteCountryByName(countryName);
    }


}
