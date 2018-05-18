package com.newpath.puremuse.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.newpath.puremuse.MainActivity;
import com.newpath.puremuse.R;
import com.newpath.puremuse.utils.Constants;

import static com.newpath.puremuse.utils.Constants.ACTION.END_ACTION;
import static com.newpath.puremuse.utils.Constants.ACTION.MAIN_ACTION;
import static com.newpath.puremuse.utils.Constants.ACTION.NEXT_MUSIC_ACTION;
import static com.newpath.puremuse.utils.Constants.ACTION.PLAY_MUSIC_ACTION;
import static com.newpath.puremuse.utils.Constants.ACTION.PREV_MUSIC_ACTION;
import static com.newpath.puremuse.utils.Constants.ACTION.START_ACTION;
import static com.newpath.puremuse.utils.Constants.ACTION.STOP_MUSIC_ACTION;
import static com.newpath.puremuse.utils.Constants.CHANNEL_ID;

public class MusicPlayService extends Service {


    public static final String TAG = "MusicPlayService";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction()==null)
            return Service.START_NOT_STICKY;

        switch(intent.getAction()){
            case START_ACTION:
                Log.i(TAG,"start action");
                startAction(intent);
                break;
            case END_ACTION:
                Log.i(TAG,"end action");
                stopForeground(true);
                stopSelf();
                break;
            case PLAY_MUSIC_ACTION:
                Log.i(TAG,"play music action");

                break;
            case STOP_MUSIC_ACTION:
                Log.i(TAG,"stop music action");

                break;
            case PREV_MUSIC_ACTION:
                Log.i(TAG,"prev music action");

                break;
            case NEXT_MUSIC_ACTION:
                Log.i(TAG,"next music action");

                break;

            default:
                break;
        }



        return Service.START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
    }

    private void startAction(Intent intent){

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setAction(MAIN_ACTION);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);

        Intent previousIntent = new Intent(this, MusicPlayService.class);
        previousIntent.setAction(PREV_MUSIC_ACTION);
        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
                previousIntent, 0);

        Intent playIntent = new Intent(this, MusicPlayService.class);
        playIntent.setAction(PLAY_MUSIC_ACTION);
        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
                playIntent, 0);

        Intent nextIntent = new Intent(this, MusicPlayService.class);
        nextIntent.setAction(NEXT_MUSIC_ACTION);
        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
                nextIntent, 0);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.mipmap.ic_sound_disc);

        //start notification

        Notification.Builder notification = new Notification.Builder(this)
                .setContentTitle("PureMuse Player")
                .setTicker("PureMuse Music Player")
                .setContentText("My Music")
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setLargeIcon(
                        Bitmap.createScaledBitmap(icon, 128, 128, false)
                )
                .setContentIntent(pendingIntent)
                .setOngoing(true)
                .addAction(android.R.drawable.ic_media_previous,
                        "Previous", ppreviousIntent)
                .addAction(android.R.drawable.ic_media_play, "Play",
                        pplayIntent)
                .addAction(android.R.drawable.ic_media_next, "Next",
                        pnextIntent)
                .setStyle(new Notification.MediaStyle()
                        .setMediaSession(null));

            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                CharSequence name = getString(R.string.channel_name);
                String description = getString(R.string.channel_description);
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
                channel.setDescription(description);
                // Register the channel with the system; you can't change the importance
                // or other notification behaviors after this
                NotificationManager notificationManager = getSystemService(NotificationManager.class);
                notificationManager.createNotificationChannel(channel);
                notification.setChannelId(CHANNEL_ID);
            }

        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
                notification.build());


    }



}
