package com.newpath.puremuse.helpers;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.newpath.puremuse.models.AudioFileModel;

import java.util.ArrayList;
import java.util.HashMap;

public class AudioFileScanner {

    private static final String TAG = "AudioFileScanner";
    Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    final ArrayList<AudioFileModel> tempAudioList = new ArrayList<>();
    final static HashMap<Integer,String> albumImageSet = new HashMap<Integer,String>();
    Context mContext;
    Runnable scannerRunnable;

    String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ALBUM_ID
    };

    String selection = MediaStore.Audio.Media.DATA +" like ? ";

    public void getAllAudioFromDevice(Context context, final Results results) {

        mContext = context;
        Log.i(TAG,"getting all audio from device..");
        Cursor c = context.getContentResolver().query(uri, projection, selection, new String[]{"%.%"}, null);

        parseCursorInBackground(c, new Callback() {
            @Override
            public void onSuccess() {
                results.onResults(tempAudioList);
                getAlbumImagesFromDevice();
            }

            @Override
            public void onError() {
                results.onError();
            }
        });


    }

    private void getAlbumImagesFromDevice(){

        if (mContext==null)
            return;

        for (Integer key : albumImageSet.keySet()) {
            Cursor cursor = mContext.getContentResolver().query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    new String[] {MediaStore.Audio.Albums._ID, MediaStore.Audio.Albums.ALBUM_ART},
                    MediaStore.Audio.Albums._ID+ "=?",
                    new String[] {String.valueOf(key)},
                    null);

            if (cursor.moveToFirst()) {
                String path = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Albums.ALBUM_ART));
                // do whatever you need to do
                albumImageSet.put(key,path);
            }
        }

    }

    public static String getAlbumImage(int albumId) throws Exception{

        if (albumImageSet!=null && albumImageSet.keySet().size()>0 && albumImageSet.entrySet().size()>0){
            if (albumImageSet.containsKey(albumId)){
                return albumImageSet.get(albumId);
            }
        } else {
            Log.e(TAG, "album image hashmap is empty");
            throw new Exception("Album Image hashmap is empty");
        }

        return null;
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
                        int albumId = c.getInt(7);

                        audioModel.setDisplayName(name);
                        audioModel.setAlbum(album);
                        audioModel.setArtist(artist);
                        audioModel.setPath(path);
                        audioModel.setAlbumId(albumId);

//                        Log.i("Name :" + name, " Album :" + album);
//                        Log.i("Path :" + path, " Artist :" + artist);

                        tempAudioList.add(audioModel);
                        albumImageSet.put(albumId,"");
                    }

                    c.close();
                    cb.onSuccess();

                } else
                    cb.onError();
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
