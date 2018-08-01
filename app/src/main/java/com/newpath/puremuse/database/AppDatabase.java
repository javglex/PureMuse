package com.newpath.puremuse.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.newpath.puremuse.dao.PlaylistDao;
import com.newpath.puremuse.models.PlaylistModel;
import com.newpath.puremuse.utils.Converters;

@Database(entities = {PlaylistModel.class}, version = 2)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public abstract PlaylistDao playlistDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) INSTANCE =
                Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "playlist-database")
                        // allow queries on the main thread.
                        // Don't do this on a real app! See PersistenceBasicSample for an example.
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build();
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }
}