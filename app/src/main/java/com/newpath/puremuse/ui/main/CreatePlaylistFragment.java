package com.newpath.puremuse.ui.main;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newpath.puremuse.R;
import com.newpath.puremuse.adapters.CollectionsAdapter;
import com.newpath.puremuse.models.CollectionModel;
import com.newpath.puremuse.utils.Constants;

import java.util.ArrayList;

import static com.newpath.puremuse.utils.Constants.COLLECTIONTYPE;

public class CreatePlaylistFragment extends Fragment implements View.OnClickListener, TextWatcher {

    final String TAG = "CreatePlaylistFragment";
    Button mBtnCancel;
    Button mBtnCreate;
    EditText mEtPlaylistName;
    String mPlaylistName="";
    private SongViewModel viewModel;

    public static CreatePlaylistFragment newInstance(){
        CreatePlaylistFragment newFrag = new CreatePlaylistFragment();
        return newFrag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        Log.d(TAG,"oncreate");
        viewModel = ViewModelProviders.of(getActivity()).get(SongViewModel.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_create_playlist, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){

        mBtnCancel = view.findViewById(R.id.btn_cancel);
        mBtnCreate = view.findViewById(R.id.btn_create);
        mEtPlaylistName= view.findViewById(R.id.et_playlist_name);

        mBtnCancel.setOnClickListener(this);
        mBtnCreate.setOnClickListener(this);
        mEtPlaylistName.addTextChangedListener(this);

    }


    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_cancel:
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            case R.id.btn_create:
                Log.d(TAG,"btnAddToPlaylist clicked");
                if(viewModel!=null)
                    viewModel.addPlaylist(getPlaylistName());
                getActivity().getSupportFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int count) {
        if (count==0){      //if there is nothing in the input field. disable create button
            mBtnCreate.setEnabled(false);
            mBtnCreate.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }else {
            mBtnCreate.setEnabled(true);
            mBtnCreate.setTextColor(getResources().getColor(android.R.color.white));
            mPlaylistName = charSequence.toString();
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }


    private String getPlaylistName(){
        return mPlaylistName.length()>0?mPlaylistName:"New Playlist "+ System.currentTimeMillis();
    }
}
