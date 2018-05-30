package com.newpath.puremuse.adapters;

import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.newpath.puremuse.R;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.models.AlbumModel;
import com.newpath.puremuse.models.AudioFileModel;

import java.util.ArrayList;
import java.util.List;

public class AlbumsAdapter extends RecyclerView.Adapter<AlbumsAdapter.MyViewHolder> {

    public static final String TAG = "AlbumsAdapter";
    private List<AlbumModel> list;

    OnItemClickListener mOnItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvAlbumName, tvSongCount;
        public ImageView imgAlbumCover;

        public MyViewHolder(View view) {
            super(view);
            tvAlbumName = view.findViewById(R.id.tv_album_title);
            tvSongCount = view.findViewById(R.id.tv_song_count);
            imgAlbumCover = view.findViewById(R.id.img_album_art);
        }
    }

    public AlbumsAdapter(List<AlbumModel> list, OnItemClickListener listener) {
        this.list = list;
        if (list==null)
            this.list = new ArrayList<>();
        this.mOnItemClickListener = listener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_album, parent, false);

        final MyViewHolder holder = new MyViewHolder(itemView);

        holder.itemView.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view){
                mOnItemClickListener.onItemClicked(holder.getAdapterPosition());
            }
        }));


        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (list.size()==0)
            return;
        AlbumModel album = list.get(position);

        holder.tvSongCount.setText(""+album.getSongList().size());
        holder.tvAlbumName.setText(album.getAlbumName());

//        holder.tvHoursUntil.setText(userSlot.getHoursUntilAlarm() + " hours until alarm");
//        holder.tvLocation.setText(userSlot.getlCoordinates().toString());
//        holder.tvName.setText(userSlot.getUsername());
//        // Loading profile image
//        Glide.with(holder.itemView.getContext()).load(userSlot.getProfilePic())
//                .thumbnail(0.5f)
//                .bitmapTransform(new CropCircleTransformation(holder.itemView.getContext())) //https://github.com/wasabeef/glide-transformations
//                .diskCacheStrategy(DiskCacheStrategy.ALL)
//                .into(holder.imgProfilePic);

    }

    @Override
    public int getItemCount() {
        if (list==null)
            throw new NullPointerException("List is not initialized");
        return list.size();
    }



    public void updateList(ArrayList<AlbumModel> list){

        this.list.clear() ;      //clear list
        for (AlbumModel item : list){
            this.list.add(item);        //repopulate list
        }
        notifyDataSetChanged();
    }


}
