package com.projekt.spotifydata.controller;

import com.projekt.spotifydata.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/playlist")
public class PlaylistController {
    @Autowired
    private PlaylistService playlistService;

    @GetMapping("/{countryName}")
    @CrossOrigin
    public ResponseEntity<?> getPlaylistByCountryName(@PathVariable("countryName") String countryName) {
        Map<String, Integer> playlistData = playlistService.getByName(countryName);
        return ResponseEntity.ok(playlistData);
    }

}

