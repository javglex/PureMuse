package com.newpath.puremuse.ui.main;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newpath.puremuse.NavigationPageActivity;
import com.newpath.puremuse.R;
import com.newpath.puremuse.adapters.SongAdapter;
import com.newpath.puremuse.helpers.MediaPlayerHelper;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.interfaces.OnOptionsClickListener;
import com.newpath.puremuse.models.AudioFileModel;

import java.util.ArrayList;

import static com.newpath.puremuse.utils.Constants.COLLECTIONPOSKEY;
import static com.newpath.puremuse.utils.Constants.COLLECTIONTYPE;

/**
 * This fragment will contain a static list of songs. To be used for inside Albums, playlists etc.
 */
public class SongListFragment extends Fragment implements OnItemClickListener, OnOptionsClickListener {

    final static String TAG = "SongListFragment";
    RecyclerView mRvSongList;
    SongAdapter mSongAdapter;
    SongViewModel viewModel;
    int mCollectionPosition;
    int mCollectionType;
    MediaPlayerHelper mMediaHelper;
    ConstraintLayout mainLayout;

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
        mainLayout = view.findViewById(R.id.main);

        mRvSongList = (RecyclerView) view.findViewById(R.id.rv_scan_list);
        mSongAdapter = new SongAdapter(this,this, new ArrayList<AudioFileModel>(), getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRvSongList.setLayoutManager(mLayoutManager);
        mRvSongList.setAdapter(mSongAdapter);

        ((NavigationPageActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((NavigationPageActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        mainLayout.setBackgroundColor(getResources().getColor(android.R.color.black));

        Log.d(TAG,"onViewCreated");
        ArrayList<AudioFileModel> collection = new ArrayList<>();


        collection = viewModel.getCollection(mCollectionType).get(mCollectionPosition).getSongList();



        Log.d(TAG,"album size:" + collection.size());
        if (collection.size()>0)
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
    public void onDestroy(){
        super.onDestroy();
        ((NavigationPageActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        ((NavigationPageActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(false);
    }


    @Override
    public void onItemClicked(int position) {
        Log.d(TAG,"position clicked: " + position);

        if (viewModel.getCollection(mCollectionType)!=null && viewModel.getCollection(mCollectionType).get(mCollectionPosition)!=null) {

            mMediaHelper.setSongsAndPlay(viewModel.getCollection(mCollectionType).get(mCollectionPosition).getSongList(), position);

        }
    }


    @Override
    public void onOptionsClicked(int position) {
        Log.d(TAG,"position clicked: " + position);

        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AudioFileModel song = viewModel.getCollection(mCollectionType).get(mCollectionPosition).getSongList().get(position);

        SongOptionsFragment fragment = SongOptionsFragment.newInstance(mCollectionType,position,mCollectionPosition);
        fragmentTransaction.add(R.id.fl_fragments, fragment);
        fragmentTransaction.addToBackStack("SongOptionsFragment");
        fragmentTransaction.commit();
    }
}
