package com.sathyabaman.belikebro;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.sathyabaman.belikebro.Adapters.MostLovedAdapter;
import com.sathyabaman.belikebro.Adapters.NewItemsAdapter;
import com.sathyabaman.belikebro.Adapters.RandomItemsAdapter;
import com.sathyabaman.belikebro.Adapters.RecentItemsAdapter;
import com.sathyabaman.belikebro.Comman.RequestExternalResouce;
import com.sathyabaman.belikebro.Comman.Utility;
import com.sathyabaman.belikebro.DataObjects.AllImageObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.TimeZone;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity implements NewItemsAdapter.ItemClickListener, MostLovedAdapter.ItemClickListener, RandomItemsAdapter.ItemClickListener, RecentItemsAdapter.ItemClickListener {

    private NewItemsAdapter NAdapter;
    private RecentItemsAdapter RAdapter;
    private MostLovedAdapter MLAdapter;
    private RandomItemsAdapter RMAdapter;
    private Context context;
    private ArrayList<AllImageObject> NewImageListASC = new ArrayList<>();
    private ArrayList<AllImageObject> NewImageListDESC = new ArrayList<>();
    private ArrayList<AllImageObject> ImageListRAND = new ArrayList<>();
    private ArrayList<AllImageObject> ImageListMOstLoved = new ArrayList<>();
    private AdView mainpage_ad;

    private  int TotalImageCount;
    private  int LastImageId;
    private  int FirstImageId;

    private int PerPageCount = 5;

    // new asc Image List
    private int ASC_offset;
    private int DESC_offset;
    private int LOVE_offset;
    private int RAND_offset;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getSupportActionBar().hide();
        context = getApplicationContext();
        LocalBroadcastManager.getInstance(context).registerReceiver(IncomingBroadcastReceiver, new IntentFilter("MainPage"));

        showAD();

        SharedPreferences sp = getSharedPreferences("Main_preferences", Activity.MODE_PRIVATE);
        TotalImageCount = sp.getInt("total_count", 0);
        LastImageId = sp.getInt("first_Image_id", 0);
        FirstImageId = sp.getInt("last_Image_id", 0);
        ASC_offset = sp.getInt("ASC_offset", 0);
        DESC_offset = sp.getInt("DESC_offset", 0);
        LOVE_offset = sp.getInt("LOVE_offset", 0);
        RAND_offset = sp.getInt("RAND_offset", 0);


        // set up the RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.RV_new_items);
        LinearLayoutManager horizontalLayoutManagaer = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(horizontalLayoutManagaer);
        NAdapter = new NewItemsAdapter(this, NewImageListASC);
        NAdapter.setClickListener(this);
        recyclerView.setAdapter(NAdapter);


        // set up the RecyclerView
        RecyclerView RV_recent_images = (RecyclerView) findViewById(R.id.RV_recent_items);
        LinearLayoutManager HLM2 = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        RV_recent_images.setLayoutManager(HLM2);
        RAdapter = new RecentItemsAdapter(this, NewImageListDESC);
        RAdapter.setClickListener(this);
        RV_recent_images.setAdapter(RAdapter);


        // set up the RecyclerView
        RecyclerView RV_ML_images = (RecyclerView) findViewById(R.id.RV_most_loved);
        LinearLayoutManager HLM3 = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        RV_ML_images.setLayoutManager(HLM3);
        MLAdapter = new MostLovedAdapter(this, ImageListMOstLoved);
        MLAdapter.setClickListener(this);
        RV_ML_images.setAdapter(MLAdapter);


        // set up the RecyclerView
        RecyclerView RV_Random_images = (RecyclerView) findViewById(R.id.RV_random_pick);
        LinearLayoutManager HLM4 = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        RV_Random_images.setLayoutManager(HLM4);
        RMAdapter = new RandomItemsAdapter(this, ImageListRAND);
        RMAdapter.setClickListener(this);
        RV_Random_images.setAdapter(RMAdapter);

        getAllImages(ASC_offset, "ascending_order");
        getAllImages(DESC_offset, "descending_order");
        getAllImages(LOVE_offset, "most_loved");
        getAllImages(RAND_offset, "random_order");

    }

    @Override
    public void onItemClick(View view, int position, String Type) {
        Intent intent = new Intent(getBaseContext(), ImageDetail.class);
        switch (Type){
            case "ascending_order":
                    intent.putExtra("ID",  String.valueOf(NewImageListASC.get(position).id));
                    intent.putExtra("LOCATION", NewImageListASC.get(position).location);
                    intent.putExtra("DATE_TIME", NewImageListASC.get(position).DateTime);
                    intent.putExtra("VIEWS", String.valueOf(NewImageListASC.get(position).no_of_views));
                    intent.putExtra("HEART", String.valueOf(NewImageListASC.get(position).no_of_heart));
                    intent.putExtra("DOWNLOADS", String.valueOf(NewImageListASC.get(position).no_of_downloads));
                break;
            case "descending_order":
                    intent.putExtra("ID",  String.valueOf(NewImageListDESC.get(position).id));
                    intent.putExtra("LOCATION", NewImageListDESC.get(position).location);
                    intent.putExtra("DATE_TIME", NewImageListDESC.get(position).DateTime);
                    intent.putExtra("VIEWS", String.valueOf(NewImageListDESC.get(position).no_of_views));
                    intent.putExtra("HEART", String.valueOf(NewImageListDESC.get(position).no_of_heart));
                    intent.putExtra("DOWNLOADS", String.valueOf(NewImageListDESC.get(position).no_of_downloads));
                break;
            case "most_loved":
                    intent.putExtra("ID",  String.valueOf(ImageListMOstLoved.get(position).id));
                    intent.putExtra("LOCATION", ImageListMOstLoved.get(position).location);
                    intent.putExtra("DATE_TIME", ImageListMOstLoved.get(position).DateTime);
                    intent.putExtra("VIEWS", String.valueOf(ImageListMOstLoved.get(position).no_of_views));
                    intent.putExtra("HEART", String.valueOf(ImageListMOstLoved.get(position).no_of_heart));
                    intent.putExtra("DOWNLOADS", String.valueOf(ImageListMOstLoved.get(position).no_of_downloads));
                break;
            case "random_order":
                    intent.putExtra("ID",  String.valueOf(ImageListRAND.get(position).id));
                    intent.putExtra("LOCATION", ImageListRAND.get(position).location);
                    intent.putExtra("DATE_TIME", ImageListRAND.get(position).DateTime);
                    intent.putExtra("VIEWS", String.valueOf(ImageListRAND.get(position).no_of_views));
                    intent.putExtra("HEART", String.valueOf(ImageListRAND.get(position).no_of_heart));
                    intent.putExtra("DOWNLOADS", String.valueOf(ImageListRAND.get(position).no_of_downloads));
                break;
            default:
                break;
        }
        startActivity(intent);

    }


    private final BroadcastReceiver IncomingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String Action = intent.getStringExtra("Type");
            switch (Action){
                case "ascending_order":
                    getAllImages(ASC_offset, "ascending_order");
                    break;
                case "descending_order":
                    getAllImages(DESC_offset, "descending_order");
                    break;
                case "most_loved":
                    getAllImages(LOVE_offset, "most_loved");
                    break;
                case "random_order":
                    getAllImages(RAND_offset, "random_order");
                    break;
                default:
                    break;
            }
        }
    };


    public void OpenImageList(View v){
        Intent intent = new Intent(MainActivity.this, ImageList.class);
        intent.putExtra("Type_request", "ascending_order");
        startActivity(intent);
    }
    public void OpenDecendingImageList (View v){
        Intent intent = new Intent(MainActivity.this, ImageList.class);
        intent.putExtra("Type_request", "descending_order");
        startActivity(intent);
    }
    public void OpenRandomImageList(View v){
        Intent intent = new Intent(MainActivity.this, ImageList.class);
        intent.putExtra("Type_request", "most_loved");
        startActivity(intent);
    }
    public void OpenLovedImageList(View v){
        Intent intent = new Intent(MainActivity.this, ImageList.class);
        intent.putExtra("Type_request", "random_order");
        startActivity(intent);
    }


    public void  getAllImages(int offset, final String request_type){
        if(new Utility().isNetworkAvailable(context)){
            try {

                JSONObject requestBody = new JSONObject();
                requestBody.put("limit", PerPageCount);
                requestBody.put("offset", offset);
                requestBody.put("request_type", request_type);


                new RequestExternalResouce(context, new Utility().getImageListURL(), requestBody.toString(), "POST", new RequestExternalResouce.OnTaskDoneListener() {
                    @Override
                    public void onTaskDone(String responseData) {
                        processAllImages(responseData, request_type);
                        System.out.println("Success");
                    }

                    @Override
                    public void onError() {
                        System.out.println("failed");
                    }
                }).execute();
            }catch (Exception e){ e.printStackTrace();}
        }
    }

    private void processAllImages(String response, String request_type){
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray allimages = jsonObject.getJSONArray("image_list");
            AllImageObject imageList[] = new Gson().fromJson(String.valueOf(allimages), AllImageObject[].class);
            SharedPreferences sp = getSharedPreferences("Main_preferences", Activity.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            switch (request_type){
                case "ascending_order":

                        for (int y=0; y< imageList.length; y++){ NewImageListASC.add(imageList[y]); }
                        NAdapter.notifyDataSetChanged();

                        if ((ASC_offset + PerPageCount) >= TotalImageCount){ ASC_offset = 0;
                        } else { ASC_offset = ASC_offset + PerPageCount; }

                        // update offset to preferences
                        editor.putInt("ASC_offset", ASC_offset);

                    break;
                case "descending_order":

                        for (int y=0; y< imageList.length; y++){ NewImageListDESC.add(imageList[y]); }
                        RAdapter.notifyDataSetChanged();

                        if ((DESC_offset + PerPageCount) >= TotalImageCount){ DESC_offset = 0;
                        } else { DESC_offset = DESC_offset + PerPageCount; }

                        // update offset to preferences
                        editor.putInt("DESC_offset", DESC_offset);

                    break;

                case "most_loved":

                        for (int y=0; y< imageList.length; y++){ ImageListMOstLoved.add(imageList[y]); }
                        MLAdapter.notifyDataSetChanged();

                        if ((LOVE_offset + PerPageCount) >= TotalImageCount){ LOVE_offset = 0;
                        } else { LOVE_offset = LOVE_offset + PerPageCount; }

                        // update offset to preferences
                        editor.putInt("LOVE_offset", LOVE_offset);

                    break;

                case "random_order":

                        for (int y=0; y< imageList.length; y++){ ImageListRAND.add(imageList[y]); }
                        RMAdapter.notifyDataSetChanged();

                        if ((RAND_offset + PerPageCount) >= TotalImageCount){ RAND_offset = 0;
                        } else { RAND_offset = RAND_offset + PerPageCount; }

                        // update offset to preferences
                        editor.putInt("RAND_offset", RAND_offset);

                    break;

                default:
                    break;
            }

            editor.commit();

        } catch (JSONException e) {  e.printStackTrace(); }
    }



    private void showAD(){
//        MobileAds.initialize(this, "ca-app-pub-7067806065281199~9864037146");
//
//        mainpage_ad = (AdView) findViewById(R.id.ad_mainpage);
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mainpage_ad.loadAd(adRequest);
//
//        mainpage_ad.setAdListener(new AdListener() {
//            @Override
//            public void onAdLoaded() { }
//
//            @Override
//            public void onAdFailedToLoad(int errorCode) { }
//
//            @Override
//            public void onAdOpened() { }
//
//            @Override
//            public void onAdLeftApplication() { }
//
//            @Override
//            public void onAdClosed() { }
//        });
    }


}