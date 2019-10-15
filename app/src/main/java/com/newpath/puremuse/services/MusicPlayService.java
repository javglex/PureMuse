package com.newpath.puremuse.services;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.ResultReceiver;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.support.v4.media.MediaBrowserCompat;
import androidx.media.MediaBrowserServiceCompat;
import android.support.v4.media.MediaMetadataCompat;

import androidx.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.util.Log;

import com.newpath.puremuse.R;
import com.newpath.puremuse.utils.Constants;
import com.newpath.puremuse.helpers.MediaStyleHelper;

import java.io.IOException;
import java.util.List;


/**
 * Reference: https://code.tutsplus.com/tutorials/background-audio-in-android-with-mediasessioncompat--cms-27030
 * Very helpful with setting up media session and media browser.
 */

public class MusicPlayService extends MediaBrowserServiceCompat implements AudioManager.OnAudioFocusChangeListener {


    public static final String TAG = "MusicPlayService";
    private MediaPlayer mMediaPlayer;
    private MediaSessionCompat mMediaSessionCompat;
    private BroadcastReceiver mNoisyReceiver;
    private Uri mCurrentPlayingURI;         //URI of song currently playing
    private MediaSessionCompat.Callback mMediaSessionCallback;
    private boolean playOnPrepare = false;      //determines whether to play song on prepare or not

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent.getAction()!=null)
            Log.i(TAG,"onstartCommand: " + intent.getAction() + " flags: " + flags  + " startid: " + startId);
        MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
        return super.onStartCommand(intent, flags, startId);
