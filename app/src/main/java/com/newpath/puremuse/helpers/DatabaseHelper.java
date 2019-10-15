package com.newpath.puremuse.helpers;

import android.os.AsyncTask;
import androidx.annotation.NonNull;
import android.util.Log;

import com.newpath.puremuse.database.AppDatabase;
import com.newpath.puremuse.models.PlaylistModel;

import java.util.ArrayList;

public class DatabaseHelper {

    private static final String TAG = DatabaseHelper.class.getName();

    public static void populateAsync(final ArrayList<PlaylistModel> collections, @NonNull final AppDatabase db, DbCallback<Void> cb) {
        PopulateDbAsync task = new PopulateDbAsync(collections, db,cb);
        task.execute();
    }

    public static void retrieveAsync(@NonNull final AppDatabase db, DbCallback<ArrayList<PlaylistModel>> cb){
        RetrieveDbAsyn task = new RetrieveDbAsyn(db,cb);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static PlaylistModel addPlaylist(final AppDatabase db, PlaylistModel playlist) {

        try {
            db.playlistDao().insertPlaylist(playlist);
        }catch(Exception e){
            e.printStackTrace();
        }

        return playlist;
    }

    private static void populateWithTestData(AppDatabase db) {
        PlaylistModel playlist = new PlaylistModel();

        for (int i=0; i<200; i++) {
            playlist.setCollectionName("Ajay" +i);
            addPlaylist(db, playlist);
        }


    }

    private static void populateWithCollections(ArrayList<PlaylistModel> collections, AppDatabase db){


        if (collections==null) {
            Log.e(TAG, "Collection is NULL");
            return;
        }

        int size = collections.size();

        try{
            db.playlistDao().nukeTable();
        }catch (Exception e){
            e.printStackTrace();
        }

        for (int i=0; i<size; i++) {

            addPlaylist(db, collections.get(i));
        }
    }

    private static ArrayList<PlaylistModel> retrieveDbData(AppDatabase db){
        return (ArrayList<PlaylistModel>) db.playlistDao().getAll();
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;
        private DbCallback<Void> mCb;
        private ArrayList<PlaylistModel> mCollections;

        PopulateDbAsync(ArrayList<PlaylistModel> collections, AppDatabase db, DbCallback<Void> cb) {
            mDb = db;
            mCb = cb;
            mCollections = collections;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            Log.d(TAG, "doing in background");
            populateWithCollections(mCollections,mDb);
            return null;
        }

        @Override
        protected void onPostExecute(final Void result){
            mCb.onFinished(null);
        }

    }

    private static class RetrieveDbAsyn extends AsyncTask<Void,Void,Void>{
        private final AppDatabase mDb;
        private ArrayList<PlaylistModel> mPlaylists;
        private DbCallback<ArrayList<PlaylistModel>> mCb;

        RetrieveDbAsyn(AppDatabase db, DbCallback<ArrayList<PlaylistModel>> cb) {
            mDb = db;
            mCb = cb;
            mPlaylists = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doing retrieve in background");
            mPlaylists = retrieveDbData(mDb);
            return null;
        }

        @Override
        protected void onPostExecute(final Void result){
            mCb.onFinished(mPlaylists);
        }
    }



    public interface DbCallback<T>{
        public void onFinished(T result);
        public void onError();
    }
}