package com.newpath.puremuse.models;

import java.util.ArrayList;

public class CollectionModel {

    String collectionName;
    ArrayList<AudioFileModel> songList;

    public CollectionModel(){}

    public void setSongList(ArrayList<AudioFileModel> songList){
        if (songList==null)
            this.songList = new ArrayList<AudioFileModel>();
        else
            this.songList = songList;
    }


    public void setCollectionName(String name){
        this.collectionName = name;
    }

    public String getCollectionName(){
        return this.collectionName;
    }

    public ArrayList<AudioFileModel> getSongList(){
        return this.songList;
    }

}
