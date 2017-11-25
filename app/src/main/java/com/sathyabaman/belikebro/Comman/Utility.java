package com.sathyabaman.belikebro.Comman;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by viewqwest on 11/11/2017.
 */

public class Utility {

    public boolean isNetworkAvailable(Context Ctx) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) Ctx.getSystemService(Ctx.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public String getbaseUrl(){
        return "http://178.62.43.112/1facebook_Scraping_belykbro/";
    }

    public String getImageCountURL() {return  "http://178.62.43.112/1facebook_Scraping_belykbro/api_scripts/get_total_image_Details.php"; }

    public String getImageListURL() {return  "http://178.62.43.112/1facebook_Scraping_belykbro/api_scripts/get_imageList.php"; }

    public String getNextPreviousURL(){ return  "http://178.62.43.112/1facebook_Scraping_belykbro/api_scripts/next_previous_image.php"; }

    public String getUpdateImageCountsURL(){ return  "http://178.62.43.112/1facebook_Scraping_belykbro/api_scripts/update_image_counts.php"; }

    public String getGEODetailsUrl(){
        return "https://geoip-db.com/json/";
    }

    public String getAddUserLogUrl(){
        return "http://178.62.43.112/1facebook_Scraping_belykbro/brocallLog.php";
    }


}
