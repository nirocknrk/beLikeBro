package com.sathyabaman.belikebro;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import java.util.ArrayList;
import android.app.Activity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.Toast;

        import com.etsy.android.grid.StaggeredGridView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.gson.Gson;
import com.sathyabaman.belikebro.Adapters.ImageListAdapter;
import com.sathyabaman.belikebro.Comman.RequestExternalResouce;
import com.sathyabaman.belikebro.Comman.Utility;
import com.sathyabaman.belikebro.DataObjects.AllImageObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.google.android.gms.ads.MobileAds;

public class ImageList extends AppCompatActivity implements AbsListView.OnItemClickListener {

    private static final String TAG = "StaggeredGridActivity";
    public static final String SAVED_DATA_KEY = "SAVED_DATA";

    private StaggeredGridView mGridView;
    private boolean mHasRequestedMore;
    private ImageListAdapter mAdapter;
    public Context context;
    private ArrayList<String> mData;
    private ArrayList<AllImageObject> AllImageList = new ArrayList<>();
    private AdView imagelist_ad;
    private AdView imagelist_ad_top;
    private ImageButton back;

    private  int TotalImageCount;
    private  int LastImageId;
    private  int FirstImageId;

    private int PerPageCount = 5;

    // new asc Image List
    private int ASC_offset;
    private int DESC_offset;
    private int LOVE_offset;
    private int RAND_offset;

    private String requestType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        context = getApplicationContext();
        LocalBroadcastManager.getInstance(context).registerReceiver(IncomingBroadcastReceiver, new IntentFilter("ImageList"));
        showAd();

        SharedPreferences sp = getSharedPreferences("Main_preferences", Activity.MODE_PRIVATE);
        TotalImageCount = sp.getInt("total_count", 0);
        LastImageId = sp.getInt("first_Image_id", 0);
        FirstImageId = sp.getInt("last_Image_id", 0);
        ASC_offset = sp.getInt("ASC_offset", 0);
        DESC_offset = sp.getInt("DESC_offset", 0);
        LOVE_offset = sp.getInt("LOVE_offset", 0);
        RAND_offset = sp.getInt("RAND_offset", 0);

        requestType = getIntent().getStringExtra("Type_request");

        switch (requestType){
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



        setTitle("Image List");
        mGridView = (StaggeredGridView) findViewById(R.id.grid_view);
        mAdapter = new ImageListAdapter(this, android.R.layout.simple_list_item_1, AllImageList);
        // do we have saved data?
        if (savedInstanceState != null) { mData = savedInstanceState.getStringArrayList(SAVED_DATA_KEY);  }

        mGridView.setAdapter(mAdapter);
        mGridView.setOnItemClickListener(this);
    }


    private final BroadcastReceiver IncomingBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String Action = intent.getStringExtra("Type");

            switch (requestType){
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
            for (int y=0; y< imageList.length; y++){ AllImageList.add(imageList[y]); }
            mAdapter.notifyDataSetChanged();


            switch (request_type){
                case "ascending_order":

                    if ((ASC_offset + PerPageCount) >= TotalImageCount){ ASC_offset = 0;
                    } else { ASC_offset = ASC_offset + PerPageCount; }

                    // update offset to preferences
                    editor.putInt("ASC_offset", ASC_offset);

                    break;
                case "descending_order":

                    if ((DESC_offset + PerPageCount) >= TotalImageCount){ DESC_offset = 0;
                    } else { DESC_offset = DESC_offset + PerPageCount; }

                    // update offset to preferences
                    editor.putInt("DESC_offset", DESC_offset);

                    break;

                case "most_loved":

                    if ((LOVE_offset + PerPageCount) >= TotalImageCount){ LOVE_offset = 0;
                    } else { LOVE_offset = LOVE_offset + PerPageCount; }

                    // update offset to preferences
                    editor.putInt("LOVE_offset", LOVE_offset);

                    break;

                case "random_order":

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


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        Toast.makeText(this, "Item Clicked: " + position, Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getBaseContext(), ImageDetail.class);
        intent.putExtra("ID",  String.valueOf(AllImageList.get(position).id));
        intent.putExtra("LOCATION", AllImageList.get(position).location);
        intent.putExtra("DATE_TIME", AllImageList.get(position).DateTime);
        intent.putExtra("VIEWS", String.valueOf(AllImageList.get(position).no_of_views));
        intent.putExtra("HEART", String.valueOf(AllImageList.get(position).no_of_heart));
        intent.putExtra("DOWNLOADS", String.valueOf(AllImageList.get(position).no_of_downloads));
        startActivity(intent);
    }

    public void go_back_to_mainpage(View V){
        finish();
        onBackPressed();
    }

    public void showAd(){
        MobileAds.initialize(this, "ca-app-pub-7067806065281199~9864037146");

        imagelist_ad = (AdView) findViewById(R.id.ad_image_list);
        AdRequest adRequest = new AdRequest.Builder().build();
        imagelist_ad.loadAd(adRequest);
        imagelist_ad.setAdListener(new AdListener() {
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


        imagelist_ad_top = (AdView) findViewById(R.id.ad_image_list_top);
        AdRequest adRequest2 = new AdRequest.Builder().build();
        imagelist_ad_top.loadAd(adRequest2);
        imagelist_ad_top.setAdListener(new AdListener() {
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
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                this.finish();
                return true;

        }
        return super.onOptionsItemSelected(item);
    }
}

