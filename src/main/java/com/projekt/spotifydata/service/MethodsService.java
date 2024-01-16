package com.projekt.spotifydata.service;

import com.projekt.spotifydata.entity.Album;
import com.projekt.spotifydata.entity.Country;
import com.projekt.spotifydata.entity.Genre;
import com.projekt.spotifydata.entity.PlayList;
import com.projekt.spotifydata.repository.AlbumRepository;
import com.projekt.spotifydata.repository.CountryRepository;
import com.projekt.spotifydata.repository.GenreRepository;
import com.projekt.spotifydata.repository.PlayListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MethodsService {
        @Autowired
        CountryRepository countryRepository;
        @Autowired
        PlayListRepository playListRepository;
        @Autowired
        AlbumRepository albumRepository;
        @Autowired
    GenreRepository genreRepository;

        //wyswietlenie wszystkich krajów z możliwościa wyszukania tylko jednego poprzez dodanie param

        public ResponseEntity<?> getAllCountries() {
            try {
                List<Country> countries = countryRepository.findAll();//List<Country> countries = new ArrayList<>();
                if (countries.isEmpty()) {
                    return new ResponseEntity<>("Baza danych krajów jest pusta", HttpStatus.NO_CONTENT);
                }
                List<Map<String, Object>> countriesData = new ArrayList<>();
                for (Country country : countries) {
                    Map<String, Object> countryData = new HashMap<>();
                    countryData.put("countryName", country.getCountryName());
                    countryData.put("countryID", country.getCountryID());
                    countryData.put("updateData",country.getUpdateData());
                    countriesData.add(countryData);
                }
                return new ResponseEntity<>(countriesData, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        //Wyszukanie tylko jednego kraju ponazwie

        public ResponseEntity<?> getCountryByName(@PathVariable("countryName") String countryName) {
            try {
                Country country = countryRepository.findByCountryName(countryName);
                if (country != null) {
                    Map<String, Object> countryData = new HashMap<>();
                    countryData.put("countryName", country.getCountryName());
                    countryData.put("countryID", country.getCountryID());
                   countryData.put("updateData",country.getUpdateData());
                    return new ResponseEntity<>(countryData, HttpStatus.OK);
                } else {
                    return new ResponseEntity<>(HttpStatus.NOT_FOUND);
                }
            } catch (Exception e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }


        public ResponseEntity<?> addCountry(@RequestBody Country country) {
            try {
                // Sprawdź, czy kraj już istnieje w bazie danych
                boolean countryExists = countryRepository.existsByCountryName(country.getCountryName());

                if (countryExists) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("Podany kraj juz istnieje");
                }
                LocalDateTime timestamp = LocalDateTime.now();
                Country _country = new Country();
                _country.setCountryName(country.getCountryName());
                _country.setCountryID(country.getCountryID());
                _country.setUpdateData(timestamp);
                countryRepository.save(_country);
                return ResponseEntity.status(HttpStatus.CREATED).body(_country + "\nKraj został dodany.");
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Wystąpił błąd podczas dodawania kraju.");
            }
        }

    //usuwanie wszystkich krajów

    public ResponseEntity<?> deleteAllCountries() {
        try {
            List<Country> countries = countryRepository.findAll();
            for (Country country : countries) {
                List<PlayList> playlists = new ArrayList<>(country.getPlaylists()); // Tworzenie kopii listy playlist
                for (PlayList playlist : playlists) {
                    List<Album> albums = new ArrayList<>(playlist.getAlbums()); // Tworzenie kopii listy albumów
                    for (Album album : albums) {
                        playlist.getAlbums().remove(album);
                        album.getPlaylists().remove(playlist);

                    }
                    playListRepository.delete(playlist);
                }
                countryRepository.delete(country);
            }
            return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("Kraje zostaly usuniete");
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> deleteCountryByName(@PathVariable("countryName") String countryName) {
        try {
            Country country = countryRepository.findByCountryName(countryName);
            if (country != null) {
                List<PlayList> playlists = new ArrayList<>(country.getPlaylists()); // Tworzenie kopii listy playlist
                for (PlayList playlist : playlists) {
                    List<Album> albums = new ArrayList<>(playlist.getAlbums()); // Tworzenie kopii listy albumów
                    for (Album album : albums) {
                        playlist.getAlbums().remove(album);
                        album.getPlaylists().remove(playlist);
                    }
                    playListRepository.delete(playlist);
                }
                countryRepository.delete(country);
                return  ResponseEntity.status(HttpStatus.NO_CONTENT).body("Kraje zostaly usuniete");
            } else {
                return  ResponseEntity.status(HttpStatus.NOT_FOUND).body("Nie znaleziono kraju");
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> getData(@PathVariable("countryName") String countryName) {
        try {
            Country country = countryRepository.findByCountryName(countryName);
            Map<String, Integer> genreCounts = new HashMap<>();
            Map<String, Integer> genres = new HashMap<>();
           // Map<String, String> albumsGenre = new HashMap<>();
            if (country != null) {
                List<PlayList> playlists = new ArrayList<>(country.getPlaylists()); // Tworzenie kopii listy playlist
                if(playlists != null) {
                    for (PlayList playlist : playlists) {
                        List<Album> albums = new ArrayList<>(playlist.getAlbums()); // Tworzenie kopii listy albumów
                        for (Album album : albums) {
                            Integer genreIDint = album.getGenreId().getId();
                            Genre genreName = genreRepository.getById(genreIDint);
                            //gatunki
                            String genreNameString = genreName.getGenreName();
                            genres.put(genreNameString, genreIDint);
                            if (genreCounts.containsKey(genreNameString)) {
                                if (playlist.getAlbumName() != null) {
                                    genreCounts.put(genreNameString, genreCounts.get(genreNameString) + 1);
                                }
                            } else {
                                genreCounts.put(genreNameString, 1);
                            }
                        }
                    }
                }
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }return new ResponseEntity<>(genreCounts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }
}
