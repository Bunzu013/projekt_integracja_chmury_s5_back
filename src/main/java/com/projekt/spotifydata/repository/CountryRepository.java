package com.projekt.spotifydata.repository;

import com.projekt.spotifydata.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CountryRepository extends JpaRepository<Country, Integer> {
    Country findByCountryName(String countryName);
    List<Country> findByCountryNameContaining(String countryName);

    void deleteByCountryName(String countryName);

    boolean existsByCountryName(String countryName);
}
