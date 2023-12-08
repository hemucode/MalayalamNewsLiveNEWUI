package com.codehemu.malayalamnewslivetv.services;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import com.codehemu.malayalamnewslivetv.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.HashMap;

public class ShortDataAsync extends AsyncTask<Void , Void, Void> {
    @SuppressLint("StaticFieldLeak")
    Context mContext;
    public ShortDataAsync(Context mContext){
        this.mContext = mContext;
    }
    @Override
    protected Void doInBackground(Void... voids) {
        Document document;
        Element titleE,linksE,thumbnailE;
        String title,links,thumbnail;

        int RssPosition = 0;
        SharedPreferences getShared = mContext.getSharedPreferences("BigBengaliJson", MODE_PRIVATE);
        String JsonValue = getShared.getString("rss","noValue");
        if (!JsonValue.equals("noValue")){
            RssPosition = Integer.parseInt(JsonValue);
        }

        try {
            document = Jsoup.connect(mContext.getResources().getStringArray(R.array.rssURL)[RssPosition]).get();

            JSONArray arr = new JSONArray();
            HashMap<String, JSONObject> map = new HashMap<>();

            for (int i =0; i< 10; i++){
                titleE = document.select("item").select("title").get(i);
                linksE = document.select("item").select("link").get(i);
                thumbnailE = document.select("item").select("media|content").get(i);

                if (titleE!=null && linksE!=null && thumbnailE!=null){
                    title = titleE.text();
                    links = linksE.text();
                    thumbnail = thumbnailE.attr("url");

                    JSONObject json = new JSONObject();
                    json.put("id",i);
                    json.put("title",title);
                    json.put("link",links);
                    json.put("thumbnail",thumbnail);
                    map.put("json" + i, json);
                    arr.put(map.get("json" + i));
                }else {
                    Log.d(TAG, "1onError: RSS Feed Element not Found..");
                }

            }

            SharedPreferences sharedPreferences = mContext.getSharedPreferences("shorts", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("row",arr.toString());
            Log.d(TAG, "1onResponse: " + arr);
            editor.apply();

        } catch (IOException e) {
            Log.d(TAG, "1onError: RSS Feed Url Connect Error =" + e);
        } catch (JSONException e) {
            Log.d(TAG, "1onError: RSS Feed Json Load Error =" + e);

        }
        return null;
    }
}
