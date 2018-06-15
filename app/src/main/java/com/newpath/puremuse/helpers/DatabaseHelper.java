package com.newpath.puremuse.helpers;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.newpath.puremuse.database.AppDatabase;
import com.newpath.puremuse.models.PlaylistModel;

import java.util.List;

public class DatabaseHelper {

    private static final String TAG = DatabaseHelper.class.getName();

    public static void populateAsync(@NonNull final AppDatabase db) {
        PopulateDbAsync task = new PopulateDbAsync(db);
        task.execute();
    }

    public static void populateSync(@NonNull final AppDatabase db) {
        populateWithTestData(db);
    }

    private static PlaylistModel addPlaylist(final AppDatabase db, PlaylistModel playlist) {
        db.playlistDao().insertAll(playlist);
        return playlist;
    }

    private static void populateWithTestData(AppDatabase db) {
        PlaylistModel playlist = new PlaylistModel();

        for (int i=0; i<100; i++) {
            playlist.setCollectionName("Ajay" +i);
            addPlaylist(db, playlist);
        }

        List<PlaylistModel> playlistList = db.playlistDao().getAll();
        Log.d(DatabaseHelper.TAG, "Rows Count: " + playlistList.size());
    }

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final AppDatabase mDb;

        PopulateDbAsync(AppDatabase db) {
            mDb = db;
        }

        @Override
        protected Void doInBackground(final Void... params) {
            populateWithTestData(mDb);
            return null;
        }

    }
}