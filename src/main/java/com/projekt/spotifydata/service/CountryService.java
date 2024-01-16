package com.projekt.spotifydata.service;

import com.projekt.spotifydata.entity.Country;
import com.projekt.spotifydata.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    @Autowired
    private CountryRepository countryRepository;

    public List<Country> getAllCountries() {
        return countryRepository.findAll();
    }

    public Country getCountryByName(String countryName) {
        return countryRepository.findByCountryName(countryName);
    }
}
