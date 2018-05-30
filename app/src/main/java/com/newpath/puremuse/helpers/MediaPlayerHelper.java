package com.newpath.puremuse.helpers;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Button;

import com.newpath.puremuse.NavigationPageActivity;
import com.newpath.puremuse.models.AudioFileModel;
import com.newpath.puremuse.services.MusicPlayService;
import com.newpath.puremuse.utils.Constants;

public class MediaPlayerHelper implements LifecycleObserver {

    private static final String TAG = "MediaPlayerHelper";
    private static MediaPlayerHelper sMediaPlayerHelper;
    private MediaControllerCompat.Callback mMediaControllerCompatCallback;
    private MediaBrowserCompat.ConnectionCallback mMediaBrowserCompatConnectionCallback;
    private MediaBrowserCompat mMediaBrowserCompat = null;
    private MediaControllerCompat mMediaControllerCompat = null;
    private Button mPlayPauseToggleButton = null;
    private String mCurrentState = Constants.STATE.STATE_PAUSED;
    private Activity mActivity;


    public static MediaPlayerHelper getMediaPlayerInstance(final Activity activity){
        if (sMediaPlayerHelper==null)
            sMediaPlayerHelper = new MediaPlayerHelper(activity);

        return sMediaPlayerHelper;
    }

    private MediaPlayerHelper(final Activity activity){

        mActivity = activity;

        initConnectionCallback();
        initMediaController();
        setupMediaToggleButton();


    }

    public void togglePlay(){
        try {
            if (mCurrentState == Constants.STATE.STATE_PAUSED) {
                MediaControllerCompat.getMediaController(mActivity).getTransportControls().play();
                mCurrentState = Constants.STATE.STATE_PLAYING;

            }
            else if (mCurrentState == Constants.STATE.STATE_PLAYING) {
                MediaControllerCompat.getMediaController(mActivity).getTransportControls().pause();
                mCurrentState = Constants.STATE.STATE_PAUSED;

            }

        }catch(Exception e){
            Log.e(TAG,"togglePlay() " + e);
        }

    }

    public MediaPlayerHelper setSong(AudioFileModel audioFile){
        Uri pathUri = Uri.parse(audioFile.getPath());
        Bundle mediaBundle = new Bundle();
        mediaBundle.putString(Constants.MediaBundle.ALBUM_NAME,audioFile.getAlbum());
        mediaBundle.putString(Constants.MediaBundle.SONG_TITLE,audioFile.getDisplayName());
        MediaControllerCompat.getMediaController(mActivity).getTransportControls().playFromUri(pathUri, mediaBundle);

        return sMediaPlayerHelper;
    }

    private void initMediaController(){
        mMediaControllerCompatCallback =new MediaControllerCompat.Callback() {

            @Override
            public void onPlaybackStateChanged(PlaybackStateCompat state) {
                super.onPlaybackStateChanged(state);
                if (state == null) {
                    return;
                }

                switch(state.getState()){
                    case PlaybackStateCompat.STATE_PLAYING:
                        mCurrentState = Constants.STATE.STATE_PLAYING;

                        break;
                    case PlaybackStateCompat.STATE_PAUSED:
                        mCurrentState = Constants.STATE.STATE_PAUSED;
                        break;
                    default:
                        break;
                }

                Log.d(TAG,"state: " + mCurrentState);

            }
        };
    }

    private void initConnectionCallback(){
        mMediaBrowserCompatConnectionCallback =  new MediaBrowserCompat.ConnectionCallback() {

            @Override
            public void onConnected() {
                super.onConnected();
                try {
                    mMediaControllerCompat = new MediaControllerCompat(mActivity, mMediaBrowserCompat.getSessionToken());
                    mMediaControllerCompat.registerCallback(mMediaControllerCompatCallback);
                    MediaControllerCompat.setMediaController(mActivity, mMediaControllerCompat);
//                    Uri pathUri = Uri.parse("/storage/emulated/0/Music/eMusic/Various Artists/Doing It in Lagos_ Boogie, Pop & Disco in 1980's Nigeria/13 Where Is the Answer.mp3");
//                    MediaControllerCompat.getMediaController(mActivity).getTransportControls().playFromUri(pathUri, null);

                } catch (RemoteException e) {

                }

            }
        };
    }

    private void setupMediaToggleButton(){
        //mPlayPauseToggleButton = findViewById<Button>(R.id.button)

        mMediaBrowserCompat = new MediaBrowserCompat(mActivity,
                new ComponentName(mActivity, MusicPlayService.class),
                mMediaBrowserCompatConnectionCallback,
                mActivity.getIntent().getExtras());

        Log.d(TAG, "mMediaBrowserCompat created");

        mMediaBrowserCompat.connect();
        Log.d(TAG, "mMediaBrowserCompat connect");

//        mPlayPauseToggleButton!!.setOnClickListener(object : View.OnClickListener {
//            override fun onClick(view: View) {
//                if (mCurrentState === Constants.STATE.STATE_PAUSED) {
//                    MediaControllerCompat.getMediaController(this@MainActivity).getTransportControls().togglePlay()
//                    mCurrentState = Constants.STATE.STATE_PLAYING
//                } else {
//                    if (MediaControllerCompat.getMediaController(this@MainActivity).getPlaybackState().getState() === PlaybackStateCompat.STATE_PLAYING) {
//                        MediaControllerCompat.getMediaController(this@MainActivity).getTransportControls().pause()
//                    }
//
//                    mCurrentState = Constants.STATE.STATE_PAUSED
//                }
//            }
//        })

    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){

        if( MediaControllerCompat.getMediaController(mActivity).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            MediaControllerCompat.getMediaController(mActivity).getTransportControls().pause();
        }

        if (mMediaBrowserCompat != null) {
            mMediaBrowserCompat.disconnect();
        }
    }

}
