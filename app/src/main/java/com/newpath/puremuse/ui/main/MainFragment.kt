package com.newpath.puremuse.ui.main

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.provider.MediaStore
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.newpath.puremuse.NavigationPageActivity
import com.newpath.puremuse.R
import com.newpath.puremuse.adapters.SongAdapter
import com.newpath.puremuse.database.AppDatabase
import com.newpath.puremuse.helpers.DatabaseHelper
import com.newpath.puremuse.helpers.MediaPlayerHelper
import com.newpath.puremuse.models.AudioFileModel
import com.newpath.puremuse.helpers.StoragePermissionHelper
import com.newpath.puremuse.interfaces.OnItemClickListener
import com.newpath.puremuse.interfaces.OnOptionsClickListener
import com.newpath.puremuse.models.PlaylistModel
import com.newpath.puremuse.utils.Constants
import kotlinx.android.synthetic.main.fragment_main.*


/**
 * Home fragment. For now it just shows the user songs from his device.
 */
class MainFragment : Fragment(), OnItemClickListener, OnOptionsClickListener {

    var TAG: String = "MainFragment"
    internal var mAlbumPosition: Int = 0
    private lateinit var viewModel: SongViewModel
    private lateinit var songAdapter: SongAdapter
    private lateinit var mMediaHelper: MediaPlayerHelper;

    companion object {
        val ALBUMPOSKEY = "ALBUMPOSITION"
        val ISMAIN:String = "ISMAIN";

        fun newInstance(pos: Int) : MainFragment {
            val newFrag = MainFragment()
            val args = Bundle()
            args.putInt(ALBUMPOSKEY, pos)

            newFrag.setArguments(args)
            return newFrag
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAlbumPosition = arguments!!.getInt(ALBUMPOSKEY, -1)
        Log.d(TAG, "album position: $mAlbumPosition")

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        return inflater.inflate(R.layout.fragment_main, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Creates a vertical Layout Manager
        rv_scan_list.layoutManager = LinearLayoutManager(activity)

        et_searchfiles.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG,"afterTextChanged: "+ s);

            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG, "beforeTextChanged: " + s);

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG,"onTextChanged: "+ s);

                var tempSearchList = ArrayList<AudioFileModel>();
                if (count==0){      //if there is nothing in the input field. Revert to showing entire list.
                    tempSearchList = viewModel.scannedSongList.value!!
                    viewModel.updateSearchedSongList(tempSearchList);
                    return;

                }

                for (song in viewModel.scannedSongList.value!!){
                    Log.d(TAG,"song searching for: "+ song);
                    Log.d(TAG,"using string: "+ s);

                    if (song.toSearchableString().contains(s.toString().toLowerCase())){
                        Log.d(TAG,"match!");
                        tempSearchList.add(song);
                    }
                }

                viewModel.updateSearchedSongList(tempSearchList);


            }
        })


        // Access the RecyclerView Adapter and load the data into it
        songAdapter = SongAdapter(this, this, ArrayList<AudioFileModel>(), this.context!!)
        rv_scan_list.adapter = songAdapter;


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        Log.d(TAG,"OnActivityCreated");
        mMediaHelper = MediaPlayerHelper.getMediaPlayerInstance(activity as NavigationPageActivity)
        viewModel = ViewModelProviders.of(activity!!).get(SongViewModel::class.java)
        viewModel.searchedSongList.observe(this, Observer { data ->
            Log.d(TAG,"observed searchedSongList change" + data!!.size);
            if (songAdapter!=null) {
                Log.d(TAG,"updating songadapter list..")
                songAdapter.updateList(data);
            }
        });

        if (savedInstanceState!=null)
            return;
        DatabaseHelper.retrieveAsync(AppDatabase.getAppDatabase(activity), object: DatabaseHelper.DbCallback <ArrayList<PlaylistModel>> {
            override fun onFinished(result: java.util.ArrayList<PlaylistModel>) {
                Log.d(TAG, "finished db retrieval")
                Log.d(TAG, "result is of size: " + result.size)
                if (viewModel!=null) {
                    viewModel.setPlaylists(result);
                    try {
                        Log.d(TAG, "test result song: " + result.get(0).getSong(0).displayName);
                    }catch(e:Exception){
                        e.printStackTrace();
                    }
                }
            }

            override fun onError() {

            }
        });

    }


     override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {

            if (::viewModel.isInitialized)
                viewModel.updateSearchedSongList(viewModel.scannedSongList.value!!)
        }
    }

    override fun onResume() {
        super.onResume()


    }

    override fun onItemClicked(pos:Int) {
        Log.d(TAG,"position clicked: " + pos);

        if (viewModel.searchedSongList!=null && viewModel.searchedSongList.value!=null) {

            mMediaHelper.setSongsAndPlay(viewModel.searchedSongList.value!!, pos);
        }

    }

    override fun onOptionsClicked(position: Int) {
        Log.d(TAG,"position clicked: " + position);
        val fragmentManager = fragmentManager
        val fragmentTransaction = fragmentManager!!.beginTransaction()
        val song = viewModel.searchedSongList.value!![position]

        val frag = fragmentManager.findFragmentByTag("SongOptionsFragment")

        if (frag == null) { //fragment not in back stack, create it.
            val fragment = SongOptionsFragment.newInstance(-1, position, -1)
            fragmentTransaction.add(R.id.fl_fragments, fragment,"SongOptionsFragment")
            fragmentTransaction.addToBackStack("SongOptionsFragment")
            fragmentTransaction.commit()
        }
    }


//    override fun onClick(view: View){
//        when (view?.id) {
//            R.id.btn_scan -> {
//                Log.i(TAG,"onScanClicked!");
//                if (StoragePermissionHelper.handlePermissions(activity))
//                    (activity as MainActivity).startScan(activity as MainActivity);
//            }
//            else -> {
//            }
//        }
//    }

}
