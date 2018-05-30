package com.newpath.puremuse.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newpath.puremuse.R;
import com.newpath.puremuse.adapters.AlbumsAdapter;
import com.newpath.puremuse.adapters.SongAdapter;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.models.AudioFileModel;

import java.util.ArrayList;

/**
 * This fragment will contain a static list of songs. To be used for inside Albums, playlists etc.
 */
public class SongListFragment extends Fragment implements OnItemClickListener {

    final String TAG = "SongListFragment";
    RecyclerView mRvAlbums;
    SongAdapter mSongAdapter;


    public static SongListFragment newInstance(){
        SongListFragment newFrag = new SongListFragment();
        Bundle args = new Bundle();
        newFrag.setArguments(args);
        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG,"oncreate");

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_albums, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        mRvAlbums = (RecyclerView) view.findViewById(R.id.rv_albums);
        mSongAdapter = new SongAdapter(this, new ArrayList<AudioFileModel>(), getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRvAlbums.setLayoutManager(mLayoutManager);
        mRvAlbums.setAdapter(mSongAdapter);
        Log.d(TAG,"onViewCreated");


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

    }


}
