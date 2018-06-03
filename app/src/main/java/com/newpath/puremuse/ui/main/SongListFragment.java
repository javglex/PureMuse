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
import com.newpath.puremuse.models.AudioFileModel;
import com.newpath.puremuse.utils.Constants;

import java.util.ArrayList;

import static com.newpath.puremuse.utils.Constants.COLLECTIONPOSKEY;
import static com.newpath.puremuse.utils.Constants.COLLECTIONTYPE;

/**
 * This fragment will contain a static list of songs. To be used for inside Albums, playlists etc.
 */
public class SongListFragment extends Fragment implements OnItemClickListener {

    final static String TAG = "SongListFragment";
    RecyclerView mRvSongList;
    SongAdapter mSongAdapter;
    SongViewModel viewModel;
    int mCollectionPosition;
    int mCollectionType;
    MediaPlayerHelper mMediaHelper;

    public static SongListFragment newInstance(int type, int pos){
        SongListFragment newFrag = new SongListFragment();
        Bundle args = new Bundle();
        args.putInt(COLLECTIONPOSKEY,pos);
        args.putInt(COLLECTIONTYPE,type);
        newFrag.setArguments(args);
        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG,"oncreate");
        viewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);

        mCollectionPosition = getArguments().getInt(COLLECTIONPOSKEY,-1);
        mCollectionType = getArguments().getInt(COLLECTIONTYPE,-1);

        mMediaHelper = MediaPlayerHelper.getMediaPlayerInstance(getActivity());

        Log.d(TAG,"album position: " + mCollectionPosition);
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
        ArrayList<AudioFileModel> collection = new ArrayList<>();

        switch (mCollectionType){
            case Constants.COLLECTION_TYPE.ALBUM:
                collection = viewModel.getAlbumList().getValue().get(mCollectionPosition).getSongList();
                break;
            case Constants.COLLECTION_TYPE.PLAYLIST:
                collection = viewModel.getCollectionList().getValue().get(mCollectionPosition).getSongList();
                break;

        }


        Log.d(TAG,"album size:" + collection.size());
        Log.d(TAG,"first song:" + collection.get(0));

        mSongAdapter.updateList(collection);


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

        if (viewModel.getAlbumList()!=null && viewModel.getAlbumList().getValue()!=null) {
            mMediaHelper.setSongs(viewModel.getAlbumList().getValue().get(mCollectionPosition).getSongList(), position);
        }
    }


}
