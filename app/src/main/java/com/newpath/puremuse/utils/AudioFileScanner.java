package com.newpath.puremuse.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.newpath.puremuse.models.AudioFileModel;

import java.util.ArrayList;

public class AudioFileScanner {

    private static final String TAG = "AudioFileScanner";
    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    //String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
    final ArrayList<AudioFileModel> tempAudioList = new ArrayList<>();
    Runnable scannerRunnable;

    String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION
    };

    String selection = MediaStore.Audio.Media.DATA +" like ? ";

    public void getAllAudioFromDevice(Context context, final Results results) {

        Log.i(TAG,"getting all audio from device..");
        Cursor c = context.getContentResolver().query(uri, projection, selection, new String[]{"%.%"}, null);

        parseCursorInBackground(c, new Callback() {
            @Override
            public void onSuccess() {
                results.onResults(tempAudioList);
            }

            @Override
            public void onError() {
                results.onError();
            }
        });


    }

    private void parseCursorInBackground(final Cursor c, final Callback cb){
        scannerRunnable = null;
        scannerRunnable = new Runnable() {
            @Override
            public void run() {
                if (c != null) {
                    while (c.moveToNext()) {

                        AudioFileModel audioModel = new AudioFileModel();
                        String album = c.getString(3);
                        String artist = c.getString(1);
                        String name = c.getString(2);
                        String path = c.getString(4);

                        audioModel.setDisplayName(name);
                        audioModel.setAlbum(album);
                        audioModel.setArtist(artist);
                        audioModel.setPath(path);

                        Log.i("Name :" + name, " Album :" + album);
                        Log.i("Path :" + path, " Artist :" + artist);

                        tempAudioList.add(audioModel);
                    }
                    c.close();
                    cb.onSuccess();
                } else cb.onError();
            }
        };

        scannerRunnable.run();

    }

    public interface Callback{
        public void onSuccess();
        public void onError();
    }

    public interface Results{
        public void onResults(ArrayList<AudioFileModel> list);
        public void onError();
    }

}
