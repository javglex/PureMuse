package com.newpath.puremuse.utils;

public class Constants {

    public interface ACTION {
        public static final String MAIN_ACTION = "com.truiton.foregroundservice.action.main";
        public static final String START_ACTION="com.newpath.puremuse.services.START";
        public static final String END_ACTION="com.newpath.puremuse.services.END";
        public static final String NEXT_MUSIC_ACTION="com.newpath.puremuse.services.NEXT";
        public static final String PREV_MUSIC_ACTION="com.newpath.puremuse.services.PREV";
        public static final String PLAY_MUSIC_ACTION="com.newpath.puremuse.services.PLAY";
        public static final String STOP_MUSIC_ACTION="com.newpath.puremuse.services.STOP";
    }

    public interface NOTIFICATION_ID {
        public static int FOREGROUND_SERVICE = 333;
    }

    public static final String CHANNEL_ID = "com.newpath.puremuse.channel";

}
