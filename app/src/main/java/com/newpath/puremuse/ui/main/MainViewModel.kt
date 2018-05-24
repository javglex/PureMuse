package com.newpath.puremuse.ui.main

import android.app.Activity
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.util.Log
import com.newpath.puremuse.models.AudioFileModel
import com.newpath.puremuse.helpers.AudioFileScanner

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
        initSearchedSongList();

    }

    private fun initSearchedSongList(){
        _searchedSongList.value = scannedSongList.value;
    }

    fun updateSearchedSongList(list: ArrayList<AudioFileModel>){
        Log.d(TAG,"updateScannedSongList data set changed");
        _searchedSongList.value = list;
    }


    fun startScan(activity: Activity){
        var audioFileScanner= AudioFileScanner();

        audioFileScanner.getAllAudioFromDevice(activity, object: AudioFileScanner.Results{
            override fun onError() {
                Log.e(TAG,"error retrieving files")
            }

            override fun onResults(list: java.util.ArrayList<AudioFileModel>) {
                Log.i(TAG,"results..")
                for (item in list){
                    Log.d(TAG, item.toString());
                }
                updateScannedSongList(list);
            }

        } );
    }

}
