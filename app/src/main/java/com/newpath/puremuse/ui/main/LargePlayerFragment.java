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
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.media.session.MediaButtonReceiver;

import com.newpath.puremuse.R;
import com.newpath.puremuse.helpers.MediaPlayerHelper;
import com.newpath.puremuse.models.AudioFileModel;
import com.newpath.puremuse.services.MusicPlayService;

import static android.media.MediaPlayer.SEEK_CLOSEST;
import static com.newpath.puremuse.utils.Constants.COLLECTIONPOSKEY;
import static com.newpath.puremuse.utils.Constants.COLLECTIONTYPE;

public class LargePlayerFragment extends Fragment implements View.OnClickListener{

    public final static String TAG = "LargePlayerFragment";
    private ImageButton ibtnPlayPause, ibtnSeekNext, ibtnSeekPrev;
    private TextView tvCurrentTime, tvTotalTime;
    private SeekBar seekbarSong;
    private MediaPlayerHelper mMediaHelper;
    private MusicPlayService.TimeElapsed mTimeElapsedObs;


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
        seekbarSong.setProgress(0);
        mMediaHelper.registerPlayerState(new MediaPlayerHelper.MusicPlayerStateChange() {
            @Override
            public void onPlaying() {
                initProgressBar();
            }

            @Override
            public void onPaused() {
            }

            @Override
            public void onStopped() {

            }

            @Override
            public void onSkipped() {
            }
        });

        seekbarSong.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(!b)   //only continue f initiated by user
                    return;
                AudioFileModel currentSongPlaying = mMediaHelper.getPlayedSong();
                if(currentSongPlaying==null)
                    return;
                int duration = Integer.parseInt(currentSongPlaying.getDuration());
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
            AudioFileModel currentSongPlaying = mMediaHelper.getPlayedSong();
            if(currentSongPlaying==null)
                return;
            int duration = Integer.parseInt(currentSongPlaying.getDuration());
            int minutes = (duration / 1000)  / 60;
            int seconds = (int)((duration / 1000) % 60);
            tvTotalTime.setText(String.format("%02d", minutes)+":"+String.format("%02d", seconds));
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


    private void setProgressBarValue(int progress, float timeElapsed){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            seekbarSong.setProgress(progress,false);   //multiplied by 10 because max is out of 1000 for smoothness
        } else seekbarSong.setProgress(progress);

        Log.d(TAG,"seekbar max: "+seekbarSong.getMax() + "seekbar current: "+seekbarSong.getProgress());
        int minutes = (int) ((timeElapsed / 1000)  / 60);
        int seconds = (int)((timeElapsed / 1000) % 60);
        tvCurrentTime.setText(String.format("%02d", minutes)+":"+String.format("%02d", seconds));
    }

    @Override
    public void onStart(){
        super.onStart();
        initProgressBar();

    }

    @Override
    public void onPause(){
        super.onPause();
        MusicPlayService.unregisterTimeElapsed();
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
