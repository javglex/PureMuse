package com.newpath.puremuse.ui.main;


import androidx.lifecycle.ViewModelProviders;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.newpath.puremuse.R;
import com.newpath.puremuse.database.AppDatabase;
import com.newpath.puremuse.helpers.DatabaseHelper;
import com.newpath.puremuse.helpers.MediaPlayerHelper;
import com.newpath.puremuse.interfaces.OnFragmentResult;
import com.newpath.puremuse.models.AudioFileModel;
import com.newpath.puremuse.models.PlaylistModel;
import com.newpath.puremuse.utils.Constants;

import java.util.ArrayList;

/**
 * Displays song options when user clicks on song settings. (Add to playlist, remove from playlist, etc)
 */
public class SongOptionsFragment extends Fragment implements View.OnClickListener {

    final String TAG = "SongOptionsFragment";
    SongViewModel viewModel;
    AudioFileModel mSelectedSong;
    Button mBtnAddToPlaylist, mBtnPlaySong, mBtnRemoveFromPlaylist;
    private MediaPlayerHelper mMediaHelper;
    private int mSongpos, mColpos, mColType;


    public static SongOptionsFragment newInstance(int coltype, int songpos, int colpos){

        SongOptionsFragment newFrag = new SongOptionsFragment();
        Bundle bundle = new Bundle();

        bundle.putInt(Constants.COLLECTIONPOSKEY, colpos);
        bundle.putInt(Constants.SONGPOSTKEY, songpos);
        bundle.putInt(Constants.COLLECTIONTYPE,coltype);
        newFrag.setArguments(bundle);

        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG,"oncreate");
        viewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);
        mMediaHelper = MediaPlayerHelper.getMediaPlayerInstance(getActivity());
        Bundle args = getArguments();
        mColpos = args.getInt(Constants.COLLECTIONPOSKEY,-1);
        mSongpos = args.getInt(Constants.SONGPOSTKEY,-1);
        mColType = args.getInt(Constants.COLLECTIONTYPE,-1);
        buildSelectedSong();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        super.onCreateView(inflater,container,savedInstanceState);

        View rootview = inflater.inflate(R.layout.fragment_song_options, container, false);

        return rootview;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        TextView tvSongTitle = view.findViewById(R.id.tv_song_title);
        TextView albumName = view.findViewById(R.id.tv_album_name);
        mBtnPlaySong = view.findViewById(R.id.btn_play);
        mBtnAddToPlaylist = view.findViewById(R.id.btn_add_playlist);
        mBtnRemoveFromPlaylist = view.findViewById(R.id.btn_remove_from_playlist);

        mBtnAddToPlaylist.setOnClickListener(this);
        mBtnPlaySong.setOnClickListener(this);
        mBtnRemoveFromPlaylist.setOnClickListener(this);

        tvSongTitle.setText(mSelectedSong.getDisplayName());
        albumName.setText(mSelectedSong.getAlbum());

        if (mColType == 1){
            mBtnRemoveFromPlaylist.setVisibility(View.VISIBLE);
        } else mBtnRemoveFromPlaylist.setVisibility(View.GONE);
    }

    public void buildSelectedSong(){

        if (mColpos==-1 && mSongpos == -1 && mColType == -1) {
            return;
        }

        if (mColType==-1 && mSongpos!=-1){

            if (viewModel.getSearchedSongList()!=null && viewModel.getSearchedSongList().getValue()!=null) {
                mSelectedSong = viewModel.getSearchedSongList().getValue().get(mSongpos);
            }

        }else if (viewModel.getCollection(mColType)!=null && viewModel.getCollection(mColType).get(mColpos)!=null) {
            mSelectedSong = viewModel.getCollection(mColType).get(mColpos).getSongList().get(mSongpos);

        } else
            return;


    }

    public void playSelectedSong(){

        if (mColpos==-1 && mSongpos == -1 && mColType == -1) {
            return;
        }

        if (mColType==-1 && mSongpos!=-1){

            if (viewModel.getSearchedSongList()!=null && viewModel.getSearchedSongList().getValue()!=null) {
                mSelectedSong = viewModel.getSearchedSongList().getValue().get(mSongpos);
                mMediaHelper.setSongsAndPlay(viewModel.getSearchedSongList().getValue(), mSongpos);
            }

        }else if (viewModel.getCollection(mColType)!=null && viewModel.getCollection(mColType).get(mColpos)!=null) {
            mSelectedSong = viewModel.getCollection(mColType).get(mColpos).getSongList().get(mSongpos);
            mMediaHelper.setSongsAndPlay(viewModel.getCollection(mColType).get(mColpos).getSongList(), mSongpos);

        } else
            return;


    }

    private void addToCollectionFragment(){
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddToCollectionFragment fragment = AddToCollectionFragment.newInstance();
        fragmentTransaction.replace(R.id.fl_fragments, fragment);
        fragmentTransaction.addToBackStack("AddToCollectionFragment");
        fragmentTransaction.commit();

        AddToCollectionFragment.registerOnResult(new OnFragmentResult() {
            @Override
            public void onResult(Bundle bundle) {

                int position = bundle.getInt("COLLECTIONPOS",-1);
                if (position==-1){
                    Log.w(TAG,"position==-1");
                    return;
                }

                if (viewModel!=null && viewModel.getCollection(Constants.COLLECTION_TYPE.PLAYLIST)!=null)
                {
                    viewModel.addToPlaylist(position,mSelectedSong);
                } else
                    Log.e(TAG,"viewModel!=null && viewModel.getCollection(position)!=null");

                getActivity().getSupportFragmentManager().popBackStack();

            }
        });
    }

    private void removeFromCollection(){

        Log.d(TAG,"removing from collection...");
        if (mColpos==-1 || mColType!=1){ //if a collection was not selected or if the collection type is not of playlist
            Log.e(TAG,"position or type incorrect");
            return;
        }

        if (viewModel!=null && viewModel.getCollection(Constants.COLLECTION_TYPE.PLAYLIST)!=null)
        {
            viewModel.removeFromPlaylist(mColpos,mSelectedSong);
        } else {
            Log.e(TAG, "viewModel!=null && viewModel.getCollection(position)!=null");
            return;
        }


        DatabaseHelper.populateAsync(
            (ArrayList<PlaylistModel>) viewModel.getCollection(Constants.COLLECTION_TYPE.PLAYLIST),
            AppDatabase.getAppDatabase(getActivity()),
            new DatabaseHelper.DbCallback<Void>() {

                @Override
                public void onFinished(Void result) {
                    Log.d(TAG,"finished db populate");
                }

                @Override
                public void onError() {

                }
            });

        //update searched song list so that parent fragment displays updated list correctly
        viewModel.updateSearchedSongList(viewModel.getCollection(mColType).get(mColpos).getSongList());
        getActivity().getSupportFragmentManager().popBackStack();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:
                getActivity().getSupportFragmentManager().popBackStack();

                playSelectedSong();

                break;
            case R.id.btn_add_playlist:
                addToCollectionFragment();
                break;
            case R.id.btn_remove_from_playlist:
                removeFromCollection();
                break;
            default:
                break;
        }
    }
}
