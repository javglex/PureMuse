package com.newpath.puremuse.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.media.session.MediaControllerCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.media.session.MediaButtonReceiver;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.newpath.puremuse.R;
import com.newpath.puremuse.helpers.AudioFileScanner;
import com.newpath.puremuse.helpers.MediaPlayerHelper;
import com.newpath.puremuse.models.AudioFileModel;
import com.newpath.puremuse.services.MusicPlayService;

import static android.media.MediaPlayer.SEEK_CLOSEST;
import static com.newpath.puremuse.utils.Constants.COLLECTIONPOSKEY;
import static com.newpath.puremuse.utils.Constants.COLLECTIONTYPE;

public class LargePlayerFragment extends Fragment implements View.OnClickListener{

    public final static String TAG = "LargePlayerFragment";
    private ImageButton ibtnPlayPause, ibtnSeekNext, ibtnSeekPrev;
    private TextView tvCurrentTime, tvTotalTime, tvAlbumName, tvSongName, tvArtist;
    private ImageView mImgCover;
    private SeekBar seekbarSong;
    private MediaPlayerHelper mMediaHelper;
    private MusicPlayService.TimeElapsed mTimeElapsedObs;
    AudioFileModel mCurrentSongPlaying;
    private MediaPlayerHelper.MusicPlayerStateChange musicPlayerStateChange;


    public static LargePlayerFragment newInstance(int type, int pos){
        LargePlayerFragment newFrag = new LargePlayerFragment();
        Bundle args = new Bundle();
        args.putInt(COLLECTIONPOSKEY,pos);
        args.putInt(COLLECTIONTYPE,type);
        newFrag.setArguments(args);
        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMediaHelper = MediaPlayerHelper.getMediaPlayerInstance(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_large_player, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        ibtnPlayPause = view.findViewById(R.id.ibtn_play_pause);
        ibtnSeekNext = view.findViewById(R.id.ibtn_seek_next);
        ibtnSeekPrev = view.findViewById(R.id.ibtn_seek_prev);
        seekbarSong = (SeekBar) view.findViewById(R.id.seekBar_song); // initiate the Seek bar
        tvCurrentTime = view.findViewById(R.id.tv_elapsed_time);
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        tvAlbumName = view.findViewById(R.id.tv_album);
        tvSongName = view.findViewById(R.id.tv_name);
        tvArtist = view.findViewById(R.id.tv_artist_name);
        mImgCover = view.findViewById(R.id.iv_album_cover);

        mCurrentSongPlaying = mMediaHelper.getPlayedSong();
        seekbarSong.setProgress(0);
        setDisplayData();

        seekbarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!b)   //only continue f initiated by user
                    return;
                if(mCurrentSongPlaying==null)
                    return;
                int duration = Integer.parseInt(mCurrentSongPlaying.getDuration());
                int timeToSeekTo = i*duration/1000;
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().seekTo(timeToSeekTo);
                MusicPlayService.correctTimeAfterSeek(timeToSeekTo);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        initProgressBar();

        ibtnPlayPause.setOnClickListener(this);
        ibtnSeekNext.setOnClickListener(this);
        ibtnSeekPrev.setOnClickListener(this);
    }

    private void initProgressBar(){
        seekbarSong.setMax(1000);

        try{    //incase our current song playing does not contain the property "getDuration".
            if(mCurrentSongPlaying==null)
                return;
            int duration = Integer.parseInt(mCurrentSongPlaying.getDuration());
            int minutes = (duration / 1000)  / 60;
            int seconds = (int)((duration / 1000) % 60);
            tvTotalTime.setText(String.format("%02d", minutes)+":"+String.format("%02d", seconds));
            if (mTimeElapsedObs==null)
                mTimeElapsedObs = new MusicPlayService.TimeElapsed() {
                    @Override
                    public void onTimerFired(float timeElapsed) {
                        float percentage = timeElapsed/duration;
                        percentage*=1000;
                        setProgressBarValue((int)percentage, timeElapsed);

                    }
                };
        }catch (Exception e){
            Log.e(TAG,e.getLocalizedMessage());
        }

        MusicPlayService.registerTimeElapsed(mTimeElapsedObs);

    }


    private void setDisplayData(){
        if (mCurrentSongPlaying==null)
            return;

        tvAlbumName.setText(mCurrentSongPlaying.getAlbum());
        tvSongName.setText(mCurrentSongPlaying.getDisplayName());
        tvArtist.setText(mCurrentSongPlaying.getArtist());
        loadAlbumImage();
    }

    private void setProgressBarValue(int progress, float timeElapsed){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekbarSong.setProgress(progress,false);   //multiplied by 10 because max is out of 1000 for smoothness
        } else seekbarSong.setProgress(progress);

        Log.d(TAG,"seekbar max: "+seekbarSong.getMax() + "seekbar current: "+seekbarSong.getProgress());
        int minutes = (int) ((timeElapsed / 1000)  / 60);
        int seconds = (int)((timeElapsed / 1000) % 60);
        tvCurrentTime.setText(String.format("%02d", minutes)+":"+String.format("%02d", seconds));
    }


    public void loadAlbumImage(){

        try {
            if( mCurrentSongPlaying==null)
                return;
            String imagePath = AudioFileScanner.getAlbumImage(mCurrentSongPlaying.getAlbumId());

            RequestOptions options = new RequestOptions();
            options.centerCrop();

            if (imagePath == null) {
                Glide.with(mImgCover.getContext()).load(R.drawable.ic_album_24dp)
                        .thumbnail(0.5f)
                        .apply(options)
                        .into(mImgCover);
            } else {

                Glide.with(mImgCover.getContext()).load(imagePath)
                        .thumbnail(0.5f)
                        .apply(options)
                        .into(mImgCover);
            }
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onStart(){
        super.onStart();
        initProgressBar();

    }

    @Override
    public void onResume(){
        super.onResume();
        musicPlayerStateChange = new MediaPlayerHelper.MusicPlayerStateChange() {
            @Override
            public void onPlaying() {
                mCurrentSongPlaying = mMediaHelper.getPlayedSong();
                setDisplayData();
                initProgressBar();
                ibtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_pause_24dp));

            }

            @Override
            public void onPaused() {
                ibtnPlayPause.setImageDrawable(getResources().getDrawable(R.drawable.ic_play_arrow_24dp));
            }

            @Override
            public void onStopped() {

            }

            @Override
            public void onSkipped() {
            }
        };
        mMediaHelper.registerPlayerState(musicPlayerStateChange);
    }

    @Override
    public void onPause(){
        super.onPause();
        MusicPlayService.unregisterTimeElapsed(mTimeElapsedObs);
        mMediaHelper.unregisterPlayerState(musicPlayerStateChange);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ibtn_play_pause:
                mMediaHelper.togglePlay();
                break;
            case R.id.ibtn_seek_next:
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().skipToNext();
                break;
            case R.id.ibtn_seek_prev:
                MediaControllerCompat.getMediaController(getActivity()).getTransportControls().skipToPrevious();
                break;
        }
    }
}
