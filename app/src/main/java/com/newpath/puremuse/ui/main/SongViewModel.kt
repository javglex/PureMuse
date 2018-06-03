package com.newpath.puremuse.ui.main

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.newpath.puremuse.models.AudioFileModel
import com.newpath.puremuse.helpers.AudioFileScanner
import com.newpath.puremuse.models.AlbumModel
import com.newpath.puremuse.models.CollectionModel
import com.newpath.puremuse.models.PlaylistModel
import java.util.HashMap

/**
 * Keeps track of list of songs, and currently active lists.
 * i.e if in a playlist, album, or search results
 */

class SongViewModel : ViewModel() {

    private val TAG:String = "SongViewModel";

    //List of scanned songs in device
    private val _scannedSongList = MutableLiveData<ArrayList<AudioFileModel>>()
    val scannedSongList: LiveData<ArrayList<AudioFileModel>>
        get() = _scannedSongList

    //List of searched songs from list
    private val _searchedSongList = MutableLiveData<ArrayList<AudioFileModel>>()
    val searchedSongList: LiveData<ArrayList<AudioFileModel>>
        get() = _searchedSongList

    //key: album name, value: list of songs respective to album name
    private val _albumList = MutableLiveData<ArrayList<out CollectionModel>>()
    val albumList : LiveData<ArrayList<out CollectionModel>>
        get() = _albumList

    //key: playlist name, value: list of songs respective to album name
    private val _collectionList = MutableLiveData<ArrayList<out CollectionModel>>()
    val collectionList : LiveData<ArrayList<out CollectionModel>>
        get() = _collectionList


    init {
        Log.d(TAG,"init fired");
        _scannedSongList.value = ArrayList<AudioFileModel>()
        _searchedSongList.value = ArrayList<AudioFileModel>()
        _albumList.value = ArrayList<AlbumModel>()
        _collectionList.value=ArrayList<PlaylistModel>()

    }

    /**
     * updates list of songs in device
     */
    fun updateScannedSongList(list: ArrayList<AudioFileModel>){
        Log.d(TAG,"updateScannedSongList data set changed");
        Log.d(TAG,"updateScannedSongList size: " + list.size);
        _scannedSongList.value = list;
        Log.d(TAG,"_scannedSongList size: " + _scannedSongList.value!!.size);
        generateAlbums()
        initSearchedSongList();

    }

    /**
     * updates list of songs you can search from
     */
    private fun initSearchedSongList(){
        _searchedSongList.value = scannedSongList.value;
    }

    /**
     * updates list of songs that have been successfully found from search
     */
    fun updateSearchedSongList(list: ArrayList<AudioFileModel>){
        Log.d(TAG,"updateSearchedSongList data set changed");
        _searchedSongList.value = list;
    }


    /**
     * scans the device for valid audio files
     */
    fun startScan(activity: Activity){
        var audioFileScanner= AudioFileScanner();

        audioFileScanner.getAllAudioFromDevice(activity, object: AudioFileScanner.Results{
            override fun onError() {
                Log.e(TAG,"error retrieving files")
            }

            override fun onResults(list: java.util.ArrayList<AudioFileModel>) {
                //Log.i(TAG,"results..")
                for (item in list){
                    //Log.d(TAG, item.toString());
                }
                updateScannedSongList(list);
            }

        } );
    }

    /**
     * Generates albums based on available songs.
     */
    private fun generateAlbums() {
        var songs = _scannedSongList.value;

        if (songs == null) {
            Log.w(TAG, "Song list uninitialized.")
            return
        }

        Log.i(TAG, "Preparing albums, song list size of " + songs.size)

        //will be used to group songs based on album name
        val albumMap = HashMap<String, java.util.ArrayList<AudioFileModel>>()

        var albums = ArrayList<AlbumModel>() //to keep track of generated albums

        var albumName: String

        for (i in songs.indices) {
            albumName = songs[i].album
            if (albumMap[albumName] == null) {
                albumMap[albumName] = java.util.ArrayList()
                albumMap[albumName]!!.add(songs[i])
            } else
                albumMap[albumName]!!.add(songs[i])
        }

        for ((key, value) in albumMap) {
            Log.d(TAG, "key: " + key + " value: " + value.size + " first value: " + value[0].displayName)
            var album = AlbumModel(key,value);
            albums.add(album)
        }

        this._albumList.value = albums;

    }

    private fun fetchCollectionList(){

        var fetchedCollection = ArrayList<PlaylistModel>()
        //playlistService fetchplaylists

        this._collectionList.value = fetchedCollection;
    }

}
