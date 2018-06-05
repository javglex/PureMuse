package com.newpath.puremuse.ui.main;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newpath.puremuse.R;
import com.newpath.puremuse.helpers.MediaPlayerHelper;

import static com.newpath.puremuse.utils.Constants.COLLECTIONPOSKEY;
import static com.newpath.puremuse.utils.Constants.COLLECTIONTYPE;

/**
 * Displays song options when user clicks on song settings. (Add to playlist, remove from playlist, etc)
 */
public class SongOptionsFragment extends Fragment {

    final String TAG = "SongOptionsFragment";
    SongViewModel viewModel;


    public SongOptionsFragment newInstance(){
        SongOptionsFragment newFrag = new SongOptionsFragment();
        Bundle bundle = new Bundle();

        newFrag.setArguments(bundle);

        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG,"oncreate");
        viewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);


        //mMediaHelper = MediaPlayerHelper.getMediaPlayerInstance(getActivity());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        super.onCreateView(inflater,container,savedInstanceState);

        View rootview = inflater.inflate(R.layout.fragment_song_options, container, false);

        return rootview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

    }

}
