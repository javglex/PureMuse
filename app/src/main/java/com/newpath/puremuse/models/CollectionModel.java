package com.newpath.puremuse.models;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.util.ArrayList;

@Entity(tableName = "collection")
public class CollectionModel {

    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "collection_name")
    public String collectionName;
    @ColumnInfo(name = "collection_artist")
    public String collectionArtist;
    @ColumnInfo(name = "song_list")
    protected ArrayList<AudioFileModel> songList;

    public CollectionModel(String name){
        this.collectionName = name;
        this.collectionArtist = "";
        songList = new ArrayList<>();
    }

    public CollectionModel(String name, String artist, ArrayList<AudioFileModel> songList){
        this.collectionName = name;
        this.songList = songList;
        this.collectionArtist=artist;
    }

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

    public String getCollectionArtist() {
        return collectionArtist;
    }

    public void setCollectionArtist(String collectionArtist) {
        this.collectionArtist = collectionArtist;
    }

    public ArrayList<AudioFileModel> getSongList(){
        return this.songList;
    }

}
