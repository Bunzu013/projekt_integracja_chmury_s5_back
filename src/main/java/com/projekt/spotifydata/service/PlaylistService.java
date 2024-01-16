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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@Service
public class PlaylistService {
    @Autowired
    private PlayListRepository playListRepository;
    @Autowired
    CountryRepository countryRepository;
    @Autowired
    AlbumRepository albumRepository;
    @Autowired
    GenreRepository genreRepository;

    public List<PlayList> getByAlbumName(String albumName) {
        return playListRepository.findByAlbumName(albumName);
    }

    public Map<String, Integer> getByName(@PathVariable("countryName") String country_name) {
        // String country_id = countryRepository.getCountryID(country_name);

        Country country = countryRepository.findByCountryName(country_name);
        //String cID = country.getCountryID();
        HttpRequest acctoken = HttpRequest.newBuilder()
                .uri(URI.create("https://accounts.spotify.com/api/token"))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .POST(HttpRequest.BodyPublishers.ofString("grant_type=client_credentials&client_id=e68d79389f1244b0b907fb2173ecbefe&client_secret=a0be6c1860474404873915ee5467ec12"))
                .build();

        HttpResponse<String> response1 = null;
        try {
            response1 = HttpClient.newHttpClient().send(acctoken, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {

            e.printStackTrace();
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        var token = response1.body().split(":")[1].split(",")[0].replace("\"", "");

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.spotify.com/v1/playlists/" + country.getCountryID() + "/tracks?fields=items%28track%28album%28name%29%29%29"))
                .header("Authorization", "Bearer " + token)
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();
        HttpResponse<String> response = null;
        try {
            response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //store names of albums in map of albums and counts
        String[] albums = response.body().split("name");
        for (int i = 1; i < albums.length; i++) {
            albums[i] = albums[i].split("\"")[2];
        }


        //count the number of times each album appears
        Map<String, Integer> albumCounts = new HashMap<>();
        for (int i = 1; i < albums.length; i++) {
            if (albums[i] != null) {
                if (albumCounts.containsKey(albums[i])) {
                    albumCounts.put(albums[i], albumCounts.get(albums[i]) + 1);
                } else {
                    albumCounts.put(albums[i], 1);
                }
            }
        }

        //GATUNKI
        Map<String, Integer> genreCounts = new HashMap<>();
        Map<String, Integer> genres = new HashMap<>();
        Map<String, String> albumsGenre = new HashMap<>();

        //get genreid and genrename for each album from deezer api
        for (String s : albumCounts.keySet()) {
            //replace spaces with %20
            s = s.replace(" ", "%20");
            //get album name and genre id

            HttpRequest request2 = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.deezer.com/search/album?q=" + s + "&limit=1"))
                    .header("Accept", "application/json")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response2 = null;
            try {
                response2 = HttpClient.newHttpClient().send(request2, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //get  genre_id
            String[] genre = response2.body().split("genre_id");
            //if index 1 is out of bounds, skip
            if (genre.length < 2) {
                continue;
            }
            String genreID = genre[1].split(",")[0];
            //System.out.println(albumName);
            //get rid of spare characters from genreID
            genreID = genreID.replace(":", "");
            genreID = genreID.replace("\"", "");
            //save genre id as an int
            int genreIDint = Integer.parseInt(genreID);
            //System.out.println(genreIDint);
            //get genre name for genreIDint
            HttpRequest request3 = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.deezer.com/genre/" + genreIDint))
                    .header("Accept", "application/json")
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();
            HttpResponse<String> response3 = null;
            try {
                response3 = HttpClient.newHttpClient().send(request3, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String[] genreName = response3.body().split("name");
            if (genreName.length < 2) {
                continue;
            }

            String genreNameString = genreName[1].split("\"")[2];
            //replace %20 with spaces in album name
            s = s.replace("%20", " ");

            //gatunki
            genres.put(genreNameString,genreIDint);
            //nazwa albumu i jego gatunek
            albumsGenre.put(s,genreNameString);

            // System.out.println(s + ": " + genreNameString + " (" + genreIDint + ")");
            if (genreCounts.containsKey(genreNameString)) {
                if (albumCounts.get(s) != null) {
                    genreCounts.put(genreNameString, genreCounts.get(genreNameString) + 1);
                }
            } else {
                genreCounts.put(genreNameString, 1);
            }

        }


        playListRepository.saveAll(albumCounts.entrySet().stream()
                .map(entry -> new PlayList(null, entry.getKey(), entry.getValue(), country))
                .collect(Collectors.toList()));
        for (String genre : genres.keySet()) {
            if (!genreRepository.existsByGenreName(genre)) {
                Genre newGenre = new Genre(genres.get(genre),genre);
               // newGenre.setGenreName(genre);
                genreRepository.save(newGenre);
            }
        }
        albumsGenre.forEach((albumNameKey, genreName) -> {
            List<PlayList> albumsName = playListRepository.findByAlbumName(albumNameKey);
            if (!albumsName.isEmpty()) {
                List<Album> albumsNameTemp = albumRepository.findByPlaylists_AlbumName(albumNameKey);
                if (albumsNameTemp.isEmpty()) {
                    List<Genre> genresName = genreRepository.findByGenreName(genreName);
                    if (!genresName.isEmpty()) {
                        Genre genre = genresName.get(0);
                        Album album = new Album();
                        album.setGenreId(genre);
                        album.setPlaylists(albumsName);
                        albumRepository.save(album);
                    }
                }
            }
        });

        return genreCounts;
    }
}