package com.sathyabaman.belikebro;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.TimeZone;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.sathyabaman.belikebro.Comman.RequestExternalResouce;
import com.sathyabaman.belikebro.Comman.Utility;

import org.json.JSONException;
import org.json.JSONObject;

public class FlashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 3000;
    public Context context;


    public String country_code = "";
    public String country_name = "";
    public String city = "";
    public String postal = "";
    public String latitude = "";
    public String longitude = "";
    public String IPv4 = "";
    public String state = "";
    public String remarks = "no_remarks";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash);
        getSupportActionBar().hide();
        context = getApplicationContext();


        putUserLog();
        getTotalCounts();

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                FlashActivity.this.startActivity(new Intent(FlashActivity.this, MainActivity.class));
                FlashActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }



    public void getTotalCounts(){
        if(new Utility().isNetworkAvailable(context)){
            new RequestExternalResouce(context, new Utility().getImageCountURL(), "", "GET", new RequestExternalResouce.OnTaskDoneListener() {
                @Override
                public void onTaskDone(String responseData) {

                    try {
                        JSONObject response  = new JSONObject(responseData);
                        int total_count =   response.getInt("total_count");
                        int first_Image_id =   response.getInt("first_Image_id");
                        int last_Image_id =   response.getInt("last_Image_id");

                        System.out.println("--> TotalCount : "+ total_count);
                        System.out.println("--> first_Image_id : "+ first_Image_id);
                        System.out.println("--> last_Image_id : "+ last_Image_id);

                        SharedPreferences sp = getSharedPreferences("Main_preferences", Activity.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putInt("total_count", total_count);
                        editor.putInt("first_Image_id", first_Image_id);
                        editor.putInt("last_Image_id", last_Image_id);
                        editor.commit();

                    } catch (JSONException e) { e.printStackTrace(); }

                }

                @Override
                public void onError() {
                    System.out.println("Error occurred - no geo request");

                }
            }).execute();
        }
    }



    public void putUserLog(){
        if(new Utility().isNetworkAvailable(context)){
            new RequestExternalResouce(context, new Utility().getGEODetailsUrl(), "", "GET", new RequestExternalResouce.OnTaskDoneListener() {
                @Override
                public void onTaskDone(String responseData) {
                    try {
                        JSONObject response  = new JSONObject(responseData);
                        country_code = response.getString("country_code").toString();
                        country_name = response.getString("country_name").toString();
                        city = response.getString("city").toString();
                        postal = response.getString("postal").toString();
                        latitude = response.getString("latitude").toString();
                        longitude = response.getString("longitude").toString();
                        IPv4 = response.getString("IPv4").toString();
                        state = response.getString("state").toString();
                    } catch (JSONException e) { e.printStackTrace(); }
                    getDeviceDetails();
                }

                @Override
                public void onError() {
                    System.out.println("Error occurred - no geo request");
                    getDeviceDetails();
                }
            }).execute();
        }
    }

    public void  getDeviceDetails(){
        if(new Utility().isNetworkAvailable(context)){
            try {
                String time_zone = TimeZone.getDefault().getDisplayName().toString();
                String versionRelease = android.os.Build.VERSION.RELEASE; // e.g. myVersion := "1.6"
                String version = String.valueOf(android.os.Build.VERSION.SDK_INT);
                String manufacturer = Build.MANUFACTURER;
                String model = Build.MODEL;
                String deviceName = android.os.Build.MODEL;

                JSONObject requestBody = new JSONObject();
                requestBody.put("country_code", country_code);
                requestBody.put("country_name", country_name);
                requestBody.put("city", city);
                requestBody.put("postal", postal);
                requestBody.put("latitude", latitude);
                requestBody.put("longitude", longitude);
                requestBody.put("IPv4", IPv4);
                requestBody.put("time_zone", time_zone);
                requestBody.put("versionRelease", versionRelease);
                requestBody.put("version", version);
                requestBody.put("manufacturer", manufacturer);
                requestBody.put("model", model);
                requestBody.put("name", deviceName);
                requestBody.put("remarks", remarks);

                new RequestExternalResouce(context, new Utility().getAddUserLogUrl(), requestBody.toString(), "POST", new RequestExternalResouce.OnTaskDoneListener() {
                    @Override
                    public void onTaskDone(String responseData) {
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


}
