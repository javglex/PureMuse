package com.newpath.puremuse.ui.main;

import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.newpath.puremuse.R;
import com.newpath.puremuse.adapters.CollectionsAdapter;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.interfaces.OnOptionsClickListener;
import com.newpath.puremuse.models.CollectionModel;
import com.newpath.puremuse.utils.Constants;

import java.util.ArrayList;

import static com.newpath.puremuse.utils.Constants.COLLECTIONTYPE;

/**
 * Displays Albums that are generated in ui.main.SongViewModel
 */
public class CollectionsFragment extends Fragment implements OnItemClickListener, OnOptionsClickListener, View.OnClickListener {

    final String TAG = "CollectionsFragment";
    RecyclerView mRvCollections;
    Button btnCreatePlaylist;
    CollectionsAdapter mCollectionsAdapter;
    SongViewModel viewModel;
    int mCollectionType;

    public static CollectionsFragment newInstance(int type){
        CollectionsFragment newFrag = new CollectionsFragment();
        Bundle args = new Bundle();
        args.putInt(Constants.COLLECTIONTYPE,type);
        newFrag.setArguments(args);
        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);
        Log.d(TAG,"oncreate");
        mCollectionType = getArguments().getInt(COLLECTIONTYPE,-1);


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_collections, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        mRvCollections = (RecyclerView) view.findViewById(R.id.rv_albums);
        btnCreatePlaylist = view.findViewById(R.id.btn_createplaylist);
        if (mCollectionType==1) {
            btnCreatePlaylist.setOnClickListener(this);
            btnCreatePlaylist.setVisibility(View.VISIBLE);
        }
        mCollectionsAdapter = new CollectionsAdapter(null, this, this);
        RecyclerView.LayoutManager mLayoutManager;

        mLayoutManager = new GridLayoutManager(getActivity(),2);

        mRvCollections.setLayoutManager(mLayoutManager);
        mRvCollections.setAdapter(mCollectionsAdapter);
        Log.d(TAG,"onViewCreated");

        viewModel.getCollectionObservable(mCollectionType).observe(this, collections -> {
            Log.d(TAG,"observable fired" + mCollectionType);
            Log.d(TAG,"observale song list size: "+collections.size());
            mCollectionsAdapter.updateList((ArrayList<CollectionModel>) collections);
        });



    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        Log.d(TAG,"onResume()");
    }


    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        Log.d(TAG,"onResume()");

    }


    @Override
    public void onItemClicked(int position) {

        if (viewModel.getCollection(mCollectionType)!=null) {

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            SongListFragment fragment = SongListFragment.newInstance(mCollectionType,position);
            fragmentTransaction.replace(R.id.fl_fragments, fragment);
            fragmentTransaction.addToBackStack("MainFragment");
            fragmentTransaction.commit();

        }


    }

    @Override
    public void onOptionsClicked(int position) {
        Log.d(TAG,"position clicked: " + position);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_createplaylist:
                addNewPlaylistFragment();
                break;
            default:
                break;
        }
    }

    private void addNewPlaylistFragment(){
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            CreatePlaylistFragment fragment = CreatePlaylistFragment.newInstance();
            fragmentTransaction.replace(R.id.fl_fragments, fragment);
            fragmentTransaction.addToBackStack("CreatePlaylistFragment");
            fragmentTransaction.commit();
    }

}
