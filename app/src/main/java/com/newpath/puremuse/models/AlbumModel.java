package com.newpath.puremuse.models;

import android.provider.MediaStore;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * A container for a list for songs that share an album
 */
public class AlbumModel {

    String albumName;
    ArrayList<AudioFileModel> songList;

    public AlbumModel(){

    }

    public AlbumModel(String name, ArrayList<AudioFileModel> songs){
        this.albumName = name;
        this.songList = songs;
    }

    public void setAlbumName(String name){
        this.albumName = name;
    }

    public void setSongList(ArrayList<AudioFileModel> songList){
        this.songList = songList;
    }

    public void addSong(AudioFileModel song){
        if (this.songList==null)
            this.songList = new ArrayList<>();
        this.songList.add(song);
    }

    public ArrayList<AudioFileModel> getSongList(){
        return this.songList;
    }

    public String getAlbumName(){
        return this.albumName;
    }
}