//        if (intent.getAction()==null)
//            return Service.START_NOT_STICKY;
//
//        mContext = getApplicationContext();
//
//
//        MediaButtonReceiver.handleIntent(mMediaSessionCompat, intent);
//        switch(intent.getAction()){
//            case START_ACTION:
//                Log.i(TAG,"start action");
//                startAction(intent);
//                break;
//            case END_ACTION:
//                Log.i(TAG,"end action");
//                stopForeground(true);
//                stopSelf();
//                break;
//            case PLAY_MUSIC_ACTION:
//                Log.i(TAG,"togglePlay music action");
//
//                break;
//            case STOP_MUSIC_ACTION:
//                Log.i(TAG,"stop music action");
//
//                break;
//            case PREV_MUSIC_ACTION:
//                Log.i(TAG,"prev music action");
//
//                break;
//            case NEXT_MUSIC_ACTION:
//                Log.i(TAG,"next music action");
//
//                break;
//
//            default:
//                break;
//        }
//
//
//
//        return Service.START_NOT_STICKY;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        Log.i(TAG,"onCreate");

        initVariables();
        initMediaPlayer();
        initMediaSession();
        initNoisyReceiver();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        switch( focusChange ) {
            case AudioManager.AUDIOFOCUS_LOSS: {
                if( mMediaPlayer.isPlaying() ) {
                    mMediaPlayer.stop();
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: {
                mMediaPlayer.pause();
                break;
            }
            case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: {
                if( mMediaPlayer != null ) {
                    mMediaPlayer.setVolume(0.3f, 0.3f);
                }
                break;
            }
            case AudioManager.AUDIOFOCUS_GAIN: {
                if( mMediaPlayer != null ) {
                    if( !mMediaPlayer.isPlaying() ) {
                        mMediaPlayer.start();
                    }
                    mMediaPlayer.setVolume(1.0f, 1.0f);
                }
                break;
            }
        }
    }

    @Nullable
    @Override
    public BrowserRoot onGetRoot(@NonNull String clientPackageName, int clientUid, @Nullable Bundle rootHints) {
        if(TextUtils.equals(clientPackageName, getPackageName())) {
            return new BrowserRoot(getString(R.string.app_name), null);
        }

        return null;
    }

    @Override
    public void onLoadChildren(@NonNull String parentId, @NonNull Result<List<MediaBrowserCompat.MediaItem>> result) {
        result.sendResult(null);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy");
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.abandonAudioFocus(this);
        unregisterReceiver(mNoisyReceiver);
        mMediaSessionCompat.release();
        NotificationManagerCompat.from(this).cancel(1);
    }

//    private void startAction(Intent intent){
//
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        notificationIntent.setAction(MAIN_ACTION);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
//                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
//                notificationIntent, 0);
//
//        Intent previousIntent = new Intent(this, MusicPlayService.class);
//        previousIntent.setAction(PREV_MUSIC_ACTION);
//        PendingIntent ppreviousIntent = PendingIntent.getService(this, 0,
//                previousIntent, 0);
//
//        Intent playIntent = new Intent(this, MusicPlayService.class);
//        playIntent.setAction(PLAY_MUSIC_ACTION);
//        PendingIntent pplayIntent = PendingIntent.getService(this, 0,
//                playIntent, 0);
//
//        Intent nextIntent = new Intent(this, MusicPlayService.class);
//        nextIntent.setAction(NEXT_MUSIC_ACTION);
//        PendingIntent pnextIntent = PendingIntent.getService(this, 0,
//                nextIntent, 0);
//
//        Bitmap icon = BitmapFactory.decodeResource(getResources(),
//                R.mipmap.ic_sound_disc);
//
//        //start notification
//
//        Notification.Builder notification = new Notification.Builder(this)
//                .setContentTitle("PureMuse Player")
//                .setTicker("PureMuse Music Player")
//                .setContentText("My Music")
//                .setPriority(Notification.PRIORITY_HIGH)
//                .setSmallIcon(R.drawable.ic_launcher_background)
//                .setLargeIcon(
//                        Bitmap.createScaledBitmap(icon, 128, 128, false)
//                )
//                .setContentIntent(pendingIntent)
//                .setOngoing(true)
//                .addAction(android.R.drawable.ic_media_previous,
//                        "Previous", ppreviousIntent)
//                .addAction(android.R.drawable.ic_media_play, "Play",
//                        pplayIntent)
//                .addAction(android.R.drawable.ic_media_next, "Next",
//                        pnextIntent)
//                .setStyle(new Notification.MediaStyle()
//                        .setMediaSession(null));
//
//            // Create the NotificationChannel, but only on API 26+ because
//            // the NotificationChannel class is new and not in the support library
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                CharSequence name = getString(R.string.channel_name);
//                String description = getString(R.string.channel_description);
//                int importance = NotificationManager.IMPORTANCE_HIGH;
//                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
//                channel.setDescription(description);
//                // Register the channel with the system; you can't change the importance
//                // or other notification behaviors after this
//                NotificationManager notificationManager = getSystemService(NotificationManager.class);
//                notificationManager.createNotificationChannel(channel);
//                notification.setChannelId(CHANNEL_ID);
//            }
//
//        startForeground(Constants.NOTIFICATION_ID.FOREGROUND_SERVICE,
//                notification.build());
//
//
//    }


    public void initVariables(){

        mNoisyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if( mMediaPlayer != null && mMediaPlayer.isPlaying() ) {
                    mMediaPlayer.pause();
                }
            }
        };

        Log.d(TAG,"initated noisy receiver");

        mMediaSessionCallback = new MediaSessionCompat.Callback() {

            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {
                Log.d(TAG, "onMediaButtonEvent called: " + mediaButtonIntent);
                return super.onMediaButtonEvent(mediaButtonIntent);
            }

            @Override
            public void onPlay() {
                super.onPlay();
                Log.d(TAG,"onPlay()");
                if( !successfullyRetrievedAudioFocus() ) {
                    return;
                }
                Log.d(TAG,"successfullyRetrievedAudioFocus()");
                mMediaSessionCompat.setActive(true);
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
                showPlayingNotification();
                initNoisyReceiver();
                mMediaPlayer.start();
            }

            @Override
            public void onPause() {
                super.onPause();
                Log.d(TAG,"onPause()");

                if( mMediaPlayer.isPlaying() ) {
                    mMediaPlayer.pause();
                    setMediaPlaybackState(PlaybackStateCompat.STATE_PAUSED);
                    showPausedNotification();
                }
            }

            @Override
            public void onStop(){
                super.onStop();
                Log.d(TAG,"onStop()");
                setMediaPlaybackState(PlaybackStateCompat.STATE_STOPPED);
                mMediaSessionCompat.setActive(false);
                mMediaPlayer.stop();
                mMediaPlayer.reset();
                mMediaPlayer.release();
                initMediaPlayer();
            }

            @Override
            public void onSkipToNext(){
                Log.d(TAG,"onSkipToNext");
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT);

            }

            @Override
            public void onSkipToPrevious(){
                Log.d(TAG,"onSkipToPrevious");
                setMediaPlaybackState(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS);

            }

            @Override
            public void onCommand(String command, Bundle extras, ResultReceiver cb) {
                Log.d(TAG,"onCommand: "+command);

                super.onCommand(command, extras, cb);
                Log.d(TAG,"onCommand: "+command);
//                if( COMMAND_EXAMPLE.equalsIgnoreCase(command) ) {
//                    //Custom command here
//                }
            }

            @Override
            public void onSeekTo(long pos) {
                super.onSeekTo(pos);
                //mMediaPlayer.seekTo((int)pos); for now disable then figure out how it works
            }

            @Override
            public void onPlayFromUri(Uri uri, Bundle extras) {
                super.onPlayFromUri(uri, extras);
                Log.d(TAG,"onPlayFromUri(): " + uri);

                if (extras==null || uri==null)
                    return;


                mCurrentPlayingURI = uri;

                mMediaPlayer.stop();
                mMediaPlayer.reset();


                try {
                    mMediaPlayer.setDataSource(getApplicationContext(),uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"failed to set data source. " + e.getLocalizedMessage());
                    return;
                }

                Log.d(TAG,"setDataSource()");
                initMediaSessionMetadata(extras);


                playOnPrepare = true;
                mMediaPlayer.prepareAsync();
                //Work with extras here if you want

            }

            @Override
            public void onPrepareFromUri(Uri uri, Bundle extras){
                Log.d(TAG,"onPrepareFromUri(): " + uri);

                if (extras==null || uri==null)
                    return;


                mCurrentPlayingURI = uri;

                mMediaPlayer.stop();
                mMediaPlayer.reset();


                try {
                    mMediaPlayer.setDataSource(getApplicationContext(),uri);
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e(TAG,"failed to set data source. " + e.getLocalizedMessage());
                    return;
                }

                Log.d(TAG,"setDataSource()");
                initMediaSessionMetadata(extras);
                playOnPrepare = false;
                mMediaPlayer.prepareAsync();
            }


        };
        Log.d(TAG,"initiated mMediaSessionCallback");

    }



    private void initMediaPlayer() {

        mMediaPlayer = new MediaPlayer();
        Log.d(TAG,"created new MediaPlayer");
        mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mMediaPlayer.setVolume(1.0f, 1.0f);
        mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // Do something. For example: playButton.setEnabled(true);
                if( !successfullyRetrievedAudioFocus() ) {
                    return;
                }
                Log.d(TAG,"successfullyRetrievedAudioFocus()");

                if (!playOnPrepare)
                    return;
                mMediaSessionCompat.setActive(true);
                setMediaPlaybackState(PlaybackStateCompat.STATE_PLAYING);
                showPlayingNotification();
                mMediaPlayer.start();
            }
        });
    }

    private void initMediaSession() {
        Log.d(TAG,"initMediaSession");

        ComponentName mediaButtonReceiver = new ComponentName(getApplicationContext(), MediaButtonReceiver.class);
        mMediaSessionCompat = new MediaSessionCompat(getApplicationContext(), TAG, mediaButtonReceiver, null);

        mMediaSessionCompat.setCallback(mMediaSessionCallback);
        mMediaSessionCompat.setFlags( MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS | MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS );

        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        mediaButtonIntent.setClass(this, MediaButtonReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, mediaButtonIntent, 0);
        mMediaSessionCompat.setMediaButtonReceiver(pendingIntent);

        setSessionToken(mMediaSessionCompat.getSessionToken());
        Log.d(TAG,"setSessionToken");

    }

    private void initNoisyReceiver() {
        //Handles headphones coming unplugged. cannot be done through a manifest receiver
        IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        registerReceiver(mNoisyReceiver, filter);
    }


    private boolean successfullyRetrievedAudioFocus() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int result = audioManager.requestAudioFocus(this,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

        return result == AudioManager.AUDIOFOCUS_GAIN;
    }

    private void setMediaPlaybackState(int state) {

        Log.d(TAG, "setMediaPlaybackState(): " + state);
        PlaybackStateCompat.Builder playbackstateBuilder = new PlaybackStateCompat.Builder();
        //previously: if state == PlayBackStateCompat.Play => setAction ( actions available when a song is playing i.e pause, forward, stop; but not play )
        playbackstateBuilder.setActions( PlaybackStateCompat.ACTION_PAUSE
                | PlaybackStateCompat.ACTION_PLAY
                | PlaybackStateCompat.ACTION_PLAY_PAUSE
                | PlaybackStateCompat.ACTION_STOP
                | PlaybackStateCompat.ACTION_SKIP_TO_NEXT
                | PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS
        );

        playbackstateBuilder.setState(state, PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN, 0);
        mMediaSessionCompat.setPlaybackState(playbackstateBuilder.build());
    }

    private void initMediaSessionMetadata(Bundle extras) {
        String albumName = extras.getString(Constants.MediaBundle.ALBUM_NAME,"");
        String songTitle = extras.getString(Constants.MediaBundle.SONG_TITLE,"");


        Log.d(TAG,"name: "+albumName);

        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        //Notification icon in card
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ALBUM_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));

        //lock screen icon for pre lollipop
        metadataBuilder.putBitmap(MediaMetadataCompat.METADATA_KEY_ART, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, songTitle);
        metadataBuilder.putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, albumName);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, 1);
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS, 1);

        mMediaSessionCompat.setMetadata(metadataBuilder.build());
    }

    private void showPausedNotification() {
        NotificationCompat.Builder builder = MediaStyleHelper.from(this, mMediaSessionCompat);
        if( builder == null ) {
            return;
        }
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_previous, "Prev", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_play, "Play", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_next, "Next", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));

        builder.setStyle(new  androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationManagerCompat.from(this).notify(1, builder.build());
    }

    private void showPlayingNotification() {

        NotificationCompat.Builder builder=null;

        try{
            builder = MediaStyleHelper.from(MusicPlayService.this, mMediaSessionCompat);
        }catch(Exception e){
            e.printStackTrace();
        }

        if( builder == null ) {
            return;
        }


        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_previous, "Prev", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS)));
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_pause, "Pause", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_PLAY_PAUSE)));
        builder.addAction(new NotificationCompat.Action(android.R.drawable.ic_media_next, "Next", MediaButtonReceiver.buildMediaButtonPendingIntent(this, PlaybackStateCompat.ACTION_SKIP_TO_NEXT)));

        builder.setStyle(new  androidx.media.app.NotificationCompat.MediaStyle().setShowActionsInCompactView(1).setMediaSession(mMediaSessionCompat.getSessionToken()));
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationManagerCompat.from(MusicPlayService.this).notify(1, builder.build());
    }

}
