package com.newpath.puremuse.adapters

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.LiveData
import android.util.Log
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.newpath.puremuse.R
import com.newpath.puremuse.helpers.AudioFileScanner
import com.newpath.puremuse.interfaces.OnItemClickListener
import com.newpath.puremuse.models.AudioFileModel
import kotlinx.android.synthetic.main.item_list_song.view.*
import com.newpath.puremuse.interfaces.OnOptionsClickListener
import com.bumptech.glide.load.resource.bitmap.TransformationUtils.centerCrop
import com.bumptech.glide.request.RequestOptions



class SongAdapter(val listener: OnItemClickListener, val optionsClickListener: OnOptionsClickListener, val items : ArrayList<AudioFileModel>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {


    private val TAG:String = "SongAdapter";


    fun updateList(list: ArrayList<AudioFileModel>){
        if (items==null)
            return;
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
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_list_song, parent, false)
        val holder = ViewHolder(v)

        holder.itemView.setOnClickListener {
            Log.d(TAG, "position clicked in listener = " + holder.adapterPosition)
            listener.onItemClicked(holder.adapterPosition);
        }


        return holder
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (items==null)
            return
        holder?.tvTitle?.text = items.get(position).displayName
        holder?.tvAlbum?.text = items.get(position).album
        holder?.tvArtist?.text = items.get(position).artist;
        holder?.btnOptions?.setOnClickListener {
            optionsClickListener.onOptionsClicked(holder.adapterPosition);
        }

        var imagePath: String? = null
        try {
            imagePath = AudioFileScanner.getAlbumImage(items.get(position).albumId)
        } catch (e: Exception) {
            e.printStackTrace()
        }


        val options = RequestOptions()
        options.centerCrop()

        if (imagePath == null) {
            Glide.with(holder.itemView.context).load(R.drawable.ic_album_24dp)
                    .thumbnail(0.5f)
                    .apply(options)
                    .into(holder.imgThumb)
            return
        }


        // Loading profile image
        Glide.with(holder.itemView.context).load(imagePath)
                .thumbnail(0.5f)
                .apply(options)
                .into(holder.imgThumb)
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val tvTitle = view.tv_title
    var tvAlbum = view.tv_album
    var tvArtist = view.tv_artist
    var btnOptions: ImageButton = view.btn_options
    var imgThumb: ImageView = view.img_thumb

}