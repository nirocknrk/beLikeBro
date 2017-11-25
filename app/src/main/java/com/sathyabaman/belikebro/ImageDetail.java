package com.sathyabaman.belikebro;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.sathyabaman.belikebro.Comman.RequestExternalResouce;
import com.sathyabaman.belikebro.Comman.Utility;
import com.sathyabaman.belikebro.DataObjects.AllImageObject;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class ImageDetail extends AppCompatActivity {

    ImageView main_image;
    Context context;
    private AdView imageDetail_ad;

    private String location;
    private String date_time;
    private String views;
    private String hearts;
    private String downloads;
    private int id;
    private Boolean isImageLiked = false;
    ImageButton button_heart;
    TextView tv_count_view;
    TextView tv_count_downloads;


    private int TotalImageCount;
    private int LastImageId;
    private int FirstImageId;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_detail);
//        getSupportActionBar().hide();
        context = getApplicationContext();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button_heart = (ImageButton)findViewById(R.id.likeButton);
        tv_count_view = (TextView) findViewById(R.id.viewsCount_textView);
        tv_count_downloads = (TextView) findViewById(R.id.downloadCount_textView);

        MobileAds.initialize(this, "ca-app-pub-7067806065281199~9864037146");
        main_image = (ImageView) findViewById(R.id.IV_main_Image);

        imageDetail_ad = (AdView) findViewById(R.id.ad_image_detail);
        AdRequest adRequest = new AdRequest.Builder().build();
        imageDetail_ad.loadAd(adRequest);


        imageDetail_ad.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() { }

            @Override
            public void onAdFailedToLoad(int errorCode) { }

            @Override
            public void onAdOpened() { }

            @Override
            public void onAdLeftApplication() { }

            @Override
            public void onAdClosed() { }
        });

        id = Integer.parseInt(getIntent().getStringExtra("ID"));
        location = getIntent().getStringExtra("LOCATION");
        date_time = getIntent().getStringExtra("DATE_TIME");
        views = getIntent().getStringExtra("VIEWS");
        downloads = getIntent().getStringExtra("DOWNLOADS");
        hearts = getIntent().getStringExtra("HEART");

        tv_count_downloads.setText(downloads + " Downloads");
        tv_count_view.setText(views + " Views");

        SharedPreferences sp = getSharedPreferences("Main_preferences", Activity.MODE_PRIVATE);
        TotalImageCount = sp.getInt("total_count", 0);
        FirstImageId = sp.getInt("first_Image_id", 0);
        LastImageId = sp.getInt("last_Image_id", 0);

        updateCounts(id, "AddView");

        Picasso.with(context)
                .load(location)
                .into(main_image);
    }



    public void zoom_Image(View V){
        Intent intent = new Intent(getBaseContext(), ImageExtraLarge.class);
        intent.putExtra("ID",  String.valueOf(id));
        intent.putExtra("LOCATION", location);
        intent.putExtra("DATE_TIME", date_time);
        intent.putExtra("VIEWS", views);
        intent.putExtra("DOWNLOADS", downloads);
        intent.putExtra("HEART", hearts);
        startActivity(intent);
    }

    public void go_back(View V){
        finish();
        onBackPressed();
    }

    public void image_liked(View V){
        isImageLiked = !isImageLiked;

        if (isImageLiked) {
            updateCounts(id, "AddHeart");
            button_heart.setImageResource(R.drawable.red_like);
        } else {
            button_heart.setImageResource(R.drawable.white_like);
        }
    }

    public void shareImage(){
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Be Like Bro");
        share.putExtra(Intent.EXTRA_TEXT, location);
        startActivity(Intent.createChooser(share, "Share Bro's Creations"));
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("Permission log : ","Permission is granted");
                downloadTointernalStorage(location);
                return true;
            } else {
                Log.v("Permission log : ","Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("Permission log : ","Permission is granted");
            downloadTointernalStorage(location);
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults[0]== PackageManager.PERMISSION_GRANTED){
            Log.v("Permission : ","Permission: "+permissions[0]+ "was "+grantResults[0]);
            //resume tasks needing this permission
        }
    }


    public void downloadTointernalStorage(String uRl){
        updateCounts(id, "AddDownload");

        Toast.makeText(getBaseContext(), "Downloading....",Toast.LENGTH_SHORT).show();
        String[] bits = uRl.split("/");
        String imageName = bits[bits.length-1];

        File direct = new File(Environment.getExternalStorageDirectory()
                + "/BeLikeBro");

        if (!direct.exists()) {
            direct.mkdirs();
        }

        DownloadManager mgr = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);

        Uri downloadUri = Uri.parse(uRl);
        DownloadManager.Request request = new DownloadManager.Request(
                downloadUri);

        request.setAllowedNetworkTypes(
                DownloadManager.Request.NETWORK_WIFI
                        | DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false).setTitle("Be like Bro")
                .setDescription("Bro is sending you his memes.")
                .setDestinationInExternalPublicDir("/BeLikeBro",  imageName);

        mgr.enqueue(request);
    }


    public void updateCounts(int id, String Type){

        //AddView
        //AddDownload
        //AddHeart
        //SubtractHeart

        if(new Utility().isNetworkAvailable(context)){
            try {

                JSONObject requestBody = new JSONObject();
                requestBody.put("image_id", id);
                requestBody.put("request_type", Type);

                new RequestExternalResouce(context, new Utility().getUpdateImageCountsURL(), requestBody.toString(), "POST", new RequestExternalResouce.OnTaskDoneListener() {
                    @Override
                    public void onTaskDone(String responseData) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(responseData);

                            System.out.println("Success");

                        } catch (JSONException e) {  e.printStackTrace(); }
                    }

                    @Override
                    public void onError() {
                        System.out.println("failed");
                    }
                }).execute();
            }catch (Exception e){ e.printStackTrace();}
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;

            case R.id.action_download:
                isStoragePermissionGranted();
                return true;

            case R.id.action_share:
                shareImage();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void goToNextImage(View v){

            if(new Utility().isNetworkAvailable(context)){
                try {

                    JSONObject requestBody = new JSONObject();
                    requestBody.put("start_image_id", FirstImageId);
                    requestBody.put("end_image_id", LastImageId);
                    requestBody.put("current_image", id);
                    requestBody.put("request_type", "next");


                    new RequestExternalResouce(context, new Utility().getNextPreviousURL(), requestBody.toString(), "POST", new RequestExternalResouce.OnTaskDoneListener() {
                        @Override
                        public void onTaskDone(String responseData) {
                            JSONObject jsonObject = null;
                            try {
                                jsonObject = new JSONObject(responseData);
                                JSONArray allimages = jsonObject.getJSONArray("image_list");
                                AllImageObject imageList[] = new Gson().fromJson(String.valueOf(allimages), AllImageObject[].class);

                                id  = imageList[0].id;
                                location = imageList[0].location;
                                date_time = imageList[0].DateTime;
                                views = String.valueOf(imageList[0].no_of_views);
                                downloads = String.valueOf(imageList[0].no_of_downloads);
                                hearts = String.valueOf(imageList[0].no_of_heart);

                                tv_count_downloads.setText(downloads + " Downloads");
                                tv_count_view.setText(views + " Views");

                                updateCounts(id, "AddView");

                                Picasso.with(context)
                                        .load(location)
                                        .into(main_image);

                                System.out.println("Success");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError() {
                            System.out.println("failed");
                        }
                    }).execute();
                }catch (Exception e){ e.printStackTrace();}
            }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_image_detail, menu);
        return true;
    }

    public void goToPreviousImage(View v){
        if(new Utility().isNetworkAvailable(context)){
            try {

                JSONObject requestBody = new JSONObject();
                requestBody.put("start_image_id", FirstImageId);
                requestBody.put("end_image_id", LastImageId);
                requestBody.put("current_image", id);
                requestBody.put("request_type", "previous");


                new RequestExternalResouce(context, new Utility().getNextPreviousURL(), requestBody.toString(), "POST", new RequestExternalResouce.OnTaskDoneListener() {
                    @Override
                    public void onTaskDone(String responseData) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(responseData);
                            JSONArray allimages = jsonObject.getJSONArray("image_list");
                            AllImageObject imageList[] = new Gson().fromJson(String.valueOf(allimages), AllImageObject[].class);

                            id  = imageList[0].id;
                            location = imageList[0].location;
                            date_time = imageList[0].DateTime;
                            views = String.valueOf(imageList[0].no_of_views);
                            downloads = String.valueOf(imageList[0].no_of_downloads);
                            hearts = String.valueOf(imageList[0].no_of_heart);

                            tv_count_downloads.setText(downloads + " Downloads");
                            tv_count_view.setText(views + " Views");

                            updateCounts(id, "AddView");

                            Picasso.with(context)
                                    .load(location)
                                    .into(main_image);

                            System.out.println("Success");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError() {
                        System.out.println("failed");
                    }
                }).execute();
            }catch (Exception e){ e.printStackTrace();}
        }
    }
}
