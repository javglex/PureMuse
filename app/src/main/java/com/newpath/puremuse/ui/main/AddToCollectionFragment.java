package com.newpath.puremuse.ui.main;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.design.internal.NavigationMenuItemView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.newpath.puremuse.NavigationPageActivity;
import com.newpath.puremuse.R;
import com.newpath.puremuse.adapters.AddToCollectionAdapter;
import com.newpath.puremuse.adapters.CollectionsAdapter;
import com.newpath.puremuse.database.AppDatabase;
import com.newpath.puremuse.helpers.DatabaseHelper;
import com.newpath.puremuse.interfaces.OnFragmentResult;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.models.CollectionModel;
import com.newpath.puremuse.models.PlaylistModel;
import com.newpath.puremuse.utils.Constants;

import java.util.ArrayList;

/**
 * lets a user add a song to an existing playlist, or create a new playlist
 */
public class AddToCollectionFragment extends Fragment implements OnItemClickListener {

    final String TAG = "AddToCollectionFragment";
    RecyclerView mRvCollections;
    AddToCollectionAdapter mCollectionsAdapter;
    private SongViewModel viewModel;
    private static OnFragmentResult mOnFragmentResult;
    Button mButton;

    public static AddToCollectionFragment newInstance(){
        AddToCollectionFragment newFrag = new AddToCollectionFragment();
        Bundle args = new Bundle();
        newFrag.setArguments(args);
        return newFrag;
    }

    public static void registerOnResult( OnFragmentResult onFragmentResult){
        mOnFragmentResult = onFragmentResult;
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

        mButton = view.findViewById(R.id.btn_add_playlist);
        mRvCollections = (RecyclerView) view.findViewById(R.id.rv_collections);
        mCollectionsAdapter = new AddToCollectionAdapter(null, this);
        RecyclerView.LayoutManager mLayoutManager;
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRvCollections.setLayoutManager(mLayoutManager);
        mRvCollections.setAdapter(mCollectionsAdapter);
        Log.d(TAG,"onViewCreated");


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"btnAddToPlaylist clicked");
                if(viewModel!=null)
                    viewModel.addPlaylist("test"+ (System.currentTimeMillis() / 10000));
            }
        });

        viewModel.getCollectionObservable(Constants.COLLECTION_TYPE.PLAYLIST).observe(this, playlists -> {
            Log.d(TAG,"observable fired");
            Log.d(TAG,"observale song list size: "+playlists.size());
            mCollectionsAdapter.updateList((ArrayList<CollectionModel>) playlists);
        });
    }

    @Override
    public void onItemClicked(int position) {
        Log.d(TAG,"on item clicked: " + position);

        if (mOnFragmentResult==null) {
            Log.e(TAG,"mOnFragment==null");
            return;
        }
        Bundle bundle = new Bundle();
        bundle.putInt("COLLECTIONPOS", position);

        mOnFragmentResult.onResult(bundle);     //return selected playlist position
        getActivity().getSupportFragmentManager().popBackStack();

        //TODO: Finish this with real playlists
        DatabaseHelper.populateAsync((ArrayList<PlaylistModel>) viewModel.getCollection(Constants.COLLECTION_TYPE.PLAYLIST),AppDatabase.getAppDatabase(getActivity()), new DatabaseHelper.DbCallback<Void>() {
            @Override
            public void onFinished(Void result) {
                Log.d(TAG,"finished db populate");
            }

            @Override
            public void onError() {

            }
        });
    }



}
