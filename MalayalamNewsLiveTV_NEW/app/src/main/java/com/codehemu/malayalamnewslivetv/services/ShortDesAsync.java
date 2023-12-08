package com.codehemu.malayalamnewslivetv.services;

import static android.content.Context.MODE_PRIVATE;
import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.codehemu.malayalamnewslivetv.R;

import android.util.Log;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.HashMap;

public class ShortDesAsync extends AsyncTask<Void , Void, Void> {
    @SuppressLint("StaticFieldLeak")
    Context mContext;
    boolean RssChange = false;
    public ShortDesAsync(Context mContext){
        this.mContext = mContext;
    }

    protected Void doInBackground(Void... voids) {

        SharedPreferences getShared = mContext.getSharedPreferences("shorts", MODE_PRIVATE);

        String JsonValue = getShared.getString("row","noValue");

        String JsonValueEdit = getShared.getString("edit","noValue");

        if (!JsonValue.equals("noValue") && !JsonValueEdit.equals("noValue") ){
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                JSONArray jsonArrayEdit = new JSONArray(JsonValueEdit);
                JSONObject row = jsonArray.getJSONObject(0);
                JSONObject edit = jsonArrayEdit.getJSONObject(0);
                if (!row.getString("title").equals(edit.getString("title"))){
                    RssChange = true;
                }

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }

        if ((JsonValueEdit.equals("noValue") && !JsonValue.equals("noValue")) || RssChange){

            JSONArray jsonArray;
            try {
                jsonArray = new JSONArray(JsonValue);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            JSONArray arr = new JSONArray();
            HashMap<String, JSONObject> map = new HashMap<>();

            String desc;
            Document document;

            for (int i = 0; i < jsonArray.length(); i++){
                try {
                    JSONObject channelData = jsonArray.getJSONObject(i);

                    try {
                        document = Jsoup.connect(channelData.getString("link")).get();
                        Elements rightSec = document.select(".khbr_rght_sec").select("p");
                        Elements container = document.select(".container").select("p");
                        Elements slider_con = document.select(".slider_con").select("p");
                        Elements all_p = document.select("p");
                        if (rightSec.first()!= null){
                            if (rightSec.text().length() > 400){
                                desc = rightSec.text().substring(0,400);
                            }else {
                                desc = rightSec.text();
                            }
                        }else if (container.first()!=null){
                            if (container.text().length() > 400){
                                desc = container.text().substring(0,400);
                            }else {
                                desc = container.text();
                            }
                        }else if (slider_con.first()!=null){
                            if (slider_con.text().length() > 401){
                                desc = slider_con.text().substring(0,400);
                            }else {
                                desc = slider_con.text();
                            }
                        }else if (all_p.first()!=null){
                            if (all_p.text().length() > 400){
                                desc = all_p.text().substring(0,400);
                            }else {
                                desc = all_p.text();
                            }
                        }else {
                            desc = mContext.getString(R.string.sorry_desc);
                        }
                    }
                    catch (IOException e) {
                        continue;
                    }

                    JSONObject json = new JSONObject();

                    json.put("id",i);
                    json.put("title",channelData.getString("title"));
                    json.put("desc",desc);
                    json.put("link",channelData.getString("link"));
                    json.put("thumbnail",channelData.getString("thumbnail"));
                    map.put("json" + i, json);
                    arr.put(map.get("json" + i));
                    Log.d(TAG, "1onErrorResponse: " + channelData.getString("title"));

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("shorts", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("edit",arr.toString());
            editor.apply();

        }
        return null;
    }
}
