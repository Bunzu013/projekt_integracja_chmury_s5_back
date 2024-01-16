package com.projekt.spotifydata.repository;

import com.projekt.spotifydata.entity.Album;
import com.projekt.spotifydata.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    List<Album> findByPlaylists_AlbumName(String albumName);
    void deleteByPlaylists(PlayList playlist);

}
