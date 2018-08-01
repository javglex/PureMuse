package com.newpath.puremuse.ui.main;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.newpath.puremuse.R;
import com.newpath.puremuse.helpers.MediaPlayerHelper;
import com.newpath.puremuse.interfaces.OnFragmentResult;
import com.newpath.puremuse.models.AudioFileModel;
import com.newpath.puremuse.models.PlaylistModel;
import com.newpath.puremuse.utils.Constants;

import java.util.ArrayList;

import static com.newpath.puremuse.utils.Constants.COLLECTIONPOSKEY;
import static com.newpath.puremuse.utils.Constants.COLLECTIONTYPE;

/**
 * Displays song options when user clicks on song settings. (Add to playlist, remove from playlist, etc)
 */
public class SongOptionsFragment extends Fragment implements View.OnClickListener {

    final String TAG = "SongOptionsFragment";
    SongViewModel viewModel;
    AudioFileModel mSelectedSong;
    Button mBtnAddToPlaylist, mBtnPlaySong;



    public static SongOptionsFragment newInstance(AudioFileModel song){

        SongOptionsFragment newFrag = new SongOptionsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Constants.SONGPROPERTIES.ALBUM_TITLE, song.getAlbum());
        bundle.putString(Constants.SONGPROPERTIES.PATH, song.getPath());
        bundle.putString(Constants.SONGPROPERTIES.DISPLAY_NAME, song.getDisplayName());
        bundle.putString(Constants.SONGPROPERTIES.ARTIST, song.getArtist());
        bundle.putString(Constants.SONGPROPERTIES.DURATION, song.getDuration());
        bundle.putString(Constants.SONGPROPERTIES.DATA, song.getData());
        bundle.putString(Constants.SONGPROPERTIES.SONG_TITLE, song.getTitle());
        bundle.putString(Constants.SONGPROPERTIES._ID, song.get_id());
        newFrag.setArguments(bundle);

        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG,"oncreate");
        viewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);

        mSelectedSong = buildSelectedSong(getArguments());

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

        mBtnAddToPlaylist.setOnClickListener(this);
        mBtnPlaySong.setOnClickListener(this);

        tvSongTitle.setText(mSelectedSong.getDisplayName());
        albumName.setText(mSelectedSong.getAlbum());

    }

    public AudioFileModel buildSelectedSong(Bundle bundle){
        String albumTitle = bundle.getString(Constants.SONGPROPERTIES.ALBUM_TITLE, "");
        String path = bundle.getString(Constants.SONGPROPERTIES.PATH, "");
        String displayName = bundle.getString(Constants.SONGPROPERTIES.DISPLAY_NAME, "");
        String artist = bundle.getString(Constants.SONGPROPERTIES.ARTIST, "");
        String duration = bundle.getString(Constants.SONGPROPERTIES.DURATION, "");
        String data = bundle.getString(Constants.SONGPROPERTIES.DATA,"");
        String songTitle = bundle.getString(Constants.SONGPROPERTIES.SONG_TITLE, "");
        String _id = bundle.getString(Constants.SONGPROPERTIES._ID, "");
        Log.d(TAG,"song built: " + "\n" + albumTitle +"\n" + path + "\n" +displayName + "\n" +artist + "\n" +duration + "\n" +data + "\n" +songTitle + "\n" +_id);
        return new AudioFileModel(_id,artist,songTitle,data,displayName,duration,albumTitle,path, true);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_play:

                break;
            case R.id.btn_add_playlist:
                addToCollectionFragment();
                break;
        }
    }
}
