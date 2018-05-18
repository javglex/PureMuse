package com.newpath.puremuse.ui.main

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import com.newpath.puremuse.models.AudioFileModel

class MainViewModel : ViewModel() {

    private val TAG:String = "MainViewModel";

    private val _data = MutableLiveData<String>()
    val data: LiveData<String>
        get() = _data

    private val _scannedSongList = MutableLiveData<ArrayList<AudioFileModel>>()
    val scannedSongList: LiveData<ArrayList<AudioFileModel>>
        get() = _scannedSongList

    private val _searchedSongList = MutableLiveData<ArrayList<AudioFileModel>>()
    val searchedSongList: LiveData<ArrayList<AudioFileModel>>
        get() = _searchedSongList

    init {
        _scannedSongList.value = ArrayList<AudioFileModel>()
        _searchedSongList.value = ArrayList<AudioFileModel>()
    }

    fun updateScannedSongList(list: ArrayList<AudioFileModel>){
        Log.d(TAG,"updateScannedSongList data set changed");
        _scannedSongList.value = list;
    }

    fun updateSearchedSongList(list: ArrayList<AudioFileModel>){
        Log.d(TAG,"updateScannedSongList data set changed");
        _searchedSongList.value = list;
    }

}
