package com.projekt.spotifydata.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Albums")
public class Album {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "genreid")
    private Genre genreId;
    @ManyToMany
   // @OnDelete(action = OnDeleteAction.CASCADE)
    private List<PlayList> playlists;


}
