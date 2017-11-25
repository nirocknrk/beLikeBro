package com.sathyabaman.belikebro.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sathyabaman.belikebro.DataObjects.AllImageObject;
import com.sathyabaman.belikebro.R;
import com.squareup.picasso.Picasso;

import java.util.Collections;
import java.util.List;


/**
 * Created by viewqwest on 11/11/2017.
 */


public class RandomItemsAdapter extends RecyclerView.Adapter<RandomItemsAdapter.ViewHolder> {

    private List<AllImageObject> AllImageList = Collections.emptyList();
    private LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private Context context;

    // data is passed into the constructor
    public RandomItemsAdapter(Context context, List<AllImageObject> imageList) {
        this.mInflater = LayoutInflater.from(context);
        this.AllImageList = imageList;
        this.context = context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.custom_random_single_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // binds the data to the view and textview in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Picasso.with(context)
                .load(AllImageList.get(position).location)
                .into(holder.myView);

        if(position==(getItemCount()-1)) {
            Intent intent = new Intent("MainPage");
            intent.putExtra("Message", "END");
            intent.putExtra("Type", "random_order");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return AllImageList.size();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView myView;

        public ViewHolder(View itemView) {
            super(itemView);
            myView = (ImageView) itemView.findViewById(R.id.IV_random_items);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition(), "random_order");
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return String.valueOf(AllImageList.get(id).id);
    }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position, String type);
    }
}