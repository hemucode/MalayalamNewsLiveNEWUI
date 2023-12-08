package com.codehemu.malayalamnewslivetv;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;


import android.content.SharedPreferences;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.codehemu.malayalamnewslivetv.adopters.ShortsAdopters;

import com.codehemu.malayalamnewslivetv.models.VerticalViewPager;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;

import java.util.Objects;

public class ShortActivity extends AppCompatActivity {
    ShortsAdopters shortsAdopters;
    ArrayList<String> title = new ArrayList<>();
    ArrayList<String> desc = new ArrayList<>();
    ArrayList<String> image = new ArrayList<>();
    ArrayList<String> link = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_short);
        Objects.requireNonNull(getSupportActionBar()).hide();

        SharedPreferences getShared = getSharedPreferences("shorts", MODE_PRIVATE);
        String JsonValue = getShared.getString("edit","noValue");

        if (!JsonValue.equals("noValue")){
            final VerticalViewPager verticalViewPages = findViewById(R.id.VerticalViewPage);

            shortsAdopters = new ShortsAdopters(ShortActivity.this,title,desc,image,link);
            verticalViewPages.setAdapter(shortsAdopters);
            try {
                JSONArray jsonArray = new JSONArray(JsonValue);
                for (int i = 0; i < jsonArray.length(); i++){
                    JSONObject channelData = jsonArray.getJSONObject(i);
                    title.add(channelData.getString("title"));
                    desc.add(channelData.getString("desc"));
                    image.add(channelData.getString("thumbnail"));
                    link.add(channelData.getString("link"));
                    shortsAdopters.notifyDataSetChanged();
                    Log.d(TAG, "1onErrorResponse: " + channelData.getString("desc"));
                }

            } catch (JSONException e) {
                Log.d(TAG, "1onErrorResponse: " + "channelData.getString()");
                throw new RuntimeException(e);
            }
        }

    }

}