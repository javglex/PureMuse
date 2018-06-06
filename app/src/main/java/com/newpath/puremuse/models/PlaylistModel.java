package com.newpath.puremuse.models;

import java.util.ArrayList;

/**
 * Holds songs for user created collections. Could refactor to extend AlbumModel/ A common collection module?
 */

public class PlaylistModel extends CollectionModel {


    public PlaylistModel(String name){
        super(name);
    }

    public PlaylistModel(String name, ArrayList<AudioFileModel> songList){
        super(name,songList);
    }

    public void addSong(AudioFileModel song){
        if (this.songList==null)
            this.songList = new ArrayList<>();
        this.songList.add(song);
    }

    public AudioFileModel getSong(int index){
        return this.songList.get(index);
    }

    public void removeSong(int index){
        this.songList.remove(index);
    }

    public void removeSongList(){
        this.songList.clear();
        this.songList = new ArrayList<>();
    }



}
