package com.sathyabaman.belikebro.Adapters;

/**
 * Created by viewqwest on 20/11/2017.
 */


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.sathyabaman.belikebro.DataObjects.AllImageObject;
import com.sathyabaman.belikebro.ImageList;
import com.sathyabaman.belikebro.R;
import com.squareup.picasso.Picasso;


public class ImageListAdapter extends ArrayAdapter<AllImageObject> {

    private static final String TAG = "ImageListAdapter";

    private final LayoutInflater mLayoutInflater;
    private final Random mRandom;
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private List<AllImageObject> AllImageList = Collections.emptyList();
    private Context context;

    public ImageListAdapter(Context context, int textViewResourceId, ArrayList<AllImageObject> objects) {
        super(context, textViewResourceId, objects);
        this.AllImageList = objects;
        this.context = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mRandom = new Random();
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (position == AllImageList.size() -2){
            Intent intent = new Intent("ImageList");
            intent.putExtra("Message", "END");
            intent.putExtra("Type", "Reload_images");
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }

        ViewHolder vh;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.custom_image_list_frame, parent, false);
            vh = new ViewHolder();
            vh.imgView = (DynamicHeightImageView) convertView .findViewById(R.id.imgView);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        double positionHeight = getPositionRatio(position);
        vh.imgView.setHeightRatio(positionHeight);

        Picasso.with(context)
                .load(AllImageList.get(position).location)
                .into(vh.imgView);

        return convertView;
    }


    static class ViewHolder {
        DynamicHeightImageView imgView;
    }


    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);

        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
         //   Log.d(TAG, "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5
        // the width
    }
}