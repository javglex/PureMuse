package com.newpath.puremuse.models;

import androidx.room.Entity;

import java.util.ArrayList;

/**
 * A container for a list for songs that share an album
 */
@Entity(tableName = "album")
public class AlbumModel extends CollectionModel {


    public AlbumModel(String name){
        super(name);
    }

    public AlbumModel(String name, String artist, ArrayList<AudioFileModel> songs){
        super(name,artist,songs);
    }




}
