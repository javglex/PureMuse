package com.newpath.puremuse.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.newpath.puremuse.R;
import com.newpath.puremuse.interfaces.OnItemClickListener;
import com.newpath.puremuse.interfaces.OnOptionsClickListener;
import com.newpath.puremuse.models.CollectionModel;

import java.util.ArrayList;
import java.util.List;


/**
 * For adding a song to a collection, playlist.
 */
public class AddToCollectionAdapter extends RecyclerView.Adapter<AddToCollectionAdapter.MyViewHolder>  {
    public static final String TAG = "CollectionsAdapter";
    private List<CollectionModel> list;
    OnItemClickListener mOnItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tvCollectionName, tvInfo;


        public MyViewHolder(View view) {
            super(view);
            tvCollectionName = view.findViewById(R.id.tv_collection_name);
            tvInfo = view.findViewById(R.id.tv_info);
        }
    }


    public AddToCollectionAdapter(List<CollectionModel> list, OnItemClickListener listener) {
        this.list = list;
        if (list==null)
            this.list = new ArrayList<>();
        this.mOnItemClickListener = listener;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_list_add_collection, parent, false);

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
    public void onBindViewHolder(@NonNull AddToCollectionAdapter.MyViewHolder holder, int position) {
        if (list.size()==0)
            return;
        CollectionModel collection = list.get(position);

        holder.tvCollectionName.setText(collection.getCollectionName());
        holder.tvInfo.setText("By Javier Gonzalez");

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
