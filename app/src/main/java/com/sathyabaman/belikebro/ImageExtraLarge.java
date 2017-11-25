package com.sathyabaman.belikebro;

import android.content.Context;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

public class ImageExtraLarge extends AppCompatActivity {

    private Context context;

    private String location;
    private String date_time;
    private String views;
    private int id;

    private ImageView zoom_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_extra_large);
        context = getApplicationContext();
        getSupportActionBar().hide();

        zoom_image = (ImageView) findViewById(R.id.IV_zoomable);
        id = Integer.parseInt(getIntent().getStringExtra("ID"));
        location = getIntent().getStringExtra("LOCATION");
        date_time = getIntent().getStringExtra("DATE_TIME");
        views = getIntent().getStringExtra("VIEWS");

        Picasso.with(context)
                .load(location)
                .into(zoom_image);

        PhotoViewAttacher pAttacher;
        pAttacher = new PhotoViewAttacher(zoom_image);
        pAttacher.update();

    }


    public void go_back_to_imageDetails(View V){
        finish();
        onBackPressed();
    }
}
