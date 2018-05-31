package com.newpath.puremuse.ui.main;

import android.animation.LayoutTransition;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.newpath.puremuse.R;
import com.newpath.puremuse.adapters.AlbumsAdapter;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.models.AlbumModel;
import com.newpath.puremuse.models.AudioFileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Displays Albums that are generated in ui.main.SongViewModel
 */
public class AlbumsFragment extends Fragment implements OnItemClickListener {

    final String TAG = "AlbumsFragment";
    RecyclerView mRvAlbums;
    AlbumsAdapter mAlbumsAdapter;
    SongViewModel viewModel;

    public static AlbumsFragment newInstance(){
        AlbumsFragment newFrag = new AlbumsFragment();
        Bundle args = new Bundle();
        newFrag.setArguments(args);
        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);
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
        mAlbumsAdapter = new AlbumsAdapter(null, this);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(),2);
        mRvAlbums.setLayoutManager(mLayoutManager);
        mRvAlbums.setAdapter(mAlbumsAdapter);
        Log.d(TAG,"onViewCreated");

        viewModel.getAlbumList().observe(this, albums -> {
            Log.d(TAG,"observable fired");
            Log.d(TAG,"observale song list size: "+albums.size());
            mAlbumsAdapter.updateList(albums);
        });
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

        if (viewModel.getAlbumList()!=null && viewModel.getAlbumList().getValue()!=null) {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SongListFragment fragment = SongListFragment.newInstance(position);
            fragmentTransaction.replace(R.id.fl_fragments, fragment);
            fragmentTransaction.addToBackStack("MainFragment");
            fragmentTransaction.commit();
        }


    }


}
