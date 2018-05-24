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
    }

    public interface STATE {
        String STATE_PAUSED = "com.newpath.puremuse.services.PAUSED";
        String STATE_PLAYING = "com.newpath.puremuse.services.PLAYING";
    }

    public interface NOTIFICATION_ID {
        int FOREGROUND_SERVICE = 333;
    }

    public static final String CHANNEL_ID = "com.newpath.puremuse.channel";

}
