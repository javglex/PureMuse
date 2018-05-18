package com.newpath.puremuse.ui.main

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import com.newpath.puremuse.MainActivity
import com.newpath.puremuse.R
import com.newpath.puremuse.adapters.SongAdapter
import com.newpath.puremuse.models.AudioFileModel
import com.newpath.puremuse.utils.StoragePermissionHandler
import kotlinx.android.synthetic.main.main_fragment.*

class MainFragment : Fragment() {

    var TAG: String = "MainFragment"

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var songAdapter: SongAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(MainViewModel::class.java)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.main_fragment, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Creates a vertical Layout Manager
        rv_scan_list.layoutManager = LinearLayoutManager(activity)

        // Access the RecyclerView Adapter and load the data into it
        songAdapter = SongAdapter(viewModel.scannedSongList.value!!, this.context!!)
        rv_scan_list.adapter = songAdapter;


        viewModel.searchedSongList.observe(this, Observer { data ->
            Log.d(TAG,"observed searchedSongList change" + data!!.size);
            if (songAdapter!=null) {
                Log.d(TAG,"updating songadapter list..")
                songAdapter.updateList(data);
            }
        });

        et_searchfiles.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG,"afterTextChanged: "+ s);

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                Log.d(TAG,"beforeTextChanged: "+ s);

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d(TAG,"onTextChanged: "+ s);
                var tempSearchList = ArrayList<AudioFileModel>();
                for (song in viewModel.scannedSongList.value!!){
                    if (song.toSearchableString().contains(s.toString().toLowerCase())){
                        tempSearchList.add(song);
                    }
                }
                if (count==0){      //if there is nothing in the input field. Revert to showing entire list.
                    tempSearchList = viewModel.scannedSongList.value!!;
                }

                viewModel.updateSearchedSongList(tempSearchList);
            }

        })


    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        if (StoragePermissionHandler.handlePermissions(activity))
            (activity as MainActivity).startScan(activity as MainActivity);

    }



//    override fun onClick(view: View){
//        when (view?.id) {
//            R.id.btn_scan -> {
//                Log.i(TAG,"onScanClicked!");
//                if (StoragePermissionHandler.handlePermissions(activity))
//                    (activity as MainActivity).startScan(activity as MainActivity);
//            }
//            else -> {
//            }
//        }
//    }

}
