package com.newpath.puremuse.helpers;

import android.app.Activity;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleObserver;
import android.arch.lifecycle.OnLifecycleEvent;
import android.content.ComponentName;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.v4.media.MediaBrowserCompat;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.widget.Button;

import com.newpath.puremuse.models.AudioFileModel;
import com.newpath.puremuse.services.MusicPlayService;
import com.newpath.puremuse.utils.Constants;

import java.util.ArrayList;

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
    private int mCurrentUriIndex;                         //currently playing index of song in the list
    private ArrayList<AudioFileModel> mAudioFileQueue;

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

            } else if (mCurrentState == Constants.STATE.STATE_STOPPED){
                MediaControllerCompat.getMediaController(mActivity).getTransportControls().play();
                mCurrentState = Constants.STATE.STATE_PLAYING;
            }

        }catch(Exception e){
            Log.e(TAG,"togglePlay() " + e);
        }

    }

    public void play(){
        MediaControllerCompat.getMediaController(mActivity).getTransportControls().stop();
        MediaControllerCompat.getMediaController(mActivity).getTransportControls().play();
        mCurrentState = Constants.STATE.STATE_PLAYING;

    }



    public MediaPlayerHelper setSong(AudioFileModel audioFile){
        Log.d(TAG,"name: " + audioFile.getDisplayName());

        Uri pathUri = Uri.parse(audioFile.getPath());
        Bundle mediaBundle = new Bundle();
        mediaBundle.putString(Constants.MediaBundle.ALBUM_NAME,audioFile.getAlbum());
        mediaBundle.putString(Constants.MediaBundle.SONG_TITLE,audioFile.getDisplayName());
        MediaControllerCompat.getMediaController(mActivity).getTransportControls().playFromUri(pathUri, mediaBundle);

        return sMediaPlayerHelper;
    }

    public MediaPlayerHelper setSongs(ArrayList<AudioFileModel> audioFiles, int startingPos){

        mAudioFileQueue = audioFiles;
        mCurrentUriIndex = startingPos;
        Bundle mediaBundle = new Bundle();
        mediaBundle.putString(Constants.MediaBundle.ALBUM_NAME, mAudioFileQueue.get(mCurrentUriIndex).getAlbum());
        mediaBundle.putString(Constants.MediaBundle.SONG_TITLE, mAudioFileQueue.get(mCurrentUriIndex).getDisplayName());


        Uri pathUri = Uri.parse(mAudioFileQueue.get(mCurrentUriIndex).getPath());

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
                Log.d(TAG,"onPlaybackStateChange: " + state);

                switch(state.getState()){
                    case PlaybackStateCompat.STATE_PLAYING:
                        mCurrentState = Constants.STATE.STATE_PLAYING;
                        break;
                    case PlaybackStateCompat.STATE_PAUSED:
                        mCurrentState = Constants.STATE.STATE_PAUSED;
                        break;
                    case PlaybackStateCompat.STATE_STOPPED:
                        mCurrentState = Constants.STATE.STATE_STOPPED;
                        break;
                    case PlaybackStateCompat.STATE_SKIPPING_TO_NEXT:
                        mCurrentState = Constants.STATE.STATE_SKIPPING_TO_NEXT;
                        setSong(mAudioFileQueue.get(getNextIndex(mCurrentUriIndex)));
                        play();
                        break;
                    case PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS:
                        mCurrentState = Constants.STATE.STATE_SKIPPING_TO_PREVIOUS;
                        setSong(mAudioFileQueue.get(getPreviousIndex(mCurrentUriIndex)));
                        play();
                        break;
                    default:
                        break;
                }

                Log.d(TAG,"mcurrentstate: " + mCurrentState);

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


    public int getNextIndex(int currentIndex){
        if (++currentIndex >= mAudioFileQueue.size()){
            mCurrentUriIndex = 0;
        } else mCurrentUriIndex = currentIndex;

        return mCurrentUriIndex;
    }


    public int getPreviousIndex(int currentIndex){
        if (--currentIndex < 0 ){
            mCurrentUriIndex = mAudioFileQueue.size()-1;
        } else mCurrentUriIndex = currentIndex;

        return mCurrentUriIndex;
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onStop(){

        Log.d(TAG,"onStop");

        if( MediaControllerCompat.getMediaController(mActivity).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            MediaControllerCompat.getMediaController(mActivity).getTransportControls().pause();
        }

        if (mMediaBrowserCompat != null) {
            mMediaBrowserCompat.disconnect();
            mMediaBrowserCompat = null;
        }
        if (mMediaControllerCompatCallback!=null){
            mMediaControllerCompatCallback.onSessionDestroyed();

        }
    }


    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy(){
        Log.d(TAG,"onDestroy");

        if( MediaControllerCompat.getMediaController(mActivity).getPlaybackState().getState() == PlaybackStateCompat.STATE_PLAYING ) {
            MediaControllerCompat.getMediaController(mActivity).getTransportControls().pause();
        }

        if (mMediaBrowserCompat != null) {
            mMediaBrowserCompat.disconnect();
            mMediaBrowserCompat = null;
        }
        if (mMediaControllerCompatCallback!=null){
            mMediaControllerCompatCallback.onSessionDestroyed();

        }
    }

}
