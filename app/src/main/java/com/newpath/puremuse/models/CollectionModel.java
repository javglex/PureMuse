package com.newpath.puremuse.models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import com.newpath.puremuse.utils.Converters;

import java.util.ArrayList;

@Entity(tableName = "collection")
public class CollectionModel {

    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "collection_name")
    public String collectionName;
    @ColumnInfo(name = "song_list")
    protected ArrayList<AudioFileModel> songList;

    public CollectionModel(String name){
        this.collectionName = name;
        songList = new ArrayList<>();
    }

    public CollectionModel(String name, ArrayList<AudioFileModel> songList){
        this.collectionName = name;
        this.songList = songList;
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

    public ArrayList<AudioFileModel> getSongList(){
        return this.songList;
    }

}
