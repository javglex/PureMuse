package com.newpath.puremuse.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.arch.lifecycle.LiveData
import android.view.ViewGroup
import com.newpath.puremuse.R
import com.newpath.puremuse.models.AudioFileModel
import kotlinx.android.synthetic.main.item_list_song.view.*

class SongAdapter(val items : ArrayList<AudioFileModel>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    fun updateList(list: ArrayList<AudioFileModel>){

        items.clear()       //clear list
        for (item in list){
            items.add(item);        //repopulate list
        }
        notifyDataSetChanged();
    }

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        if (items==null)
            return 0
        else
            return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_list_song, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items==null)
            return
        holder?.tvTitle?.text = items.get(position).displayName
        holder?.tvAlbum?.text = items.get(position).album
        holder?.tvArtist?.text = items.get(position).artist
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvTitle = view.tv_title
    var tvAlbum = view.tv_album
    var tvArtist = view.tv_artist
}