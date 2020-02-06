package com.newpath.puremuse.adapters;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.newpath.puremuse.R;
import com.newpath.puremuse.helpers.AudioFileScanner;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.interfaces.OnOptionsClickListener;
import com.newpath.puremuse.models.CollectionModel;

import java.util.ArrayList;
import java.util.List;

public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.MyViewHolder> {

    public static final String TAG = "CollectionsAdapter";
    private List<CollectionModel> list;

    OnItemClickListener mOnItemClickListener;
    OnOptionsClickListener mOnOptionsClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCollectionName, tvArtist;
        public ImageView imgAlbumCover;
        public ImageButton btnOptions;

        public MyViewHolder(View view) {
            super(view);
            tvCollectionName = view.findViewById(R.id.tv_album_title);
            tvArtist = view.findViewById(R.id.tv_album_artist);
            imgAlbumCover = view.findViewById(R.id.img_album_art);
            btnOptions = view.findViewById(R.id.btn_options);
        }
    }

    public CollectionsAdapter(List<CollectionModel> list, OnItemClickListener listener, OnOptionsClickListener optionsClickListener) {
        this.list = list;
        if (list==null)
            this.list = new ArrayList<>();
        this.mOnItemClickListener = listener;
        this.mOnOptionsClickListener = optionsClickListener;
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

//        holder.btnOptions.setOnClickListener((new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mOnOptionsClickListener.onOptionsClicked(holder.getAdapterPosition());
//            }
//        }));


        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        if (list.size()==0)
            return;
        CollectionModel collection = list.get(position);

        holder.tvArtist.setText(""+collection.getCollectionArtist());
        holder.tvCollectionName.setText(collection.getCollectionName());

        String imagePath=null;
        try {
            if (collection.getSongList().size()>0 && collection.getSongList().get(0)!=null)
                imagePath = AudioFileScanner.getAlbumImage(collection.getSongList().get(0).getAlbumId());
        } catch (Exception e) {
            e.printStackTrace();
        }

        RequestOptions options = new RequestOptions();
        options.centerCrop();

        if (imagePath==null) {
            Glide.with(holder.itemView.getContext()).load(R.drawable.ic_album_24dp)
                    .thumbnail(0.5f)
                    .apply(options)
                    .into(holder.imgAlbumCover);
            return;
        }


        // Loading album image
        Glide.with(holder.itemView.getContext()).load(imagePath)
                .thumbnail(0.5f)
                .apply(options)
                .into(holder.imgAlbumCover);

    }

    @Override
    public int getItemCount() {
        if (list==null)
            throw new NullPointerException("List is not initialized");
        return list.size();
    }



    public void updateList(ArrayList<CollectionModel> list){

        this.list.clear() ;      //clear list
        for (CollectionModel item : list){
            this.list.add(item);        //repopulate list
        }
        notifyDataSetChanged();
    }


}
