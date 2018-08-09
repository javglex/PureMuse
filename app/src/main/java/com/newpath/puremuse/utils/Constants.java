package com.newpath.puremuse.utils;

public class Constants {

    public interface ACTION {
        String MAIN_ACTION = "com.truiton.foregroundservice.action.main";
        String START_ACTION="com.newpath.puremuse.services.START";
        String END_ACTION="com.newpath.puremuse.services.END";
        String NEXT_MUSIC_ACTION="com.newpath.puremuse.services.NEXT";
        String PREV_MUSIC_ACTION="com.newpath.puremuse.services.PREV";
        String PLAY_MUSIC_ACTION="com.newpath.puremuse.services.PLAY";
        String STOP_MUSIC_ACTION="com.newpath.puremuse.services.STOP";
        String PAUSE_MUSIC_ACTION="com.newpath.puremuse.services.PAUSE";
    }

    public interface MediaBundle{
        String ALBUM_NAME = "com.truiton.foregroundservice.action.main.ALBUM_NAME";
        String SONG_TITLE = "com.truiton.foregroundservice.action.main.SONG_TITLE";
        String SONG_LIST = "com.truiton.foregroundservice.action.main.SONG_LIST";
        String START_SONG_POS = "com.truiton.foregroundservice.action.main.START_SONG_POS";
    }

    public interface STATE {
        String STATE_PAUSED = "com.newpath.puremuse.services.PAUSED";
        String STATE_PLAYING = "com.newpath.puremuse.services.PLAYING";
        String STATE_STOPPED = "com.newpath.puremuse.services.STOPPED";
        String STATE_SKIPPING_TO_PREVIOUS = "com.newpath.puremuse.services.STATE_SKIPPING_TO_PREVIOUS";
        String STATE_SKIPPING_TO_NEXT = "com.newpath.puremuse.services.STATE_SKIPPING_TO_NEXT";
    }

    public interface SONGPROPERTIES{
        String PATH = "com.newpath.puremuse.PATH";
        String DISPLAY_NAME = "com.newpath.puremuse.DISPLAY_NAME";
        String ALBUM_TITLE = "com.newpath.puremuse.ALBUM_TITLE";
        String _ID = "com.newpath.puremuse._ID";
        String ARTIST = "com.newpath.puremuse.ARTIST";
        String DURATION = "com.newpath.puremuse.DURATION";
        String SONG_TITLE = "com.newpath.puremuse.SONG_TITLE";
        String DATA = "com.newpath.puremuse.DATA";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 333;
    }

    public interface COLLECTION_TYPE{
        int ALBUM = 0;
        int PLAYLIST = 1;
    }

    public final static String COLLECTIONPOSKEY = "COLLECTIONPOSKEY";          //position of collection to be opened, whether it is an album or a playlist
    public final static String SONGPOSTKEY = "SONGPOSTKEY";          //position of song inside collection to be played
    public final static String COLLECTIONTYPE = "COLLECTIONTYPE";      //is it an album or a playlist? (0 or 1?)

    public static final String CHANNEL_ID = "com.newpath.puremuse.channel";

}
