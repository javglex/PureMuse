package com.newpath.puremuse.services;


import android.util.Log;

import com.newpath.puremuse.models.AudioFileModel;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Uses hashing methods to store a list of on-device songs into a hashmap in memory.
 * Database song list will be compared to the on-device list in order to see which songs are actually available in device.
 */
public class SongsOnDeviceService {

    private static final String TAG = "SongsOnDeviceService";

    private HashMap<Integer, AudioFileModel> deviceSongMap;
    private static SongsOnDeviceService mSongsOnDeviceService;

    private SongsOnDeviceService(){
        deviceSongMap = new HashMap<>();
    }

    public static SongsOnDeviceService getService(){
        if (mSongsOnDeviceService==null)
            mSongsOnDeviceService = new SongsOnDeviceService();
        return mSongsOnDeviceService;
    }

    public void hashSongsList(ArrayList<AudioFileModel> songlist){

        AudioFileModel song = null;
        int songSize = songlist.size();
        Log.d(TAG,"song list size: " + songSize);

        for (int i = 0; i<songSize; i++){

            /**
             * The reason we are using title and album is because some songs may have the same name
             * across different albums (for example remixes) by using the title and the album in
             * conjunction we guarantee no clashing of hash codes
             */
            song = songlist.get(i);
            String songString = song.toHashableString();
            Log.d(TAG,"title + album: " + songString);
            int hashCode = songString.hashCode();
            Log.d(TAG, "hashcode: " + hashCode);

            deviceSongMap.put(hashCode, song);

        }

        Log.d(TAG,"map size (checking for any collisions): " + deviceSongMap.keySet().size() );

    }



}
