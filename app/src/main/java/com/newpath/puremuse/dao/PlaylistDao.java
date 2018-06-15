package com.newpath.puremuse.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.newpath.puremuse.models.PlaylistModel;

import java.util.List;

@Dao
public interface PlaylistDao {

    @Query("SELECT * FROM playlist")
    List<PlaylistModel> getAll();

    @Query("SELECT * FROM playlist where collection_name LIKE :collection_name")
    PlaylistModel findByName(String collection_name);

    @Query("SELECT * FROM playlist where uid LIKE :uid")
    PlaylistModel findByUid(int uid);

    @Query("SELECT COUNT(*) from playlist")
    int countPlaylists();

    @Insert
    void insertAll(PlaylistModel... playlist);

    @Delete
    void delete(PlaylistModel user);
}