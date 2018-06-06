package com.newpath.puremuse.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.newpath.puremuse.R;
import com.newpath.puremuse.adapters.AddToCollectionAdapter;
import com.newpath.puremuse.adapters.CollectionsAdapter;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.models.CollectionModel;
import com.newpath.puremuse.utils.Constants;

import java.util.ArrayList;

public class AddToCollectionFragment extends Fragment implements OnItemClickListener {

    final String TAG = "AddToCollectionFragment";
    RecyclerView mRvCollections;
    AddToCollectionAdapter mCollectionsAdapter;
    SongViewModel viewModel;

    public static AddToCollectionFragment newInstance(){
        AddToCollectionFragment newFrag = new AddToCollectionFragment();
        Bundle args = new Bundle();
        newFrag.setArguments(args);
        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle){
        return inflater.inflate(R.layout.fragment_add_to_collection,container, false);
    }

    public void onViewCreated(View view, Bundle savedInstanceState ){

        mRvCollections = (RecyclerView) view.findViewById(R.id.rv_collections);
        mCollectionsAdapter = new AddToCollectionAdapter(null, this);
        RecyclerView.LayoutManager mLayoutManager;

        mLayoutManager = new GridLayoutManager(getActivity(),2);

        mRvCollections.setLayoutManager(mLayoutManager);
        mRvCollections.setAdapter(mCollectionsAdapter);
        Log.d(TAG,"onViewCreated");

        viewModel.getCollectionObservable(Constants.COLLECTION_TYPE.PLAYLIST).observe(this, playlists -> {
            Log.d(TAG,"observable fired");
            Log.d(TAG,"observale song list size: "+playlists.size());
            mCollectionsAdapter.updateList((ArrayList<CollectionModel>) playlists);
        });
    }

    @Override
    public void onItemClicked(int position) {

    }
}
