package com.projekt.spotifydata.repository;

import com.projekt.spotifydata.entity.Country;
import com.projekt.spotifydata.entity.PlayList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayListRepository extends JpaRepository<PlayList, Long> {

    List<PlayList> findByAlbumName(String albumName);
    void deleteByPlaylist(Country playlist);
}
