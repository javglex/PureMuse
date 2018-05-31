package com.newpath.puremuse.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newpath.puremuse.R;
import com.newpath.puremuse.adapters.SongAdapter;
import com.newpath.puremuse.helpers.MediaPlayerHelper;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.models.AlbumModel;
import com.newpath.puremuse.models.AudioFileModel;

import java.util.ArrayList;

/**
 * This fragment will contain a static list of songs. To be used for inside Albums, playlists etc.
 */
public class SongListFragment extends Fragment implements OnItemClickListener {

    final static String TAG = "SongListFragment";
    final static String ALBUMPOSKEY = "ALBUMPOSITION";
    RecyclerView mRvSongList;
    SongAdapter mSongAdapter;
    SongViewModel viewModel;
    int mAlbumPosition;
    MediaPlayerHelper mMediaHelper;

    public static SongListFragment newInstance(int pos){
        SongListFragment newFrag = new SongListFragment();
        Bundle args = new Bundle();
        args.putInt(ALBUMPOSKEY,pos);
        newFrag.setArguments(args);
        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG,"oncreate");
        viewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);
        mAlbumPosition = getArguments().getInt(ALBUMPOSKEY,-1);
        mMediaHelper = MediaPlayerHelper.getMediaPlayerInstance(getActivity());

        Log.d(TAG,"album position: " + mAlbumPosition);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        mRvSongList = (RecyclerView) view.findViewById(R.id.rv_scan_list);
        mSongAdapter = new SongAdapter(this, new ArrayList<AudioFileModel>(), getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRvSongList.setLayoutManager(mLayoutManager);
        mRvSongList.setAdapter(mSongAdapter);
        Log.d(TAG,"onViewCreated");

        ArrayList<AudioFileModel> album = viewModel.getAlbumList().getValue().get(mAlbumPosition).getSongList();
        Log.d(TAG,"album size:" + album.size());
        Log.d(TAG,"first song:" + album.get(0));

        mSongAdapter.updateList(album);


    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }


    @Override
    public void onItemClicked(int position) {
        Log.d(TAG,"position clicked: " + position);

        AudioFileModel audioFile ;
        if (viewModel.getAlbumList()!=null && viewModel.getAlbumList().getValue()!=null) {
            audioFile = viewModel.getAlbumList().getValue().get(mAlbumPosition).getSongList().get(position);
            Log.d(TAG,"name: " + audioFile.getDisplayName());
            mMediaHelper.setSong(audioFile).play();
        }
    }


}
