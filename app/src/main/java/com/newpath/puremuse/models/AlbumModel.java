package com.newpath.puremuse.models;

import android.provider.MediaStore;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A container for a list for songs that share an album
 */
public class AlbumModel extends CollectionModel {


    public AlbumModel(String name){
        super(name);
    }

    public AlbumModel(String name, ArrayList<AudioFileModel> songs){
        super(name,songs);
    }




}
