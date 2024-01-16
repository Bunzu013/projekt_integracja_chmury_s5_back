package com.projekt.spotifydata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "playLists")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String albumName;
    @Column(nullable = false)
    private Integer occureCount;

    @ManyToOne
    @JoinColumn(name = "PlaylistID")
    // @OnDelete(action = OnDeleteAction.CASCADE)
    private Country playlist;


    @ManyToMany(mappedBy = "playlists")
    private List<Album> albums;
    public PlayList(Long id, String albumName, Integer occureCount, Country playlist) {
        this.id = id;
        this.albumName = albumName;
        this.occureCount = occureCount;
        this.playlist = playlist;
    }
}
