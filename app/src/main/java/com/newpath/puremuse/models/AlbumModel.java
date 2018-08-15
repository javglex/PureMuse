package com.newpath.puremuse.models;

import android.arch.persistence.room.Entity;
import android.provider.MediaStore;

import java.lang.reflect.Array;
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
