package com.newpath.puremuse.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

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

    @Query("DELETE FROM playlist")
    public void nukeTable();

    @Query("SELECT COUNT(*) from playlist")
    int countPlaylists();

    @Insert
    void insertAll(PlaylistModel... playlist);

    @Insert
    void insertPlaylist(PlaylistModel playlist);

    @Delete
    void delete(PlaylistModel user);

}